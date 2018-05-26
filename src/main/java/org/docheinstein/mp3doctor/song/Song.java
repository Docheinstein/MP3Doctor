package org.docheinstein.mp3doctor.song;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.adt.FileWrapper;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.commons.utils.FXUtil;
import org.docheinstein.mp3doctor.commons.utils.StringUtil;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.*;
import org.jaudiotagger.tag.images.Artwork;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Represents a song, which wraps an .mp3 file on the disk and expose
 * its tags.
 * <p>
 * This class implements the {@link Observable} interface which makes it easy
 * to be listened for changes to the tags.
 */
public class Song implements Comparable<Song>, Observable, FileWrapper {

    private static final Logger L = Logger.createForClass(Song.class);

    // Meta details

    /** Underlying song's file. */
    private final File mFile;

    /** Container of the song tags. */
    private AudioFile mAudioFile;

    /** Filename of the song. */
    private final StringProperty mFilenameProperty;

    /** Path of the song on the disk. */
    private final StringProperty mPathProperty;

    // Song's tags

    // Those are kept as Property(s) for make listen to to changes
    // possible (this is convenient for the structures that use
    // the observable values to be up to date, such as JavaFX TableView)

    // Read/write tags

    /** Artist tag. */
    private final StringProperty mArtistProperty;

    /** Title tag. */
    private final StringProperty mTitleProperty;

    /** Album tag. */
    private final StringProperty mAlbumProperty;

    /** Year tag. */
    private final StringProperty mYearProperty;

    /** Genre tag. */
    private final StringProperty mGenreProperty;

    /** Track number tag. */
    private final StringProperty mTrackNumberProperty;

    /** Lyrics tag. */
    private final StringProperty mLyricsProperty;

    /** Cover. */
    private Artwork mCover;

    // Read only header tags

    /** Bit rate header. */
    private final StringProperty mBitRateProperty;

    /** Sample rate header. */
    private final StringProperty mSampleRateProperty;

    /** Channels header. */
    private final StringProperty mChannelsProperty;

    /** Listeners that listen to the changes to the song (tags). */
    private final Set<InvalidationListener> mListeners = new CopyOnWriteArraySet<>();

    /**
     * Creates a song for the given underling file
     * and optionally load its tags synchronously.
     * <p>
     * The tags can be alternatively via {@link #loadTags()}.
     * @param file the song file
     * @param loadTags whether the tags should be loaded synchronously
     *
     * @see #Song(File)
     */
    public Song(File file, boolean loadTags) {
        mFile = file;

        mFilenameProperty =  new SimpleStringProperty(file.getName());
        mPathProperty = new SimpleStringProperty(file.getAbsolutePath());

        mArtistProperty = new SimpleStringProperty();
        mTitleProperty = new SimpleStringProperty();
        mAlbumProperty = new SimpleStringProperty();
        mYearProperty = new SimpleStringProperty();
        mGenreProperty = new SimpleStringProperty();
        mTrackNumberProperty = new SimpleStringProperty();
        mLyricsProperty = new SimpleStringProperty();

        mBitRateProperty = new SimpleStringProperty();
        mSampleRateProperty = new SimpleStringProperty();
        mChannelsProperty = new SimpleStringProperty();

        if (loadTags)
            loadTags();
    }

    /**
     * Creates a song for the given underlying file without load the tags.
     * <p>
     * The tags can be loaded via {@link #loadTags()}.
     * @param file the song file
     *
     * @see #Song(File, boolean)
     */
    public Song(File file) {
        this(file, false);
    }

    @Override
    public String toString() {
        return getFilename();
    }

