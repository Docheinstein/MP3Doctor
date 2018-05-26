package org.docheinstein.mp3doctor.cache;

import org.docheinstein.mp3doctor.commons.constants.Config;
import org.docheinstein.mp3doctor.commons.hierarchy.DEntry;
import org.docheinstein.mp3doctor.commons.hierarchy.FEntry;

/**
 * Manager of the disk's files this application use as cache.
 * <p>
 * This can be used for easily retrieve the cache files.
 */
public class CacheHierarchy {

    /** Unique instance of this class. */
    private static final CacheHierarchy INSTANCE = new CacheHierarchy();

    /** Cache file entry for the songs. */
    private final FEntry mSongsEntry = new FEntry(Config.Paths.Cache.SONGS_FOLDER_NAME);

    /** Cache directory entry for the playlists. */
    private final DEntry mPlaylistsEntry = new DEntry(Config.Paths.Cache.PLAYLISTS_FOLDER_NAME);

    /** Cache directory entry for the artists. */
    private final DEntry mArtistsEntry = new DEntry(Config.Paths.Cache.ARTISTS_FOLDER_NAME);

    /** The root of the cache of the disk cache. */
    private final DEntry mRoot = DEntry.createNormalizedHierarchyForPath(
        Config.Paths.Cache.ROOT_PATH, Config.Paths.Cache.ROOT_FOLDER_NAME,
        mSongsEntry,
        mPlaylistsEntry,
        mArtistsEntry
    );

    /**
     * Returns the unique instance of this class.
     * @return the unique instance of this class
     */
    public static CacheHierarchy instance() {
        return INSTANCE;
    }

    /**
     * Returns the root of the cache of the disk cache.
     * @return the root of the cache of the disk cache
     */
    public DEntry getRoot() {
        return mRoot;
    }

    /**
     * Returns the songs entry of the disk cache.
     * @return the songs entry of the disk cache.
     */
    public FEntry getSongsEntry() {
        return mSongsEntry;
    }

    /**
     * Returns the playlists entry of the disk cache.
     * @return the playlists entry of the disk cache.
     */
    public DEntry getPlaylistsEntry() {
        return mPlaylistsEntry;
    }

    /**
     * Returns the artists entry of the disk cache.
     * @return the artists entry of the disk cache.
     */
    public DEntry getArtistsEntry() {
        return mArtistsEntry;
    }
}
