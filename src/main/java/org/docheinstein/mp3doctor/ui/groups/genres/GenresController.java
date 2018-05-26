package org.docheinstein.mp3doctor.ui.groups.genres;

import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.ui.groups.SongsGroupsController;

/**
 * Represents a controller that aggregate a list of songs by their genre tag.
 *
 * @see SongsGroupsController
 */
public class GenresController extends SongsGroupsController {

    @Override
    protected String getGroupingField(Song song) {
        return song.getGenre();
    }
}
