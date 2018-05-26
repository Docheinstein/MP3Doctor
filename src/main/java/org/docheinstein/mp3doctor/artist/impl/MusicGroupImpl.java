package org.docheinstein.mp3doctor.artist.impl;

import org.docheinstein.mp3doctor.artist.base.Group;
import org.docheinstein.mp3doctor.artist.base.MusicArtist;
import org.docheinstein.mp3doctor.artist.base.MusicGroup;
import org.docheinstein.mp3doctor.artist.base.Person;

import java.time.LocalDate;

/**
 * Represents a basic implementation of {@link MusicArtistImpl} for a
 * {@link Group}, which let the fields to be get and set and
 * furthermore implements the {@link javafx.beans.Observable} interface
 * which may help to listen to changes of this entity.
 * <p>
 * This actually extends {@link MusicArtistImpl} since most of the
 * fields of this class are part of {@link MusicArtist} and thus shared.
 */
public class MusicGroupImpl extends MusicArtistImpl implements MusicGroup {

    // Music group fields

    private LocalDate mCreationDate;
    private LocalDate mDisbandDate;
    private Person[] mArtists;

    // Music group methods

    @Override
    public LocalDate getCreationDate() {
        return mCreationDate;
    }

    /**
     * Sets the creation date of the music group, i.e. when the group was born.
     * @param creationDate the creation date
     */
    public void setCreationDate(LocalDate creationDate) {
        mCreationDate = creationDate;
    }

    @Override
    public LocalDate getDisbandDate() {
        return mDisbandDate;
    }

    /**
     * Sets the disband date of the music, i.e. when the group has broken up.
     * @param disbandDate the disband date
     */
    public void setDisbandDate(LocalDate disbandDate) {
        mDisbandDate = disbandDate;
    }

    @Override
    public Person[] getMembers() {
        return mArtists;
    }

    /**
     * Sets the members of the group.
     * @param members the members
     */
    public void setMembers(Person[] members) {
        mArtists = members;
    }
}
