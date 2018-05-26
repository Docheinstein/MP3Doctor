package org.docheinstein.mp3doctor.playlist.provider;

import org.docheinstein.mp3doctor.playlist.Playlist;

/**
 * Represents an entity that is able to provide a playlist (e.g. a controller
 * that embeds a table which items are playlists and is able to return the
 * selected one).
 */
public interface PlaylistProvider {

    /**
     * Returns the playlist provided by this entity.
     * @return the provided playlist
     */
    Playlist getProvidedPlaylist();
}
