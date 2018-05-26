package org.docheinstein.mp3doctor.ui.groups;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.TilePane;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.commons.utils.StringUtil;
import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.song.SongsManager;
import org.docheinstein.mp3doctor.ui.commons.CloseableControllerHandler;
import org.docheinstein.mp3doctor.ui.commons.controller.base.Reinitializable;
import org.docheinstein.mp3doctor.ui.commons.controller.custom.InstantiableReinitializableSplitControllerView;

import java.util.*;

/**
 * Represents a controller that aggregate a list of songs by a field
 * (which must be provided by the entities that inherit from this class)
 * and shows the aggregated items, one for each different value of the aggregation
 * field.
 * <p>
 * Furthermore the controller shows the set of songs aggregated
 * for each item (by clicking it).
 */
public abstract class SongsGroupsController
    extends InstantiableReinitializableSplitControllerView
    implements CloseableControllerHandler {

    protected static final Logger L =
        Logger.createForClass(SongsGroupsController.class);

    // The key for the unknown songs.
    // Note that this key begins with space = (ASCII: 32) which is
    // almost the lowest possible value; this guarantee that the unknown
    // entry will be the first one among the items
    private static final String UNKNOWN_MAGIC_NAME = " 000000";

    /** Contains a 'tile' for each group of song contained in {@link #mSongsGroup}. */
    @FXML
    private TilePane uiSongsGroupsPane;

    /**
     * Contains the groups of songs aggregated by the value of the aggregation
     * field of the songs; these are mapped to the list of songs for that
     * particular value of field.
     */
    private SortedMap<String, List<Song>> mSongsGroup;

    /**
     * Controller used for show the list of songs of a particular group.
     * <p>
     * This is cached in order to reinitialize it after the first time
     * via {@link Reinitializable#reinit()}.
     */
    private SongsGroupSongsController mSongGroupController;

    /**
     * Creates a new songs group controller.
     */
    public SongsGroupsController() {}

    @FXML
    private void initialize() {
        reinit();
    }

    @Override
    public String getFXMLAsset() {
        return "songs_groups.fxml";
    }

    @Override
    public void reinit() {
        L.debug("Reinitializing SongsGroupController");

        // For the first call of init() something might be useless, but since init()
        // is called even on clearCache(), it makes sense to put everything
        // about the initialization here

        removeLastPane(true);

        // Clear items
        uiSongsGroupsPane.getChildren().clear();

        if (mSongsGroup != null)
            mSongsGroup.clear();
        mSongsGroup = createSongsGroupsMap();

        L.debug("Category items pane controller have been initialized; size: "
            + mSongsGroup.entrySet().size());

        // Ok this may not be a brilliant idea but its the only way I found that
        // makes the nested node load the css in every situation.
        // This is a hack for the case the controller is invalidate from
        // the main window onSongsChanged
        Platform.runLater(() -> mSongsGroup.forEach((groupName, songs) -> {
            L.verbose("Iterating group: " + groupName);

            Image image = null; // If not specified, the default one is used

            // Image of the group
            if (!groupName.equals(UNKNOWN_MAGIC_NAME))
                image = getSongsGroupImage(songs);

            // Name of the group
            String shownGroupName =
                groupName.equals(UNKNOWN_MAGIC_NAME) ?
                    "[Unknown]" :
                    groupName;

            // Create a tile for the current song list with its name and image
            Node categoryItem = new SongsGroupController(
                shownGroupName,
                image,
                songs.size()
            ).createNode();

            // Trick for making cover image effectively final, as required by
            // the lambda
            Image finalImage = image;

            // Shows the song list on click
            categoryItem.setOnMouseClicked(event -> {
                L.debug("Clicked on an a group: showing songs in a side panel");

                // Initializes and add the songs controller for this aggregated
                // songs item
                if (uiSplitPane.getItems().size() < getMaximumPaneCount()) {
                    if (mSongGroupController == null) {
                        mSongGroupController = createSongsGroupController(
                            finalImage, groupName, songs);
                        mSongGroupController.setDelegate(SongsGroupsController.this);
                    }
                    addLastPane(mSongGroupController.createNode());
                }

                Asserts.assertNotNull(mSongGroupController,
                    "Songs controller should be not null at this point!");

                mSongGroupController.set(finalImage, shownGroupName, songs);
                mSongGroupController.reinit();

            });

            // Adds the aggregated songs item to the list of items
            uiSongsGroupsPane.getChildren().add(categoryItem);
        }));
    }

    @Override
    public void closeController(Object controller) {
        L.debug("Close button have been clicked for the songs pane. Hiding it");
        removeLastPane(true);
    }

    @Override
    protected int getMaximumPaneCount() {
        return 2;
    }

    /**
     * Returns the controller that is responsible for handle the list of song
     * for a given group of songs.
     * <p>
     * The default and base controller responsible for the task is
     * {@link SongsGroupSongsController}, but entities that inherit
     * from this one may consider to override the method in order to handle
     * the list of songs in an advanced way.
     * @param cover the image associated with the group of songs
     * @param itemName the name of the group of songs
     * @param songs the song list of the group of songs
     * @return a new controller that handles the group of songs
     */
    protected SongsGroupSongsController createSongsGroupController(
        Image cover, String itemName, List<Song> songs) {
        return new SongsGroupSongsController(cover, itemName, songs);
    }

    /**
     * Returns an image for a list of song.
     * <p>
     * This method actually returns the first valid image between the
     * songs of the list.
     * @param songs the list of songs from which extract an image
     * @return an {@link Image} for the given song list
     */
    protected Image getSongsGroupImage(List<Song> songs) {
        Image validCover = null;

        int i = 0;

        while (i < songs.size() && validCover == null) {
            validCover = songs.get(i).getCoverAsImage();

            if (validCover != null)
                L.verbose("Found valid cover for song to use as group emblem: " + songs.get(i));

            i++;
        }

        return validCover;
    }

    /**
     * Returns the value of the aggregation field for which the songs
     * are grouped by for a given song.
     * <p>
     * For instance, if the songs have to be grouped by the artist, the
     * implementation of this method should be {@link Song#getArtist()}.
     * @param song a song
     * @return the value of the aggregation field for the given song
     */
    protected abstract String getGroupingField(Song song);

    /**
     * Creates a map that group each song by the value of the aggregation
     * field provided by {@link #getGroupingField(Song)}.
     * <p>
     * The returned map thus maps a string, which is the value of the aggregation
     * field, with the list of songs that has that particular value
     * as aggregation field.
     * <p>
     * For instance, if the aggregation field is the artist of the song,
     * the maps contains an entry for each artist that is mapped to the
     * list of songs for that particular artist.
     * @return a map between aggregation fields and their songs lists
     */
    private SortedMap<String, List<Song>> createSongsGroupsMap() {
        SortedMap<String, List<Song>> categoryItems =
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        // Aggregate the songs by the required field
        SongsManager.instance().getList().forEach(s ->
            categoryItems.computeIfAbsent(
                StringUtil.isValid(getGroupingField(s)) ?
                    getGroupingField(s) : UNKNOWN_MAGIC_NAME,
                k -> new ArrayList<>()
            ).add(s));
        return categoryItems;
    }
}
