package org.docheinstein.mp3doctor.artist.base;

import java.time.LocalDate;

/** Represents a group of person. */
public interface Group {

    /**
     * Returns the date in which the group has been created.
     * @return the creation date
     */
    LocalDate getCreationDate();

    /**
     * Returns the date in which the group has broken up.
     * @return the disband date
     */
    LocalDate getDisbandDate();

    /**
     * Returns the members of the group.
     * @return the members of the group
     */
    // I opted for treat the members as Person(s) instead of as SoloMusicArtist(s)
    // since this class represents a generic group, not a specifically group
    // of artist or music artist
    Person[] getMembers();
}
