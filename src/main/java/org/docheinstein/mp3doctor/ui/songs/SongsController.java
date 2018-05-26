package org.docheinstein.mp3doctor.ui.songs;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.docheinstein.mp3doctor.commons.logger.Logger;

import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.commons.utils.CollectionsUtil;
import org.docheinstein.mp3doctor.song.player.PlayableSongQueueProvider;
import org.docheinstein.mp3doctor.song.filter.SongSearchFilters;
import org.docheinstein.mp3doctor.song.provider.SongProvider;
import org.docheinstein.mp3doctor.song.viewer.SongViewer;
import org.docheinstein.mp3doctor.song.provider.SongSelectionProvider;

import org.docheinstein.mp3doctor.song.provider.SongListProvider;
import org.docheinstein.mp3doctor.ui.commons.CloseableControllerHandler;
import org.docheinstein.mp3doctor.ui.commons.controller.custom.InstantiableReinitializableSplitControllerView;
import org.docheinstein.mp3doctor.ui.menu.AddSelectionToPlaylistMenuItemAction;
import org.docheinstein.mp3doctor.ui.menu.ViewSongMenuItemAction;
import org.docheinstein.mp3doctor.ui.menu.PlaySongMenuItemAction;
import org.docheinstein.mp3doctor.ui.menu.RemoveSelectedSongsFromLibraryMenuItemAction;
import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.ui.song.SongController;

import java.util.List;

/**
 * Controller that shows a list of songs, allows to filter those and is able
 * to do an action when a song is click (the default one is view the song's tag
 * but can be overridden by {@link #onRowDoubleClicked(TableRow)}).
 */
