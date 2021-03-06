package org.docheinstein.mp3doctor.song.provider;

import org.docheinstein.mp3doctor.song.Song;

import java.util.List;

/**
 * Represents an entity that is able to provide a list of songs (e.g. a controller
 * that embeds a table which items are songs and is able to return the
 * selected songs).
 * <p>
 * Actually this interface does exactly the same as {@link SongListProvider},
 * but since the purpose of the two are different and the two can be used
 * together by the same entity with a different implementation, the two doesn't
 * share the methods.
 * @see SongListProvider
 */
public interface SongSelectionProvider {

    /**
     * Returns the song list provided by this entity.
     * @return the provided song list
     */
    List<Song> getProvidedSongSelection();
}
