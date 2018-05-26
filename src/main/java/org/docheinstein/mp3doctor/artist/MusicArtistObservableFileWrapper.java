package org.docheinstein.mp3doctor.artist;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.docheinstein.mp3doctor.artist.base.MusicArtist;
import org.docheinstein.mp3doctor.commons.adt.ObservableFileWrapper;

import java.io.File;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * This entity encapsulate a {@link MusicArtist} in order to detect
 * changes of the artist when it is reinitialize via {@link #set(MusicArtist)}
 * (changes to the instance of artist is listen too since it implements the
 * {@link Observable} interface.
 * <p>
 * Moreover this entity associate the given artist to a file on the disk which
 * may be used for save/load the details of this artist.
 */
public class MusicArtistObservableFileWrapper
    implements ObservableFileWrapper<MusicArtist>, InvalidationListener {

    /** The listeners that listen to changes of the wrapped artist. */
    protected Set<InvalidationListener> mListeners = new CopyOnWriteArraySet<>();

    /** The file on the disk the artist is mapped to. */
    private final File mFile;

    /** The instance of artist this entity wraps. */
    protected MusicArtist mArtist;

    /**
     * Creates a wrapper for the given artist and maps it to the given file.
     * @param artist the artist
     * @param file the fil
     */
    public MusicArtistObservableFileWrapper(MusicArtist artist, File file) {
        mFile = file;
        set(artist, true
            /* Do not notify the changes since the artist is just been set */);
    }

    /**
     * Returns the underlying artist.
     * @return the underlying artist
     */
    @Override
    public MusicArtist get() {
        return mArtist;
    }

    /**
     * Sets the underlying artist, notifying this change to the listener
     * and saving the artist to the disk cache via
     * {@link MusicArtistFactory#saveArtist(MusicArtist, File)}
     * <p>
     * Note that this method should be used only if the instance of artist
     * changes because a different implementation of {@link MusicArtist}
     * is needed, but if the type of artist doesn't change there is no need
     * to use this method, instead changes the fields of the artist itself.

     * @param artist the artist
     */
    @Override
    public void set(MusicArtist artist) {
        set(artist, false /* Notify the change and save to disk */);
    }

    /**
     * Returns the file associated with the mapped artist.
     * <p>
     * Note that the wrapped file is the one this instance of wrapper has
     * been created for. Changes to the underlying artist via {@link #set(MusicArtist)}
     * do not affect the wrapped file, which remains the same.
     * @return the file associated with the mapped artist
     */
    @Override
    public File getWrappedFile() {
        return mFile;
    }

    /**
     * Returns a string that uniquely identify this entity, and in particular
     * the wrapped file (the identifier is actually the filename of the wrapped file).
     * @return the identifier of the wrapped file
     */
    public String getIdentifier() {
        return getWrappedFile().getName();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        if (listener != null)
            mListeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void invalidated(Observable observable) {
        mListeners.forEach(l -> l.invalidated(this));
    }

    /**
     * Sets the underlying artist, notifying this change to the listener
     * and saving the artist to the disk cache via
     * {@link MusicArtistFactory#saveArtist(MusicArtist, File)}
     * <p>
     * Note that this method should be used only if the instance of artist
     * changes because a different implementation of {@link MusicArtist}
     * is needed, but if the type of artist doesn't change there is no need
     * to use this method, instead changes the fields of the artist itself.
     * @param artist the artist
     * @param isASecret whether the change should be notified to the listeners
     *                  and the artist should be save do the disk cache
     */
    private void set(MusicArtist artist, boolean isASecret) {
        if (mArtist != null)
            mArtist.removeListener(this);
        mArtist = artist;

        if (!isASecret) {
            MusicArtistFactory.saveArtist(mArtist, mFile);
            invalidated(this);
        }

        if (mArtist != null)
            mArtist.addListener(this);
    }
}
