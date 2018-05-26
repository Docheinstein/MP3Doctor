package org.docheinstein.mp3doctor.song;

import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.cache.CacheHierarchy;
import org.docheinstein.mp3doctor.commons.adt.ObservableFileListManager;
import org.docheinstein.mp3doctor.commons.adt.ObservableListManager;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.commons.utils.CollectionsUtil;
import org.docheinstein.mp3doctor.commons.utils.FileUtil;
import org.docheinstein.mp3doctor.ui.commons.alert.AlertInstance;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * Manager responsible for keep in memory the songs imported in the library.
 * <p>
 * Since this manager is a {@link ObservableFileListManager}, it handles the
 * changes to the songs by save them to the disk and load the saved files
 * when needed.
 */
public class SongsManager extends ObservableListManager<Song> {

    private static final Logger L = Logger.createForClass(SongsManager.class);

    /** Unique instance of this class. */
    private static final SongsManager INSTANCE = new SongsManager();

    /**
     * Returns the unique instance of this class.
     * @return the unique instance of this class.
     */
    public static SongsManager instance() {
        return INSTANCE;
    }

    /** Creates a new songs manager. */
    private SongsManager() {
        super();
        loadSongsFromCache();
    }

    @Override
    public void onElementsAdded(List<? extends Song> songs) {
        L.verbose("Songs have been added to the manager, saving to cache");
        saveSongsToCache();
    }

    @Override
    public void onElementsRemoved(List<? extends Song> songs) {
        L.verbose("Songs have been removed from the manager, saving to cache");
        saveSongsToCache();
    }

    @Override
    public void onElementsChanged(List<? extends Song> songs) {
        L.verbose("Songs have been updated in the manager, but nothing to do");
    }

    /**
     * Adds a single song to the library for the given file.
     * @param file the file of the song to add

     * @see #addFromFiles(File[])
     * @see #addFromFiles(Collection)
     */
    public void addFromFile(File file) {
        Asserts.assertNotNull(file, "Can't import null file", L);
        addFromFiles(new File[] { file });
    }

    /**
     * Adds multiple songs to the library for the given files.
     * @param files the files of the songs to add
     *
     * @see #addFromFiles(File[])
     */
    public void addFromFiles(Collection<File> files) {
        Asserts.assertNotNull(files, "Can't import null files", L);
        addFromFiles(files.toArray(new File[0]));
    }

    /**
     * Adds multiple songs to the library for the given files.
     * @param files the files of the songs to add
     *
     * @see #addFromFiles(Collection)
     */
    public void addFromFiles(File[] files) {
        Asserts.assertNotNull(files, "Can't import null files", L);
        L.debug("Importing songs from files");

        // The songs are added all at once instead of adding them one by one
        // in order to fire the onElementsAdded callback just once.

        List<Song> newSongs = createSongsFromMP3Files(files);
        CollectionsUtil.addIfNotNull(mList, newSongs);

        // Loads the tags for the added songs
        loadSongsTagsAsync(newSongs);

        // Duplicates are not allowed in the library
        int listSize = mList.size();
        CollectionsUtil.sortAndRemoveDuplicates(mList, Song::compareTo);

        int duplicatedSongCount = listSize - mList.size();
        if (duplicatedSongCount > 0) {
            L.warn("Duplicated songs have been found, those have been skipped");
            AlertInstance.DuplicatedSongs.show(duplicatedSongCount);
        }
    }

    /**
     * Creates a list of songs for the given files.
     * @param files the songs files
     * @return a list that contains a song for each file in the given array
     */
    private static List<Song> createSongsFromMP3Files(File[] files) {
        Asserts.assertNotNull(files, "Can't import null files", L);

        List<Song> songsInPath = new ArrayList<>();

        for (File f : files) {
            if (f.isDirectory()) {
                L.verbose("Found an inner directory, entering it recursively: "
                    + f.getAbsolutePath());
                File[] innerFiles = f.listFiles();

                if (innerFiles == null) {
                    L.warn("Inner directory returned null file list, skipping it");
                    continue;
                }

                // Retrieve the song list for the nested dir and add it the the
                // list of songs in this path
                CollectionsUtil.addIfNotNull(songsInPath,
                    createSongsFromMP3Files(innerFiles));
            } else {
                // Add the single song to the list of songs in this path
                CollectionsUtil.addIfNotNull(songsInPath,
                    createSongFromMP3File(f));
            }
        }

        return songsInPath;
    }

    /**
     * Creates a song for the given file
     * @param f the file
     * @return the song that wraps the given file
     */
    private static Song createSongFromMP3File(File f) {
        Asserts.assertNotNull(f, "Can't create song for a null file", L);

        // Not an assert, just skip this
        if (!f.exists()) {
            L.warn("Can't create a song for a non existing file: " + f.getName());
            return null;
        }

        String extension = FileUtil.getFileExtension(f);

        if (!extension.equals("mp3")) {
            L.warn("Creation of non MP3 not allowed: " + f.getName());
            L.verbose("Extension was:" + extension);
            return null;
        }

        // Do not load the tags, this will be done asynchronously by the manager
        return new Song(f, false);
    }

    /**
     * Loads the songs from the disk to the in memory list.
     */
    private void loadSongsFromCache() {
        L.debug("Loading song list from cache");
        Scanner cacheScanner = FileUtil.getScanner(
            CacheHierarchy.instance().getSongsEntry().getWrappedFile());

        if (cacheScanner == null)
            return;

        List<File> cachedSongsFiles = new ArrayList<>();

        while (cacheScanner.hasNext())
            cachedSongsFiles.add(new File(cacheScanner.nextLine()));

        // Add all the songs at once
        addFromFiles(cachedSongsFiles);

        cacheScanner.close();
    }

    /**
     * Saves the song list to the disk.
     */
    private void saveSongsToCache() {
        L.info("Saving song list from library to cache");
        PrintWriter cacheWriter = FileUtil.getWriter(
            CacheHierarchy.instance().getSongsEntry().getWrappedFile());

        if (cacheWriter == null)
            return;

        mList.forEach(s -> cacheWriter.println(s.getPath()));

        cacheWriter.close();
    }

    /**
     * Starts a thread that asynchronously load the tags for each given song.
     * @param songForWhichLoadTags the songs which tags have to be loaded
     */
    private void loadSongsTagsAsync(List<Song> songForWhichLoadTags) {
        if (CollectionsUtil.isFilled(songForWhichLoadTags))
            new Thread(() -> {
                L.debug("BEGIN - Loading songs tags async");
                songForWhichLoadTags.forEach(Song::loadTags);
                L.debug("END - Loading songs tags async");
            }).start();
    }
}