public class SongsController
    extends InstantiableReinitializableSplitControllerView
    implements
        PlayableSongQueueProvider,
        SongSelectionProvider,
        SongListProvider,
        SongProvider,
        CloseableControllerHandler,
        SongViewer {

    private static final Logger L =
        Logger.createForClass(SongsController.class);

    @FXML
    protected TableView<Song> uiSongsTable;

    // Table

    @FXML
    private TableColumn<Song, Song> uiIncrementalNumberColumn;

    @FXML
    private TableColumn<Song, String> uiFilenameColumn;

    @FXML
    private TableColumn<Song, String> uiArtistColumn;

    @FXML
    private TableColumn<Song, String> uiTitleColumn;

    @FXML
    private TableColumn<Song, String> uiPathColumn;

    @FXML
    private TableColumn<Song, String> uiAlbumColumn;

    @FXML
    private TableColumn<Song, String> uiYearColumn;

    @FXML
    private TableColumn<Song, String> uiGenreColumn;

    @FXML
    private TableColumn<Song, String> uiTrackNumberColumn;

    // Filters

    @FXML
    private TextField uiSearchBar;

    @FXML
    private CheckMenuItem uiFilterTitleCheckMenuItem;

    @FXML
    private CheckMenuItem uiFilterArtistCheckMenuItem;

    @FXML
    private CheckMenuItem uiFilterAlbumCheckMenuItem;

    @FXML
    private CheckMenuItem uiFilterYearCheckMenuItem;

    @FXML
    private CheckMenuItem uiFilterGenreCheckMenuItem;

    @FXML
    private CheckMenuItem uiFilterWithLyricsCheckMenuItem;

    @FXML
    private CheckMenuItem uiFilterWithoutLyricsCheckMenuItem;

    @FXML
    private CheckMenuItem uiFilterWithCoverCheckMenuItem;

    @FXML
    private CheckMenuItem uiFilterWithoutCoverCheckMenuItem;

    // Secondary pane

    @FXML
    private SplitPane uiSplitPane;

    @FXML
    private AnchorPane uiSecondary;

    /** Current (filtered) song list. */
    private FilteredList<Song> mCurrentSongList;

    /** Underlying (complete) song list. */
    private ObservableList<Song> mSongs;

    /** Current controller that shows the song's tag. */
    private SongController mSongController;

    @Override
    public String getFXMLAsset() {
        return "songs.fxml";
    }

    @Override
    protected int getMaximumPaneCount() {
        return 2;
    }

    @Override
    public void reinit() {
        L.verbose("Reinitializing SongsController; nothing to do");
        // This controller remains up to date, there is nothing to reinitialize.
        // (Note that it doesn't mean that is not reinitializable, which actually
        // is, but just means that always remains up to date due to the observability
        // of the model).

        // Just a little thing:

        // Select the first row by default, if exists
        uiSongsTable.getSelectionModel().selectFirst();
    }

    /**
     * Creates a new songs controller for the given song list.
     * @param songs the song list that has to be shown
     */
    public SongsController(ObservableList<Song> songs) {
        L.verbose("Initializing " + getClass().getSimpleName() +
                  "; hash: " + hashCode());
        mSongs = songs;
    }

    @Override
    public List<Song> getSongQueueToPlay() {
        return getProvidedSongList(); // The queue is the entire song list
    }

    @Override
    public Song getSongToPlay() {
        return getProvidedSong();
    }

    /**
     * Returns the selected song (the first of the selection provided
     * with {@link #getProvidedSongSelection()}).
     * @return the selected song
     */
    @Override
    public Song getProvidedSong() {
        List<Song> selection = getProvidedSongSelection();
        return CollectionsUtil.isFilled(selection) ? selection.get(0) : null;
    }


    @Override
    public List<Song> getProvidedSongList() {
        return getSongs();
    }

    @Override
    public List<Song> getProvidedSongSelection() {
        L.verbose("Controller is providing song selection ( " +
            getClass().getSimpleName() + " hash: " + hashCode() + ")");
        return uiSongsTable != null ? uiSongsTable.getSelectionModel().getSelectedItems() : null;
    }

    /**
     * Returns the underlying list of songs.
     * @return the list of songs
     */
    public List<Song> getSongs() {
        return mSongs;
    }

    @Override
    public void closeController(Object controller) {
        L.debug("Close button have been clicked for the song pane. Hiding it");
        removeLastPane(true);
    }

    @Override
    public void viewSong(Song song) {
        L.debug("Showing song pane for selected song row");

        if (uiSplitPane.getItems().size() < getMaximumPaneCount()) {
            if (mSongController == null) {
                mSongController = new SongController(song);
                mSongController.setDelegate(this);
            }

            addLastPane(mSongController.createNode());
        }

        Asserts.assertNotNull(mSongController,
            "Songs controller should not be null at this point!");

        mSongController.setSong(song);
        mSongController.reinit();
    }

    @FXML
    protected void initialize() {
        L.verbose("First initialization of " + getClass().getSimpleName());

        //  ---- SEARCH BAR ----
        uiSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            L.verbose("Search bar text changed: " + newValue);
            filterSongs();
        });

        //  ---- SONGS TABLE ----
        mCurrentSongList = new FilteredList<>(mSongs);

        // For use both filter and sorting the original list should be wrapped
        // in a FilteredList and then in a SortedList
        // http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/

        SortedList<Song> sortedList = new SortedList<>(mCurrentSongList);

        sortedList.comparatorProperty().bind(uiSongsTable.comparatorProperty());

        uiSongsTable.setItems(sortedList);

        // Multiple selection
        uiSongsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Context menu for rows
        ContextMenu songsContextMenu = new ContextMenu();

        MenuItem removeFromLibraryItem = new RemoveSelectedSongsFromLibraryMenuItemAction(this, this);
        MenuItem playItem = new PlaySongMenuItemAction(this);
        MenuItem addToPlaylistItem = new AddSelectionToPlaylistMenuItemAction(this);
        MenuItem editTagsItem = new ViewSongMenuItemAction(this, this);

        songsContextMenu.getItems().add(removeFromLibraryItem);
        songsContextMenu.getItems().add(playItem);
        songsContextMenu.getItems().add(addToPlaylistItem);
        songsContextMenu.getItems().add(editTagsItem);

        // Hide the actions that cannot be done (nothing selected)
        songsContextMenu.setOnShowing(
            event -> {
                removeFromLibraryItem.setDisable(getProvidedSong() == null);
                playItem.setDisable(getSongToPlay() == null);
                editTagsItem.setDisable(getProvidedSong() == null);
            }
        );

        uiSongsTable.setContextMenu(songsContextMenu);

        // For handle the incremental number of the songs and keep them up to date
        // sets the column number equals to the position on the table of the song
        // https://stackoverflow.com/questions/16384879/auto-numbered-table-rows-javafx
        uiIncrementalNumberColumn.setCellFactory(
            new Callback<TableColumn<Song, Song>, TableCell<Song, Song>>() {
                @Override
                public TableCell<Song, Song> call(TableColumn<Song, Song> param) {
                    return new TableCell<Song, Song>() {
                        @Override
                        protected void updateItem(Song item, boolean empty) {
                            super.updateItem(item, empty);

                            setText(
                                (getTableRow() != null && item != null) ?
                                    String.valueOf(getTableRow().getIndex() + 1) : "");
                        }
                    };
                }
            });

        // Bound columns with the song model

        uiIncrementalNumberColumn.setCellValueFactory(
            cell -> new ReadOnlyObjectWrapper<>(cell.getValue()));
        uiFilenameColumn.setCellValueFactory(
            cell -> cell.getValue().getFilenameProperty());
        uiArtistColumn.setCellValueFactory(
            cell -> cell.getValue().getArtistProperty());
        uiTitleColumn.setCellValueFactory(
            cell -> cell.getValue().getTitleProperty());
        uiPathColumn.setCellValueFactory(
            cell -> cell.getValue().getPathProperty());
        uiAlbumColumn.setCellValueFactory(
            cell -> cell.getValue().getAlbumProperty());
        uiYearColumn.setCellValueFactory(
            cell -> cell.getValue().getYearProperty());
        uiGenreColumn.setCellValueFactory(
            cell -> cell.getValue().getGenreProperty());
        uiTrackNumberColumn.setCellValueFactory(
            cell -> cell.getValue().getTrackNumberProperty());

        // Double click on the row
        uiSongsTable.setRowFactory(tableView -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    onRowDoubleClicked(row);
                }
            });
            return row;
        });

        // Filters the songs when a new filter is specified

        uiFilterTitleCheckMenuItem.setOnAction(event -> filterSongs());
        uiFilterArtistCheckMenuItem.setOnAction(event -> filterSongs());
        uiFilterAlbumCheckMenuItem.setOnAction(event -> filterSongs());
        uiFilterGenreCheckMenuItem.setOnAction(event -> filterSongs());
        uiFilterYearCheckMenuItem.setOnAction(event -> filterSongs());

        // Binary advanced filters: deselect the opposite filter before filter
        // the songs

        uiFilterWithCoverCheckMenuItem.setOnAction(event -> {
            uiFilterWithoutCoverCheckMenuItem.setSelected(false);
            filterSongs();
        });

        uiFilterWithoutCoverCheckMenuItem.setOnAction(event -> {
            uiFilterWithCoverCheckMenuItem.setSelected(false);
            filterSongs();
        });

        uiFilterWithLyricsCheckMenuItem.setOnAction(event -> {
            uiFilterWithoutLyricsCheckMenuItem.setSelected(false);
            filterSongs();
        });

        uiFilterWithoutLyricsCheckMenuItem.setOnAction(event -> {
            uiFilterWithLyricsCheckMenuItem.setSelected(false);
            filterSongs();
        });

        reinit();

    }

    /**
     * Called when a row is double clicked.
     * <p>
     * The method can be overridden for do different action on this event.
     * @param row the row that has been double clicked
     */
    protected void onRowDoubleClicked(TableRow<Song> row) {
        viewSong(row.getItem());
    }

    /**
     * Filters the song list using the current text of the search bar
     * and the selected filters.
     */
    protected void filterSongs() {
        String filterString = uiSearchBar.getText();

        mCurrentSongList.setPredicate(song -> {
            // The song is accepted if at least one among the filters is satisfied
            boolean basicFiltering = false;
            boolean basicFiltersCheck = false;

            // If the filter is specified and the song satisfies the searched
            // value, the basic filtering is satisfied

            if (uiFilterTitleCheckMenuItem.isSelected()) {
                basicFiltersCheck |=
                    new SongSearchFilters.Title(filterString).check(song);
                basicFiltering = true;
            }
            if (uiFilterArtistCheckMenuItem.isSelected()) {
                basicFiltersCheck |=
                    new SongSearchFilters.Artist(filterString).check(song);
                basicFiltering = true;
            }
            if (uiFilterAlbumCheckMenuItem.isSelected()) {
                basicFiltersCheck |=
                    new SongSearchFilters.Album(filterString).check(song);
                basicFiltering = true;
            }
            if (uiFilterYearCheckMenuItem.isSelected()) {
                basicFiltersCheck |=
                    new SongSearchFilters.Year(filterString).check(song);
                basicFiltering = true;
            }
            if (uiFilterGenreCheckMenuItem.isSelected()) {
                basicFiltersCheck |=
                    new SongSearchFilters.Genre(filterString).check(song);
                basicFiltering = true;
            }

            boolean advancedFiltering = false;
            boolean advancedFiltersCheck = true;

            // If the filter is specified and the song satisfies the searched
            // value, the advanced filtering is satisfied, BUT, every advanced
            // filter must be satisfied.

            if (uiFilterWithLyricsCheckMenuItem.isSelected() ||
                uiFilterWithoutLyricsCheckMenuItem.isSelected()) {
                advancedFiltersCheck &= new SongSearchFilters.Lyrics(
                    uiFilterWithLyricsCheckMenuItem.isSelected()).check(song);
                advancedFiltering = true;
            }

            if (uiFilterWithCoverCheckMenuItem.isSelected() ||
                uiFilterWithoutCoverCheckMenuItem.isSelected()) {
                advancedFiltersCheck &= new SongSearchFilters.Cover(
                    uiFilterWithCoverCheckMenuItem.isSelected()).check(song);
                advancedFiltering = true;
            }

            // The song is accepted if it passes both the filters (and they
            // are used => there is at least a filter for the category
            // (which are basic and advanced))).
            return
                (!basicFiltering || basicFiltersCheck) &&
                (!advancedFiltering || advancedFiltersCheck);
        });
    }
}
