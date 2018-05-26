package org.docheinstein.mp3doctor.ui.song;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.constants.Config;
import org.docheinstein.mp3doctor.commons.utils.FXUtil;
import org.docheinstein.mp3doctor.commons.utils.StringUtil;
import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.ui.commons.alert.AlertInstance;
import org.docheinstein.mp3doctor.ui.commons.controller.base.InstantiableReinitializableControllerView;
import org.docheinstein.mp3doctor.ui.commons.CloseableControllerHandler;
import org.docheinstein.mp3doctor.ui.commons.controller.base.RootController;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/** Controller responsible for view and edit the tags of a song and its cover. */
public class SongController
    implements InstantiableReinitializableControllerView, RootController {

    private static final Logger L =
        Logger.createForClass(SongController.class);

    @FXML
    private Node uiRoot;

    @FXML
    private Label uiHeader;

    @FXML
    private TextField uiArtist;

    @FXML
    private TextField uiTitle;

    @FXML
    private TextField uiAlbum;

    @FXML
    private TextField uiYear;

    @FXML
    private TextField uiTrackNumber;

    @FXML
    private TextField uiGenre;

    @FXML
    private TextArea uiLyrics;

    @FXML
    private Label uiBitRate;

    @FXML
    private Label uiSampleRate;

    @FXML
    private Label uiChannels;

    @FXML
    private ImageView uiCover;

    @FXML
    private ImageView uiSaveButton;

    @FXML
    private ImageView uiDeleteCoverButton;

    @FXML
    private Button uiSelectCoverButton;

    @FXML
    private ImageView uiCloseButton;

    /** Tooltip for the save button. */
    private final Tooltip mSaveSongTooltip = new Tooltip("Save song");

    /** Tooltip for the delete button. */
    private final Tooltip mDeleteCoverTooltip = new Tooltip("Delete cover");

    /** Whether the user changes the cover from the last save (reflects the UI). */
    private boolean mCoverChanged = false;

    /** File chosen by the user of the new cover. */
    private File mNewCoverFile = null;

    /** Song currently handled by this controller. */
    private Song mSong;

    /** Delegate responsible for handle the request of close this controller. */
    private CloseableControllerHandler mDelegate;

    /**
     * Creates a new song controller.
     * @param song the song to handle
     */
    public SongController(Song song) {
        mSong = song;
    }

    /**
     * Sets the song handled by this controller
     * @param song the song to handle
     */
    public void setSong(Song song) {
        mSong = song;
    }

    /**
     * Sets the delegate responsible for handle the request from this controller
     * to close this controller.
     * @param delegate the delegate
     */
    public void setDelegate(CloseableControllerHandler delegate) {
        mDelegate = delegate;
    }

    @Override
    public Node getRoot() {
        return uiRoot;
    }

    @Override
    public String getFXMLAsset() {
        return "song.fxml";
    }

    @FXML
    private void initialize() {
        // Detect the change of the text for enable the save button
        ChangeListener<String> songTagChangeHandler =
            (observable, oldValue, newValue) -> enableSaveSongButton(true);

        uiArtist.textProperty().addListener(songTagChangeHandler);
        uiTitle.textProperty().addListener(songTagChangeHandler);
        uiAlbum.textProperty().addListener(songTagChangeHandler);
        uiYear.textProperty().addListener(songTagChangeHandler);
        uiTrackNumber.textProperty().addListener(songTagChangeHandler);
        uiGenre.textProperty().addListener(songTagChangeHandler);
        uiLyrics.textProperty().addListener(songTagChangeHandler);

        uiSaveButton.setOnMouseClicked(event -> saveSong());
        uiSelectCoverButton.setOnMouseClicked(event -> onSelectCoverClicked());
        uiDeleteCoverButton.setOnMouseClicked(event -> onDeleteCoverClicked());

        uiCloseButton.setOnMouseClicked(event -> onCloseClicked());
        Tooltip.install(uiCloseButton, new Tooltip("Close panel"));

        reinit();
    }

    @Override
    public void reinit() {
        L.debug("Reinitializing SongController");

        // Load the song tags in the text fields
        uiHeader.setText(mSong.getFilename());
        uiArtist.setText(mSong.getArtist());
        uiTitle.setText(mSong.getTitle());
        uiAlbum.setText(mSong.getAlbum());
        uiYear.setText(mSong.getYear());
        uiTrackNumber.setText(mSong.getTrackNumber());
        uiGenre.setText(mSong.getGenre());
        uiLyrics.setText(mSong.getLyrics());

        Image cover = mSong.getCoverAsImage();


        uiCover.setImage(cover != null ? cover : Config.UI.DEFAULT_COVER_IMAGE);

        uiBitRate.setText(mSong.getBitRate());
        uiSampleRate.setText(mSong.getSampleRate());
        uiChannels.setText(mSong.getChannels());

        // Disable the save song button if nothing is modified
        enableSaveSongButton(false);

        // Must be done after the set of the texts
        enableDeleteCoverButton(cover != null);
    }


    /**
     * Called when the close button is clicked: notifies the delegate about
     * the fact.
     */
    private void onCloseClicked() {
        if (mDelegate != null)
            mDelegate.closeController(this);
    }

    /**
     * Called when the user chose to change the cover: shows the file chooser.
     */
    private void onSelectCoverClicked() {
        L.debug("Showing cover chooser window");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select song's cover");
        onCoverUpdated(fileChooser.showOpenDialog(getStage()));
    }

    /**
     * Called when the user chose to delete the cover.
     */
    private void onDeleteCoverClicked() {
        L.debug("Deleting song's cover");
        onCoverUpdated(null);
    }

    /**
     * Called when a new cover is required for the song (but the change is
     * not and should not be committed yet).
     * @param newCoverFile the new cover file
     */
    private void onCoverUpdated(File newCoverFile) {
        mNewCoverFile = newCoverFile;
        uiCover.setImage(newCoverFile != null ?
            new Image(newCoverFile.toURI().toString()) :
            Config.UI.DEFAULT_COVER_IMAGE);
        // The song can be saved and the cover can't be deleted anymore
        mCoverChanged = true;
        enableSaveSongButton(true);
        enableDeleteCoverButton(newCoverFile != null);
    }

    /** Saves the songs by committing the tags to the .mp3 file. */
    private void saveSong() {
        L.debug("Saving songs tag");

        // Check that numeric fields actually contain numbers

        try {
            String s = uiTrackNumber.getText();
            if (StringUtil.isValid(s))
                Integer.parseInt(uiTrackNumber.getText());
            // else := null is allowed
        } catch (NumberFormatException nfe) {
            L.error("Track number must be a valid number");
            AlertInstance.SongSaveFailedNumericFieldException.show("truck number");
            return;
        }

        try {
            String s = uiYear.getText();
            if (StringUtil.isValid(s))
                Integer.parseInt(uiYear.getText());
            // else := null is allowed
        } catch (NumberFormatException nfe) {
            L.error("Year must be a valid number");
            AlertInstance.SongSaveFailedNumericFieldException.show("year");
            return;
        }

        HashMap<FieldKey, String> tags = new HashMap<>();
        tags.put(FieldKey.TITLE, uiTitle.getText());
        tags.put(FieldKey.ARTIST, uiArtist.getText());
        tags.put(FieldKey.ALBUM, uiAlbum.getText());
        tags.put(FieldKey.YEAR, uiYear.getText());
        tags.put(FieldKey.TRACK, uiTrackNumber.getText());
        tags.put(FieldKey.GENRE, uiGenre.getText());
        tags.put(FieldKey.LYRICS, uiLyrics.getText());

        boolean saveOutcome;

        if (mCoverChanged) {
            L.verbose("Overwriting tags and cover");
            try {
                saveOutcome = mSong.overwriteTagsAndCover(
                    tags,
                    mNewCoverFile == null ?
                        null :
                        ArtworkFactory.createArtworkFromFile(mNewCoverFile),
                    true);
            } catch (IOException e) {
                L.error("Error occurred while creating artwork from file. " +
                        "As B plan only the tags will be set");
                saveOutcome = mSong.overwriteTags(tags, true);
            }
        }
        else {
            L.verbose("Overwriting only tags (no cover)");
            saveOutcome = mSong.overwriteTags(tags, true);
        }

        if (saveOutcome) {
            AlertInstance.SongSaved.show(mSong.toString());
            // Disable since there is nothing to commit at this point
            enableSaveSongButton(false);
        }
        else {
            L.error("Can't save tags a non existing song");
            AlertInstance.SongFileNotExist.show(mSong);
        }
    }

    /**
     * Enables/disables the delete cover button.
     * @param enable whether enable the button
     */
    private void enableDeleteCoverButton(boolean enable) {
        enableImageButton(uiDeleteCoverButton, enable, mDeleteCoverTooltip);
    }

    /**
     * Enables/disables the save song tags button.
     * @param enable whether enable the button
     */
    private void enableSaveSongButton(boolean enable) {
        enableImageButton(uiSaveButton, enable, mSaveSongTooltip);
    }

    /**
     * Enables/disables the given button and performs the action by
     * locking an atomic checker.
     * @param button the button to enable/disable
     * @param enable whether enable the given button
     * @param tooltip a tooltip to install if the button is enabled;
     *                if enable is 'false', then any previously installed
     *                tooltip is uninstalled, despite the value of this parameter
     */
    private void enableImageButton(ImageView button, boolean enable, Tooltip tooltip) {
        // L.verbose("Enabling button " +  button.getId() + " : " + enable);
        button.setOpacity(enable ? 1 : 0.2);
        button.setDisable(!enable);
        if (enable) {
            FXUtil.setClass(button, "cursor-hand");
            Tooltip.install(button, tooltip);
        }
        else {
            FXUtil.clearClasses(button);
            Tooltip.uninstall(button, tooltip);
        }
    }
}
