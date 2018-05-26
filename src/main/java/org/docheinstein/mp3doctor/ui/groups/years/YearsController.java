package org.docheinstein.mp3doctor.ui.groups.years;

import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.ui.groups.SongsGroupsController;

/**
 * Represents a controller that aggregate a list of songs by their year tag.
 *
 * @see SongsGroupsController
 */
public class YearsController extends SongsGroupsController {

    @Override
    protected String getGroupingField(Song song) {
        return song.getYear();
    }
}
