package org.docheinstein.mp3doctor.playlist;

import org.docheinstein.mp3doctor.cache.CacheHierarchy;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.adt.ObservableFileListManager;

import java.io.File;

/**
 * Manager responsible for keep in memory the existing playlists on the disk.
 * <p>
 * Since this manager is a {@link ObservableFileListManager}, it handles the
 * changes to the playlists by save them to the disk and load
 * the saved files when needed.
 */
public class PlaylistsManager extends ObservableFileListManager<Playlist> {


    private static final Logger L =
        Logger.createForClass(PlaylistsManager.class);

    /** The unique instance of this class. */
    private static final PlaylistsManager INSTANCE = new PlaylistsManager();

    /**
     * Returns the unique instance of this class.
     * @return the unique instance of this class
     */
    public static PlaylistsManager instance() {
        return INSTANCE;
    }

    @Override
    public File getBaseDirectory() {
        return CacheHierarchy.instance().getPlaylistsEntry().getWrappedFile();
    }

    @Override
    public Playlist newFileWrapper(File f) {
        return new Playlist(f);
    }
}
