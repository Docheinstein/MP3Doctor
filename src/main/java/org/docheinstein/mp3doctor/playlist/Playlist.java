package org.docheinstein.mp3doctor.playlist;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.adt.FileWrapper;
import org.docheinstein.mp3doctor.commons.adt.ObservableListListener;
import org.docheinstein.mp3doctor.commons.adt.ObservableListManager;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.commons.utils.CollectionsUtil;
import org.docheinstein.mp3doctor.commons.utils.FileUtil;
import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.song.SongsManager;
import org.docheinstein.mp3doctor.ui.commons.alert.AlertInstance;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * Represents a playlist, which essentially is a list of song with a name.
 * <p>
 * This class implements the {@link Observable} interface which makes it easy
 * to be listened for changes and the {@link FileWrapper} interface since a
 * playlist is mapped to a custom file on the disk.
 */
public class Playlist
    extends ObservableListManager<Song>
    implements Observable, FileWrapper {

    private static final Logger L = Logger.createForClass(Playlist.class);

    /** Name of the playlist. */
    private final String mName;

    /** File wrapped by this playlist. */
    private final File mFile;

    /** Listeners of the changes of the playlist. */
    private final Set<InvalidationListener> mListeners = new CopyOnWriteArraySet<>();

    /**
     * Creates a new playlist and maps it to a custom file on the disk.
     * @param playlistFile the underlying file where the playlist details should
     *                     be saved to
     */
    public Playlist(File playlistFile) {
        // Observe the underlying list after the list has been loaded
        super(false);

        Asserts.assertNotNull(playlistFile, "Can't create a playlist for a null file");

        mFile = playlistFile;
        mName = FileUtil.getFileNameWithoutExtension(mFile);

        loadPlaylistFromCache();

        // Already done in loadPlaylistFromCache
        // startListObservation();

        SongsManager.instance().addListener(new ObservableListListener<Song>() {
            @Override
            public void onElementsChanged(List<? extends Song> songs) {}

            @Override
            public void onElementsAdded(List<? extends Song> songs) {}

            @Override
            public void onElementsRemoved(List<? extends Song> songs) {
                // Ensure that the songs in the playlist exist in the library
                // by reloading the playlist
                L.debug("Some songs in the SongsManager have been removed:" +
                    " the playlist is going to be reloaded from its file");
                loadPlaylistFromCache();
            }
        });
    }

    /**
     * Returns the name of the playlist (which actually is an identifier of the
     * playlist.
     * @return the name of the playlist
     */
    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public File getWrappedFile() {
        return mFile;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        if (listener != null)
            mListeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void onElementsAdded(List<? extends Song> songs) {
        L.debug("Detected playlist change: added songs. Removing duplicates and saving to cache");

        // Modification to the list can't be done on change, these must be done
        // using Platform.runLater: https://bugs.openjdk.java.net/browse/JDK-8088022
        Platform.runLater(() -> {
            int listSize = mList.size();
            CollectionsUtil.sortAndRemoveDuplicates(mList, Song::compareTo);
            int duplicatedSongCount = listSize - mList.size();
            if (duplicatedSongCount > 0) {
                L.warn("Duplicated songs have been found, those have been skipped");
                AlertInstance.DuplicatedSongs.show(duplicatedSongCount);
            }

            savePlaylistToCache();
            mListeners.forEach(l -> l.invalidated(this));
        });
    }

    @Override
    public void onElementsRemoved(List<? extends Song> songs) {
        L.debug("Detected playlist change: removed songs. Saving to cache");
        savePlaylistToCache();
        mListeners.forEach(l -> l.invalidated(this));
    }

    @Override
    public void onElementsChanged(List<? extends Song> songs) {
        L.debug("Detected playlist change: modified songs. Doing nothing");
    }

    /**
     * Loads the playlist's songs from the cache by
     * parsing the underlying file and adding a song for each entry.
     * <p>
     * Pay attention: the songs must exist in the current songs library.
     */
    private void loadPlaylistFromCache() {
        L.info("Loading playlist from file: " + mFile.getPath());

        if (!mFile.isFile()) {
            L.error("Playlist file: " + mFile.getName() + " is not a simple file");
            return;
        }

        if (!mFile.exists()) {
            L.error("Playlist file: " + mFile.getName() + " doesn't exists");
            return;
        }

        Scanner scanner = FileUtil.getScanner(mFile);

        if (scanner == null) {
            L.error("Can't initialize a valid scanner for file: " + mFile.getName());
            return;
        }

        List<File> playlistSongList = new ArrayList<>();

        while (scanner.hasNext()) {
            String songPath = scanner.nextLine();
            L.debug("Found song " + songPath + " for playlist " + mName);
            playlistSongList.add(new File(songPath));
        }

        // Only the songs in library can be added
        List<Song> validSongs = SongsManager.instance().getList()
            .stream()
            .filter(librarySong -> {
                for (File playlistSong : playlistSongList)
                    if (playlistSong.getName().equals(librarySong.getFilename()))
                        return true;
                return false;
            })
            .collect(Collectors.toList());

        scanner.close();

        // The addition of songs from this loader should not be notified,
        // suspended till the end of the method
        stopListObservation();

        mList.clear();
        mList.addAll(validSongs);

        // Clear playlist file from zombies
        if (validSongs.size() != playlistSongList.size()) {
            L.warn("SKipping zombie songs found in the playlist file but not in the " +
                "library anymore, # " + (playlistSongList.size() - validSongs.size()));
            L.debug("Saving playlist again without the zombies");
            savePlaylistToCache();
        }

        // Continue to listen the songs list
        startListObservation();
    }

    /**
     * Saves the playlist's song to the cache, writing an entry for each song.
     */
    private void savePlaylistToCache() {
        L.info("Saving playlist to file: " + mFile.getPath());

        if (!mFile.isFile()) {
            L.error("Playlist file: " + mFile.getName() + " is not a simple file");
            return;
        }

        if (!mFile.exists()) {
            L.error("Playlist file: " + mFile.getName() + " doesn't exists");
            return;
        }

        PrintWriter writer = FileUtil.getWriter(mFile);

        if (writer == null) {
            L.error("Can't initialize a valid scanner for file: " + mFile.getName());
            return;
        }

        L.debug("New playlist size: " + mList.size());

        mList.forEach(s -> writer.println(s.getPath()));

        writer.close();
    }

}
