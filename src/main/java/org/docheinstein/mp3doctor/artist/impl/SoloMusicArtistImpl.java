package org.docheinstein.mp3doctor.artist.impl;

import org.docheinstein.mp3doctor.artist.base.MusicArtist;
import org.docheinstein.mp3doctor.artist.base.Person;
import org.docheinstein.mp3doctor.artist.base.SoloMusicArtist;

import java.time.LocalDate;

/**
 * Represents a basic implementation of {@link MusicArtistImpl} for a
 * {@link Person}, which let the fields to be get and set and
 * furthermore implements the {@link javafx.beans.Observable} interface
 * which may help to listen to changes of this entity.
 * <p>
 * This actually extends {@link MusicArtistImpl} since most of the
 * fields of this class are part of {@link MusicArtist} and thus shared.
 */
public class SoloMusicArtistImpl extends MusicArtistImpl implements SoloMusicArtist {

    // Person fields

    private String mName;
    private String mSurname;
    private LocalDate mBirthDate;
    private LocalDate mDeathDate;
    private String mNationality;

    public SoloMusicArtistImpl() {}

    // Person methods

    @Override
    public String getName() {
        return mName;
    }

    /**
     * Sets the first name of the person
     * @param name the name
     */
    public void setName(String name) {
        mName = name;
    }

    @Override
    public String getSurname() {
        return mSurname;
    }

    /**
     * Sets the last name of the person.
     * @param surname the surname
     */
    public void setSurname(String surname) {
        mSurname = surname;
    }

    @Override
    public LocalDate getBirthDate() {
        return mBirthDate;
    }

    /**
     * Sets the birth date of the person.
     * @param birthDate the birth date
     */
    public void setBirthDate(LocalDate birthDate) {
        mBirthDate = birthDate;
    }

    @Override
    public LocalDate getDeathDate() {
        return mDeathDate;
    }

    /**
     * Sets the death date of the person.
     * @param deathDate the death date
     */
    public void setDeathDate(LocalDate deathDate) {
        mDeathDate = deathDate;
    }

    @Override
    public String getNationality() {
        return mNationality;
    }

    /**
     * Sets the nationality of the person.
     * @param nationality the nationality
     */
    public void setNationality(String nationality) {
        mNationality = nationality;
    }
}
