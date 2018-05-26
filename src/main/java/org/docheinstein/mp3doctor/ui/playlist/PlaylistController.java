package org.docheinstein.mp3doctor.ui.playlist;

import javafx.scene.control.TableRow;
import org.docheinstein.mp3doctor.playlist.Playlist;
import org.docheinstein.mp3doctor.playlist.provider.PlaylistProvider;
import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.ui.songs.SongsController;

import java.util.List;

/**
 * A custom type of {@link SongsController} that instead of view
 * a song when a row is double clicked plays it (actually notifies the delegate
 * about the fact which should play it).
 */
public class PlaylistController
    extends SongsController
    implements PlaylistProvider {

    /** Shown playlist. */
    private final Playlist mPlaylist;

    /** Delegate that will be notified {@link #onRowDoubleClicked(TableRow)}. */
    protected SongPlayerHandler mPlayerDelegate;

    /**
     * Creates a new playlist controller for the given playlist
     * @param playlist the playlist to show.
     */
    public PlaylistController(Playlist playlist) {
        super(playlist.getList());
        mPlaylist = playlist;
    }

    /**
     * Sets the delegate that will be asked to play a song via
     * {@link SongPlayerHandler#playSong(List, Song)}
     * @param delegate the delegate that is able to play a given song
     */
    public void setPlayerDelegate(SongPlayerHandler delegate) {
        mPlayerDelegate = delegate;
    }

    @Override
    public Playlist getProvidedPlaylist() {
        return mPlaylist;
    }

    @Override
    protected void onRowDoubleClicked(TableRow<Song> row) {
        // Notifies song play / pause action
        if (mPlayerDelegate != null)
            mPlayerDelegate.playSong(uiSongsTable.getItems(), row.getItem());
    }
}
