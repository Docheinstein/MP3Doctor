package org.docheinstein.mp3doctor.song.provider;

import org.docheinstein.mp3doctor.song.Song;

/**
 * Represents an entity that is able to provide a song (e.g. a controller
 * that embeds a table which items are songs and is able to return the
 * selected one).
 */
public interface SongProvider {

    /**
     * Returns the song provided by this entity.
     * @return the provided song
     */
    Song getProvidedSong();
}