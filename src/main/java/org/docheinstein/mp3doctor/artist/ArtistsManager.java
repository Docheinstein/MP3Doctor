package org.docheinstein.mp3doctor.artist;

import org.docheinstein.mp3doctor.cache.CacheHierarchy;
import org.docheinstein.mp3doctor.commons.adt.ObservableFileListManager;

import java.io.File;

/**
 * Manager responsible for keep in memory the details of the existing
 * artist on the disk.
 * <p>
 * Since this manager is a {@link ObservableFileListManager}, it handles the
 * changes to the artists by save their details to the disk and load
 * the saved files when needed.
 */
public class ArtistsManager
    extends ObservableFileListManager<MusicArtistObservableFileWrapper> {

    /** The unique instance of this class. */
    private static final ArtistsManager INSTANCE = new ArtistsManager();

    /**
     * Returns the unique instance of this class.
     * @return the unique instance of this class
     */
    public static ArtistsManager instance() {
        return INSTANCE;
    }

    @Override
    public File getBaseDirectory() {
        return CacheHierarchy.instance().getArtistsEntry().getWrappedFile();
    }

    @Override
    public MusicArtistObservableFileWrapper newFileWrapper(File f) {
        return new MusicArtistObservableFileWrapper(
            MusicArtistFactory.createArtist(f), f);
    }
}
