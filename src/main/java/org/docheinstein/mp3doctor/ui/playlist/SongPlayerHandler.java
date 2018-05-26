package org.docheinstein.mp3doctor.ui.playlist;

import org.docheinstein.mp3doctor.song.Song;

import java.util.List;

/** Represents and handler that can be asked to play a song within a queue. */
public interface SongPlayerHandler {

    /**
     * Requires this handler to play the given song queue, and in particular
     * the given song as the first song to play.
     * @param songQueue the song queue
     * @param song the first song to play, which must be within the song queue
     */
    void playSong(List<Song> songQueue, Song song);
}