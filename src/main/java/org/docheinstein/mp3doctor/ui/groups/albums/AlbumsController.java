package org.docheinstein.mp3doctor.ui.groups.albums;

import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.ui.groups.SongsGroupsController;

/**
 * Represents a controller that aggregate a list of songs by their album tag.
 *
 * @see SongsGroupsController
 */
public class AlbumsController extends SongsGroupsController {

    @Override
    protected String getGroupingField(Song song) {
        return song.getAlbum();
    }
}
