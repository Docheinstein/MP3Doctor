package org.docheinstein.mp3doctor.song.player;

import org.docheinstein.mp3doctor.song.Song;

import java.util.List;

/**
 * Represents an entity that can provide both a song
 * and song queue that can be played (for instance,
 * via {@link SongPlayer}
 * <p>
 * Note that the provided song should be between the elements
 * of the song queue.
 */
public interface PlayableSongQueueProvider extends PlayableSongProvider {

    /**
     * Returns the song queue this entity provide to be played.
     * @return a playable song queue
     */
    List<Song> getSongQueueToPlay();
}
