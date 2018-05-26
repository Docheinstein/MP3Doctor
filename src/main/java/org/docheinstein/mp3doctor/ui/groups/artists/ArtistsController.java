package org.docheinstein.mp3doctor.ui.groups.artists;


import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.commons.utils.FXUtil;
import org.docheinstein.mp3doctor.song.Song;

import org.docheinstein.mp3doctor.ui.groups.SongsGroupSongsController;
import org.docheinstein.mp3doctor.ui.groups.SongsGroupsController;

import java.util.List;

/**
 * Represents a controller that aggregate a list of songs by their artist tag.
 * <p>
 * Furthermore, this controller provides a custom controller for handling
 * the pane that is open for an artist group: it can handle the artist details
 * and thus allow to switch between the standard list of songs shown by the
 * default controller ({@link SongsGroupSongsController} and the details of the artist.
 *
 * @see SongsGroupsController
 * @see ArtistSongsGroupController
 */
public class ArtistsController extends SongsGroupsController {

    protected static final Logger L =
        Logger.createForClass(ArtistsController.class);

    @Override
    protected String getGroupingField(Song song) {
        return song.getArtist();
    }

    @Override
    protected SongsGroupSongsController createSongsGroupController(
        Image cover, String itemName, List<Song> songs) {
        // Custom controller
        return new ArtistSongsGroupController(cover, itemName, songs);
    }

    /**
     * Controller that handles the songs list of a group just like
     * {@link SongsGroupSongsController} that furthermore allow to switch between
     * the song list and the artist's details and save those to the disk.
     */
    private static class ArtistSongsGroupController extends SongsGroupSongsController {

        // Shared images used by the controller

        private static final Image ARTIST_SONGS_IMAGE =
            FXUtil.createImage(
                ArtistSongsGroupController.class,
                "clipboard-list.png");

        private static final Image ARTIST_DETAILS_IMAGE =
            FXUtil.createImage(
                ArtistSongsGroupController.class,
                "details.png");

        private static final Image ARTIST_SAVE_IMAGE =
            FXUtil.createImage(
                ArtistSongsGroupController.class,
                "save.png");

        /** Embedded controller for the artist's details. */
        private ArtistDetailsController mArtistDetailsController;

        /** Current shown pane. */
        private PaneMode mCurrentMode;

        /** Types of pane that can be embedded in this controller. */
        private enum PaneMode {
            /** Artists details. */
            ArtistDetails,
            /** Artist's songs (retrieved from the library). */
            Songs
        }

        /**
         * Creates a new controller for the given image, name and song list.
         * @param groupImage the image of the group of songs
         * @param groupName the name of the group of songs
         * @param songs the song list of the group of songs
         */
        public ArtistSongsGroupController(Image groupImage,
                                          String groupName,
                                          List<Song> songs) {
            super(groupImage, groupName, songs);
        }

        @Override
        @FXML
        protected void initialize() {
            super.initialize();
            uiExtraButton1.setOnMouseClicked(event -> onExtraButton1Clicked());
            uiExtraButton2.setOnMouseClicked(event -> onExtraButton2Clicked());

            Tooltip.install(uiExtraButton1, new Tooltip("Artist details/Song list"));
            Tooltip.install(uiExtraButton2, new Tooltip("Save artist details"));
        }

        @Override
        public void reinit() {
            // Base re-init
            super.reinit();

            // Restore the songs pane
            mCurrentMode = PaneMode.Songs;

            uiExtraButton1.setVisible(true);
            uiExtraButton1.setImage(ARTIST_DETAILS_IMAGE);

            uiExtraButton2.setVisible(false);
            uiExtraButton2.setImage(ARTIST_SAVE_IMAGE);
        }

        // Switch mode button

        /**
         * Performs the action associated with the click on the 1st extra button.
         * <p>
         * Actually this switches the embedded controller.
         */
        protected void onExtraButton1Clicked() {
            mCurrentMode =
                mCurrentMode == PaneMode.ArtistDetails ?
                PaneMode.Songs : PaneMode.ArtistDetails;

            L.debug("Clicked artist's details button. Switching to mode " + mCurrentMode);

            // Header text
            if (mCurrentMode == PaneMode.Songs)
                uiHeader.setText(getHeaderText());
            else if (mCurrentMode == PaneMode.ArtistDetails)
                uiHeader.setText(mGroupName + "'s details");

            // Visibility of the panes
            uiSecondary.setVisible(mCurrentMode == PaneMode.ArtistDetails);
            uiMain.setVisible(mCurrentMode == PaneMode.Songs);

            if (mArtistDetailsController == null) {
                L.verbose("Allocating a new ArtistDetailsController. Group name:" + mGroupName);
                mArtistDetailsController = new ArtistDetailsController(mGroupName);
            }
            else {
                L.verbose("Reusing existing artist details controller. Group name: " + mGroupName);
                mArtistDetailsController.setArtist(mGroupName);
                mArtistDetailsController.reinit();
            }

            if (uiSecondary.getContent() == null) {
                uiSecondary.setContent(mArtistDetailsController.createNode());
            }

            // Changes the image of the switch controller button
            uiExtraButton1.setImage(
                mCurrentMode == PaneMode.ArtistDetails ?
                ARTIST_SONGS_IMAGE :
                ARTIST_DETAILS_IMAGE);

            // Show the save button for the artist mode
            uiExtraButton2.setVisible(mCurrentMode == PaneMode.ArtistDetails);
        }

        // Save button

        /**
         * Performs the action associated with the click on the 2nd extra button.
         * <p>
         * Actually this saves the artist details on the disk.
         */
        protected void onExtraButton2Clicked() {
            Asserts.assertTrue(
                mCurrentMode == PaneMode.ArtistDetails,
                "Current controller is not of type artist details: this should not happen",
                L);

            Asserts.assertNotNull(
                mArtistDetailsController,
                "Current artist's  details controller is null: this should not happen",
                L);

            mArtistDetailsController.saveArtistToCache();
        }
    }
}
