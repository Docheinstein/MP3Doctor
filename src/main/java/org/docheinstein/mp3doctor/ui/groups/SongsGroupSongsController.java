package org.docheinstein.mp3doctor.ui.groups;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.song.player.SongPlayer;
import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.ui.commons.controller.base.InstantiableReinitializableControllerView;
import org.docheinstein.mp3doctor.ui.commons.CloseableControllerHandler;

import java.util.List;

/**
 * Controller that is responsible for show a list of songs for a
 * songs group.
 */
public class SongsGroupSongsController implements InstantiableReinitializableControllerView {

    protected static final Logger L =
        Logger.createForClass(SongsGroupSongsController.class);

    @FXML
    protected Label uiHeader;

    @FXML
    private ImageView uiCategoryItemImage;

    @FXML
    private Label uiCategoryItemName;

    @FXML
    private Label uiSongCount;


    @FXML
    private ImageView uiCloseButton;

    @FXML
    protected ImageView uiExtraButton1;

    @FXML
    protected ImageView uiExtraButton2;

    @FXML
    private TableView<Song> uiSongsTable;

    @FXML
    private TableColumn<Song, String> uiFilenameColumn;

    @FXML
    private TableColumn<Song, String> uiArtistColumn;

    @FXML
    private TableColumn<Song, String> uiTitleColumn;

    @FXML
    protected Node uiMain;

    @FXML
    protected ScrollPane uiSecondary;

    /** Name of the group. */
    private Image mGroupImage;

    /** Name of the group. */
    protected String mGroupName;

    /** Song list of the group to show. */
    private List<Song> mSongs;

    /** Delegate responsible for handle the request of close this controller. */
    private CloseableControllerHandler mCloseDelegate;


    @Override
    public String getFXMLAsset() {
        return "songs_group_songs.fxml";
    }


    /**
     * Creates a new songs controller for the given image, name and song list.
     * @param groupImage image of the group, null may be passed for use the default
     *                   image
     * @param groupName name of the group
     * @param songs list of songs of the group
     */
    public SongsGroupSongsController(Image groupImage,
                                     String groupName,
                                     List<Song> songs) {
        set(groupImage, groupName, songs);
    }

    /**
     * Sets the image, the name and the song list this controller should show.
     * <p>
     * This should be used in combination with {@link #reinit()}.
     * @param groupImage image of the group, null may be passed for use the default
     *                   image
     * @param groupName name of the group
     * @param songs list of songs of the group
     */
    public void set(Image groupImage,
                    String groupName,
                    List<Song> songs) {
        mGroupImage = groupImage;
        mGroupName = groupName;
        mSongs = songs;
    }

    /**
     * Sets the delegate responsible for handle the request from this controller
     * to close this controller.
     * @param delegate the delegate
     */
    public void setDelegate(CloseableControllerHandler delegate) {
        mCloseDelegate = delegate;
    }

    @FXML
    protected void initialize() {
        //  Toolbar
        uiCloseButton.setOnMouseClicked(event -> onCloseClicked());
        Tooltip.install(uiCloseButton, new Tooltip("Close panel"));

        //  Songs table

        uiFilenameColumn.setCellValueFactory(
            cell -> cell.getValue().getFilenameProperty());
        uiArtistColumn.setCellValueFactory(
            cell -> cell.getValue().getArtistProperty());
        uiTitleColumn.setCellValueFactory(
            cell -> cell.getValue().getTitleProperty());


        uiSongsTable.setRowFactory(tableView -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    SongPlayer.instance().play(
                        uiSongsTable.getItems(),
                        row.getItem());
                }
            });
            return row;
        });

        uiMain.managedProperty().bind(uiMain.visibleProperty());
        uiSecondary.managedProperty().bind(uiSecondary.visibleProperty());

        reinit();
    }

    @Override
    public void reinit() {
        //  Group header
        L.debug("Reinitializing SongsGroupSongsController");

        if (mGroupImage != null)
            uiCategoryItemImage.setImage(mGroupImage);
        uiCategoryItemName.setText(mGroupName);
        uiSongCount.setText("(" + mSongs.size() + ")");

        // Visibility

        uiMain.setVisible(true);
        uiSecondary.setVisible(false);

        uiExtraButton1.setVisible(false);
        uiExtraButton2.setVisible(false);

        uiHeader.setText(getHeaderText());

        uiSongsTable.setItems(FXCollections.observableList(mSongs));

        // Select the first row by default, if exists
        uiSongsTable.getSelectionModel().selectFirst();
    }

    /**
     * Returns the text for the header of this controller.
     * <p>
     * Is called on {@link #reinit()}.
     * <p>
     * May be overridden for add custom text to the header.
     * @return the text for the header
     */
    protected String getHeaderText() {
        return mGroupName + "'s songs";
    }

    /**
     * Called when the close button is clicked: notifies the delegate about
     * the fact.
     */
    private void onCloseClicked() {
        if (mCloseDelegate != null)
            mCloseDelegate.closeController(this);
    }
}