    @Override
    public int compareTo(Song s) {
        return getPath().compareTo(s.getPath());
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

    // Meta details


    /**
     * Returns the underlying file of this song.
     * @return the file of this song
     */
    @Override
    public File getWrappedFile() {
        return mFile;
    }

    /**
     * Returns the filename of the song.
     * @return the filename of the song
     */
    public String getFilename() {
        return mFilenameProperty.get();
    }

    /**
     * Returns the filename of the song as an observable property.
     * @return the filename property
     */
    public StringProperty getFilenameProperty() {
        return mFilenameProperty;
    }

    /**
     * Returns the path of the underlying song file.
     * @return the path of the song
     */
    public String getPath() {
        return mPathProperty.get();
    }

    /**
     * Returns the path of the song as an observable property.
     * @return the path property
     */
    public StringProperty getPathProperty() {
        return mPathProperty;
    }

    // Read/write tags

    /**
     * Returns the artist tag of the song.
     * @return the artist
     */
    public String getArtist() {
        return mArtistProperty.get();
    }

    /**
     * Returns the artist tag of the song as an observable property.
     * @return the artist property
     */
    public StringProperty getArtistProperty() {
        return mArtistProperty;
    }

    /**
     * Returns the title tag of the song
     * @return the title
     */
    public String getTitle() {
        return mTitleProperty.get();
    }

    /**
     * Returns the title tag of the song as an observable property
     * @return the title property
     */
    public StringProperty getTitleProperty() {
        return mTitleProperty;
    }

    /**
     * Returns the album tag of the song
     * @return the album
     */
    public String getAlbum() {
        return mAlbumProperty.get();
    }

    /**
     * Returns the album tag of the song as an observable property
     * @return the album property
     */
    public StringProperty getAlbumProperty() {
        return mAlbumProperty;
    }

    /**
     * Returns the year tag of the song
     * @return the year
     */
    public String getYear() {
        return mYearProperty.get();
    }

    /**
     * Returns the year tag of the song as an observable property
     * @return the year property
     */
    public StringProperty getYearProperty() {
        return mYearProperty;
    }

    /**
     * Returns the genre tag of the song
     * @return the genre
     */
    public String getGenre() {
        return mGenreProperty.get();
    }

    /**
     * Returns the genre tag of the song as an observable property
     * @return the genre property
     */
    public StringProperty getGenreProperty() {
        return mGenreProperty;
    }

    /**
     * Returns the track number tag of the song
     * @return the track number
     */
    public String getTrackNumber() {
        return mTrackNumberProperty.get();
    }

    /**
     * Returns the track number tag of the song as an observable property
     * @return the track number property
     */
    public StringProperty getTrackNumberProperty() {
        return mTrackNumberProperty;
    }

    /**
     * Returns the lyrics tag of the song
     * @return the lyrics number
     */
    public String getLyrics() {
        return mLyricsProperty.get();
    }

    /**
     * Returns the lyrics tag of the song as an observable property
     * @return the lyrics property
     */
    public StringProperty getLyricsProperty() {
        return mLyricsProperty;
    }

    /**
     * Returns the cover of the song
     * @return the cover
     */
    public Artwork getCover() {
        return mCover;
    }

    /**
     * Returns the cover of the song as an {@link Image}
     * @return the cover as a JavaFX image
     */
    public Image getCoverAsImage() {
        try {
            return mCover == null ? null :
                FXUtil.awtBufferedImageToImage((BufferedImage) mCover.getImage());
        } catch (IOException e) {
            L.error("Error occurred during conversion from artwork to JFX image");
            return null;
        }
    }

    // Read only header details

    /**
     * Returns the bit rate header of the song
     * @return the bit rate
     */
    public String getBitRate() {
        return mBitRateProperty.get();
    }

    /**
     * Returns the bit rate header of the song as an observable property
     * @return the bit rate property
     */
    public StringProperty getBitRateProperty() {
        return mBitRateProperty;
    }

    /**
     * Returns the sample rate header of the song
     * @return the sample rate
     */
    public String getSampleRate() {
        return mSampleRateProperty.get();
    }

    /**
     * Returns the sample rate header of the song as an observable property
     * @return the sample rate property
     */
    public StringProperty getSampleRateProperty() {
        return mSampleRateProperty;
    }

    /**
     * Returns the channels header of the song
     * @return the channels
     */
    public String getChannels() {
        return mChannelsProperty.get();
    }

    /**
     * Returns the channels header of the song as an observable property
     * @return the channels property
     */
    public StringProperty getChannelsProperty() {
        return mChannelsProperty;
    }

    /**
     * Writes a bunch of tags to the underlying file of this song and optionally
     * commit those to the file (it may be useful to use 'false' for the commit
     * parameter when multiple overwrite have to be made to the songs and not
     * only to the tags (e.g. overwrite both the tags and the cover) and
     * the commit have to be done only once).
     * @param tags the tags to overwrite and their values
     * @param commit whether the changes have to be committed with this overwrite
     *
     * @see #overwriteCover(Artwork, boolean)
     * @see #overwriteTagsAndCover(Map, Artwork, boolean)
     *
     * @return false if the tags commit has been requested and failed
     */
    public boolean overwriteTags(Map<FieldKey, String> tags, boolean commit) {
        L.debug("Overwriting tags for song " + this +
                " - Committing: " + commit);
        Asserts.assertNotNull(tags, "Can't tolerate a null tags map", L);

        Tag audioTag = mAudioFile.getTag();

        for (Map.Entry<FieldKey, String> tag : tags.entrySet()) {
            try {
                Asserts.assertNotNull(tag.getKey(), "Can't set a null field key", L);

                // Set the field if it contains something
                if (StringUtil.isValid(tag.getValue()))
                    audioTag.setField(tag.getKey(), tag.getValue());
                // Delete the field if it contains null or an empty string
                else
                    audioTag.deleteField(tag.getKey());
            } catch (FieldDataInvalidException e) {
                L.error("Exception occurred while setting field: " + tag.getKey(), e);
            }
        }

        if (commit)
            return commitTags();

        return false;
    }

    /**
     * Overwrite the song's cover and optionally commits the change to the file
     * (it may be useful to use 'false' for the commit parameter when multiple
     * overwrite have to be made to the songs and not only to the tags (e.g.
     * overwrite both the tags and the cover) and the commit have to be done only once).
     * @param cover the new cover for the song
     * @param commit whether the changes have to be committed with this overwrite
     *
     * @see #overwriteTags(Map, boolean)
     * @see #overwriteTagsAndCover(Map, Artwork, boolean)
     * @return false if the tags commit has been requested and failed
     */
    public boolean overwriteCover(Artwork cover, boolean commit) {
        L.debug("Overwriting cover for song " + this +
                " - Committing: " + commit);

        // Delete the cover if null is passed as parameter
        if (cover == null)
            mAudioFile.getTag().deleteArtworkField();
        else {
            try {
                mAudioFile.getTag().setField(cover);
            } catch (FieldDataInvalidException e) {
                L.error("Exception occurred while setting cover", e);
            }
        }

        if (commit)
            return commitTags();

        return false;
    }

    /**
     * Overwrite both the song's cover and its tags, and optionally commits the
     * change to the file (it may be useful to use 'false' for the commit
     * parameter when multiple overwrite have to be made to the songs
     * and not only to the tags (e.g. overwrite both the tags and the cover)
     * and the commit have to be done only once).
     * @param tags the tags to overwrite and their values
     * @param cover the new cover for the song
     * @param commit whether the changes have to be committed with this overwrite
     * @return false if the tags commit has been requested and failed
     */
    public boolean overwriteTagsAndCover (Map<FieldKey, String> tags,
                                       Artwork cover, boolean commit) {
        overwriteTags(tags, false);
        overwriteCover(cover, false);

        if (commit)
            return commitTags();

        return true;
    }

    /**
     * Loads the tags of the song from the underlying file (which must be an
     * .mp3 file).
     */
    public void loadTags() {
        // Read file tags
        L.info("Loading tags for song " + this);

        try {
            mAudioFile = AudioFileIO.read(mFile);
        } catch (CannotReadException | InvalidAudioFrameException |
            ReadOnlyFileException | TagException | IOException e) {
            L.error("Error occurred while loading tags for song: " + this);
            return;
        }

        // Read/write values container
        Tag audioTag = mAudioFile.getTag();

        mArtistProperty.setValue(audioTag.getFirst(FieldKey.ARTIST));
        mTitleProperty.setValue(audioTag.getFirst(FieldKey.TITLE));
        mAlbumProperty.setValue(audioTag.getFirst(FieldKey.ALBUM));
        mYearProperty.setValue(audioTag.getFirst(FieldKey.YEAR));
        mGenreProperty.setValue(audioTag.getFirst(FieldKey.GENRE));
        mTrackNumberProperty.setValue(audioTag.getFirst(FieldKey.TRACK));
        mLyricsProperty.setValue(audioTag.getFirst(FieldKey.LYRICS));
        mCover = audioTag.getFirstArtwork();

        // Read/only values container
        AudioHeader audioHeader = mAudioFile.getAudioHeader();

        mBitRateProperty.set(audioHeader.getBitRate());
        mSampleRateProperty.set(audioHeader.getSampleRate());
        mChannelsProperty.set(audioHeader.getChannels());
    }

    /**
     * Commits the changes to the tags (update via a method like
     * {@link #overwriteTags(Map, boolean)}) and thus actually save those
     * to the underlying file.
     * <p>
     * This action cause this song to notify its listeners about the change
     * by calling {@link InvalidationListener#invalidated(Observable)}.
     * @return true if the tags have been saved
     */
    public boolean commitTags()  {
        try {
            L.info("Overwriting tags of song: " + this);

            // Commit the changes to the underlying file
            if (!mFile.exists()) {
                L.error("Tags can't be written sing underlying file doesn't exist");
                return false;
            }

            mAudioFile.commit();
            // Reload the new tags
            loadTags();
            // Notify the listeners about the change
            mListeners.forEach(l -> l.invalidated(this));

            return true;
        } catch (CannotWriteException e) {
            L.error("Exception occurred committing tags", e);
            return false;
        }
    }
}
