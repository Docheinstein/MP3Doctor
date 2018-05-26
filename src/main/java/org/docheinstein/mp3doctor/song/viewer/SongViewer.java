package org.docheinstein.mp3doctor.song.viewer;

import org.docheinstein.mp3doctor.song.Song;

/**
 * Represents an entity that is able to show a song in a sense that depends
 * on the implementation of this entity.
 */
public interface SongViewer {

    /**
     * Performs the action of viewer the given song.
     * @param song the song to viewer
     */
    void viewSong(Song song);
}
