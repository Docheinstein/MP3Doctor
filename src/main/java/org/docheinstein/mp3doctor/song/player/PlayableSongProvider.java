package org.docheinstein.mp3doctor.song.player;

import org.docheinstein.mp3doctor.song.Song;

/** Represents an entity that can provide a song that can be played. */
public interface PlayableSongProvider {

    /**
     * Returns the song this entity provide to be played.
     * @return a playable song
     */
    Song getSongToPlay();
}
