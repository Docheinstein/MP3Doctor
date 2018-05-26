package org.docheinstein.mp3doctor.artist.impl;

import javafx.beans.InvalidationListener;
import org.docheinstein.mp3doctor.artist.base.MusicArtist;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Represents an abstract implementation of {@link MusicArtist} which
 * let the fields to be get and set and furthermore implements the
 * {@link javafx.beans.Observable} interface which may help to listen
 * to changes of this entity.
 */
public abstract class MusicArtistImpl implements MusicArtist {

    // Artist fields

    protected String mStageName;
    protected String mBiography;
    protected LocalDate mDebutDate;

    // Music artist fields

    protected String[] mPreferredGenres;
    protected MusicArtist.Disk[] mDiscography;
    protected String mFanPage;

    protected Set<InvalidationListener> mListeners = new CopyOnWriteArraySet<>();

    public MusicArtistImpl() {}

    // Artist methods

    @Override
    public String getStageName() {
        return mStageName;
    }

    /**
     * Sets the stage name of the artist.
     * @param stageName the stage name
     */
    public void setStageName(String stageName) {
        mStageName = stageName;
    }

    @Override
    public String getBiography() {
        return mBiography;
    }

    /**
     * Sets the biography of the artist
     * @param biography the biography
     */
    public void setBiography(String biography) {
        mBiography = biography;
    }

    @Override
    public LocalDate getDebutDate() {
        return mDebutDate;
    }

    /**
     * Sets the debut date of the artist
     * @param debutDate the debut date
     */
    public void setDebutDate(LocalDate debutDate) {
        mDebutDate = debutDate;
    }

    // MusicArtist methods

    @Override
    public String[] getPreferredGenres() {
        return mPreferredGenres;
    }

    /**
     * Sets the preferred genres of the music artist
     * @param preferredGenres the preferred genres
     */
    public void setPreferredGenres(String[] preferredGenres) {
        mPreferredGenres = preferredGenres;
    }

    @Override
    public MusicArtist.Disk[] getDiscography() {
        return mDiscography;
    }

    /**
     * Sets the discography of the music artist
     * @param discography the discography
     */
    public void setDiscography(MusicArtist.Disk[] discography) {
        mDiscography = discography;
    }

    @Override
    public String getFanPage() {
        return mFanPage;
    }

    /**
     * Sets the fan page of the music artist.
     * @param fanPage the fan page
     */
    public void setFanPage(String fanPage) {
        mFanPage = fanPage;
    }

    // Observable

    @Override
    public void addListener(InvalidationListener listener) {
        if (listener != null)
            mListeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        mListeners.remove(listener);
    }

}
