package org.docheinstein.mp3doctor.ui.main;

import javafx.application.Platform;
import javafx.css.Styleable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.cache.CacheHierarchy;
import org.docheinstein.mp3doctor.commons.adt.ObservableListListener;
import org.docheinstein.mp3doctor.commons.constants.Config;
import org.docheinstein.mp3doctor.commons.hierarchy.FEntry;
import org.docheinstein.mp3doctor.commons.utils.*;
import org.docheinstein.mp3doctor.playlist.Playlist;
import org.docheinstein.mp3doctor.playlist.provider.PlaylistProvider;
import org.docheinstein.mp3doctor.playlist.PlaylistsManager;
import org.docheinstein.mp3doctor.song.player.SongPlayer;
import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.song.SongsManager;
import org.docheinstein.mp3doctor.song.provider.SongListProvider;
import org.docheinstein.mp3doctor.song.provider.SongProvider;
import org.docheinstein.mp3doctor.song.provider.SongSelectionProvider;
import org.docheinstein.mp3doctor.song.viewer.SongViewer;
;
import org.docheinstein.mp3doctor.ui.commons.alert.AlertInstance;
import org.docheinstein.mp3doctor.ui.commons.controller.base.Reinitializable;
import org.docheinstein.mp3doctor.ui.groups.albums.AlbumsController;
import org.docheinstein.mp3doctor.ui.groups.artists.ArtistsController;
import org.docheinstein.mp3doctor.ui.groups.genres.GenresController;
import org.docheinstein.mp3doctor.ui.commons.controller.base.InstantiableReinitializableControllerView;
import org.docheinstein.mp3doctor.ui.commons.controller.base.RootController;
import org.docheinstein.mp3doctor.ui.commons.dialog.StyledTextInputDialog;
import org.docheinstein.mp3doctor.ui.menu.*;
import org.docheinstein.mp3doctor.song.player.PlayableSongQueueProvider;
import org.docheinstein.mp3doctor.ui.playlist.PlaylistListRowController;
import org.docheinstein.mp3doctor.ui.playlist.PlaylistController;
import org.docheinstein.mp3doctor.ui.playlist.SongPlayerHandler;
import org.docheinstein.mp3doctor.ui.songs.SongsController;
import org.docheinstein.mp3doctor.ui.groups.years.YearsController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main window and controller of the application that is responsible for:
 * <ul>
 *     <li>
 *         Embed a controller which is the main controller of the
 *         application
 *     </li>
 *     <li>
 *         Show the menus and allow to use those with shortcuts
 *     </li>
 *     <li>
 *         Show a row for each category the use can navigate to
 *         (e.g. Genres, Years, Songs, ...).
 *     </li>
 *     <li>
 *         Show a row for each existing playlist and let the user
 *         navigate it.
 *     </li>
 *     <li>
 *         Play a song queue provided by an embedded controller.
 *     </li>
 * </ul>
 *
 * <p>
 *
 * This controller always embeds a controller that changes when the user
 * select a different category.
 *
 * <p>
 *
 * The philosophy of the controller is being a bridge through the actions
 * the user can request with {@link MenuItemAction}s and the embedded controller.
 *
 * <p>
 *
 * For this reason, this class implements every interface that any possible
 * embedded controller implements.
 *
 * <p>
 *
 * For instance, this controller implements {@link SongViewer}, although
 * it is not able to show a song by itself; for these reason this controller
 * asks to the current controller to do so (the embedded controller should
 * be of the type required for execute the action required).
 */
public class MainWindowController
    implements
        RootController,
        SongPlayerHandler,
        SongPlayer.SongPlayerListener,
        ObservableListListener<Playlist>,
        PlayableSongQueueProvider,
        PlaylistProvider,
        SongListProvider,
        SongProvider,
        SongSelectionProvider,
        SongViewer {

    private static final Logger L =
        Logger.createForClass(MainWindowController.class);

    // Categories

    /**
     * Entity that represents a category which essentially is a controller
     * {@link #mController} and its view {@link #mNode}.
     *
     * <p>
     *
     * Furthermore, this entity is responsible for provide a title and a
     * category label (which actually is the row that contains the category
     * name on the left side panel)
     *
     * <p>
     *
     * The category entity keeps track even of the validity of its controller:
     * if the controller is valid (not marked as dirty via {@link #markAsDirty()})
     * then when the user changes category and returns to that valid controller,
     * no action is done.
     *
     * <p>
     *
     * Although, if the controller is not valid (marked as dirty via
     * {@link #markAsDirty()}, then a reinitialization of the controller can be
     * required before show the controller via {@link Reinitializable#reinit()},
     * which must be implemented by the controller this entity wraps.
     */
    private static abstract class Category {
        /** Node created from {@link #mController}. */
        private Node mNode;

        /** Wrapped controller of this category. */
        private InstantiableReinitializableControllerView mController;

        /**
         * Whether the underlying controller is invalid because of
         * a model change and thus should be re-initialized.
         */
        private AtomicBoolean mDirty = new AtomicBoolean(false);

        /**
         * Returns the {@link Styleable} that let the user navigate to this
         * category.
         * @return the label of this category
         */
        public abstract Styleable getCategoryLabel();

        /**
         * Returns the title of this category.
         * @return the title of this category
         */
        public abstract String getTitle();

        /**
         * Creates a new controller for this category which must implements
         * the interface {@link InstantiableReinitializableControllerView}.
         * @return a new controller for this category
         */
        protected abstract InstantiableReinitializableControllerView createController();

        /**
         * Returns the view of this category.
         * <p>
         * The controller will automatically be re-initialized via
         * {@link Reinitializable#reinit()} if this category is marked as dirty
         * via {@link #markAsDirty()}.
         * @return the node that represents the view of this category
         */
        public Node getNode() {
            if (mNode == null) {
                mNode = getController(true).createNode();
            } else {
                initIfDirty();
            }

            return mNode;
        }

        /**
         * Returns the controller that handles the view of this category
         * <p>
         * The controller may be null, in particular it is not lazy initialized
         * if it is null but the current instance is returned.
         * @return the current controller 'as it is'
         *
         * @see #getController(boolean)
         */
        public InstantiableReinitializableControllerView getController() {
            return getController(false);
        }

        /**
         * Returns the controller that handles the view of this category and
         * optionally initialize it if it is invalid.
         * @param createIfNotExists whether the controller must be initialized
         *                          if it is null. If false is passed, this
         *                          method acts exactly like {@link #getController()}
         * @return the current controller, optionally initialized if null
         *
         * @see #getController()
         */
        public InstantiableReinitializableControllerView getController(boolean createIfNotExists) {
            if (createIfNotExists && mController == null)
                mController = createController();

            initIfDirty();

            return mController;
        }

        /**
         * Marks the category as dirty (it can be used when the underlying model
         * of the controller changes and thus it must be reinitialized).
         * <p>
         * Using this method implies that when the {@link #getNode()} is called
         * it will invoke {@link Reinitializable#reinit()} for the controller
         * (only once).
         * <p>
         * Note that this action has effect only if a node has already been created,
         * otherwise there is nothing to mark as dirty.
         */
        public void markAsDirty() {
            if (mController != null) {
                L.verbose("Actually marking category as dirty");
                mDirty.set(true);
            }
        }

        /**
         * Calls {@link Reinitializable#reinit()} if this category is dirty,
         * and if so, marks this category as not dirty anymore.
         */
        private void initIfDirty() {
            if (mController == null)
                return;

            if (mDirty.compareAndSet(true, false)) {
                L.debug("Calling reinit() for controller " +
                    mController.getClass().getSimpleName() +
                    " since category is marked as dirty");
                mController.reinit();
            }
        }
    }

    private final Category mSongsCategory = new Category() {

        @Override
        public Styleable getCategoryLabel() {
            return uiSongsCategoryContainer;
        }

        @Override
        public String getTitle() {
            return "SONGS";
        }

        @Override
        protected InstantiableReinitializableControllerView createController() {
            return new SongsController(SongsManager.instance().getList());
        }
    };

    /** Category for {@link ArtistsController}. */
    private final Category mArtistsCategory = new Category() {

        @Override
        public Styleable getCategoryLabel() {
            return uiArtistsCategoryContainer;
        }

        @Override
        public String getTitle() {
            return "ARISTS";
        }

        @Override
        protected InstantiableReinitializableControllerView createController() {
            return new ArtistsController();
        }
    };

    /** Category for {@link AlbumsController}. */
    private final Category mAlbumsCategory = new Category() {

        @Override
        public Styleable getCategoryLabel() {
            return uiAlbumsCategoryContainer;
        }


        @Override
        public String getTitle() {
            return "ALBUMS";
        }

        @Override
        protected InstantiableReinitializableControllerView createController() {
            return new AlbumsController();
        }
    };

    /** Category for {@link GenresController}. */
    private final Category mGenresCategory = new Category() {

        @Override
        public Styleable getCategoryLabel() {
            return uiGenresCategoryContainer;
        }

        @Override
        public String getTitle() {
            return "GENRES";
        }

        @Override
        protected InstantiableReinitializableControllerView createController() {
            return new GenresController();
        }
    };

    /** Category for {@link YearsController}. */
    private final Category mYearsCategory = new Category() {

        @Override
        public Styleable getCategoryLabel() {
            return uiYearsCategoryContainer;
        }

        @Override
        public String getTitle() {
            return "YEARS";
        }

        @Override
        protected InstantiableReinitializableControllerView createController() {
            return new YearsController();
        }
    };

    /** Possible categories the user can navigate to. */
    private final Category[] mSongsCategories = {
        mSongsCategory,
        mArtistsCategory,
        mAlbumsCategory,
        mGenresCategory,
        mYearsCategory
    };

    /**
     * Playlist categories the user can navigate to.
     * <p>
     * This are kept into a Map for easily retrieve the category
     * from a given playlist.
     */
    private final Map<Playlist, Category> mPlaylistCategories = new LinkedHashMap<>();
    // LinkedHashMap because keeps the order, thus the playlists are order
    // by their name with no particular effort

    /** Current category, which controller is shown as the main embedded one. */
    private Category mCurrentCategory;

    // Root

    @FXML
    private Node uiRoot;

    // Header

    @FXML
    private ImageView uiLogo;

    @FXML
    private Pane uiHeaderContainer;

    // Menu

    @FXML
    private Menu uiFileMenu;

    @FXML
    private Menu uiEditMenu;

    @FXML
    private Menu uiActivityMenu;

    // Primary pane

    @FXML
    private Label uiTitle;

    @FXML
    private AnchorPane uiPrimaryPane;

    // Player

    @FXML
    private ImageView uiPrevSong;

    @FXML
    private ImageView uiNextSong;

    @FXML
    private ImageView uiPlayPauseSong;

    @FXML
    private Label uiSongTitle;

    @FXML
    private Label uiSongTime;

    @FXML
    private ProgressBar uiSongProgress;

    @FXML
    private Node uiCurrentSongInfo;

    // Playlists

    @FXML
    private VBox uiPlaylists;

    // Categories rows

    @FXML
    private Styleable uiSongsCategoryContainer;

    @FXML
    private Styleable uiArtistsCategoryContainer;

    @FXML
    private Styleable uiAlbumsCategoryContainer;

    @FXML
    private Styleable uiGenresCategoryContainer;

    @FXML
    private Styleable uiYearsCategoryContainer;

    private static final Image PAUSE_IMAGE = FXUtil.createImage("pause.png");
    private static final Image PLAY_IMAGE = FXUtil.createImage("play.png");


    // Playlists callback

    @Override
    public void onElementsAdded(List<? extends Playlist> playlists) {
        L.debug("Main window has been notified about added playlists");

        playlists.forEach(playlist -> {
            L.debug("Inserting category for playlist " + playlist.getName());

            PlaylistListRowController playlistRowController =
                new PlaylistListRowController(playlist.getName());
            Node playlistRowNode = playlistRowController.createNode();

            Category playlistCategory = new Category() {
                @Override
                public Styleable getCategoryLabel() {
                    return playlistRowNode;
                }

                @Override
                public String getTitle() {
                    return playlist.getName().toUpperCase();
                }

                @Override
                protected InstantiableReinitializableControllerView createController() {
                    PlaylistController playlistController =
                        new PlaylistController(playlist);
                    playlistController.setPlayerDelegate(MainWindowController.this);
                    return playlistController;
                }
            };


            playlistRowNode.setOnMouseClicked(event -> {
                // Left click
                if (event.getButton() == MouseButton.PRIMARY)
                    setCurrentCategory(playlistCategory);
                else if (event.getButton() == MouseButton.SECONDARY) {
                    ContextMenu playlistContextMenu = new ContextMenu();

                    playlistContextMenu.getItems().add(
                        new RemovePlaylistMenuItemAction(this));

                    playlistContextMenu.setX(event.getScreenX());
                    playlistContextMenu.setY(event.getScreenY());

                    playlistContextMenu.show(getStage());
                }
            });

            mPlaylistCategories.put(playlist, playlistCategory);

            uiPlaylists.getChildren().add(playlistRowNode);
        });
    }

    @Override
    public void onElementsRemoved(List<? extends Playlist> playlists) {
        L.debug("Main window has been notified about removed playlists");

        playlists.forEach(playlist -> {
            L.debug("Removing category for playlist " + playlist.getName());

            Category playlistCategory = mPlaylistCategories.remove(playlist);

            boolean currentPaneIsTheRemovedOne = mCurrentCategory == playlistCategory;

            if (playlistCategory == null) {
                L.warn("A playlist that doesn't match any existing category has been removed");
                return;
            }

            // Without the cast the compiler notifies a warn
            uiPlaylists.getChildren().remove((Node) playlistCategory.getCategoryLabel());

            // If the current pane is the one that belongs to the removed playlist,
            // we should change pane.

            if (currentPaneIsTheRemovedOne) {
                L.verbose("Since the current pane belongs to the removed playlist," +
                    " the pane will be changed with the songs pane");
                setCurrentCategory(mSongsCategory);
            }
        });
    }

    @Override
    public void onElementsChanged(List<? extends Playlist> playlists) {
        L.debug("Main window has been notified about changed playlists");

        playlists.forEach(playlist -> {
            L.debug("Marking changed playlist category as dirty");

            Category playlistCategory = mPlaylistCategories.get(playlist);

            if (playlistCategory == null) {
                L.warn("A playlist that doesn't match any existing category has been changed");
                return;
            }

            playlistCategory.markAsDirty();
        });
    }

    @Override
    public Node getRoot() {
        return uiRoot;
    }

    /** Creates a new main window controller. */
    public MainWindowController() {
        SongPlayer.instance().addListener(this);
        SongsManager.instance().addListener(new ObservableListListener<Song>() {

            @Override
            public void onElementsAdded(List<? extends Song> songs) {
                clearCache();
            }

            @Override
            public void onElementsRemoved(List<? extends Song> songs) {
                clearCache();
            }

            @Override
            public void onElementsChanged(List<? extends Song> songs) {
                clearCache();
            }

            private void clearCache() {
                // Clear controller cache
                L.debug("Marking each songs category as dirty");

                for (Category category : mSongsCategories) {
                    Object ctrl = category.getController();

                    if (ctrl != null)
                        L.verbose("Trying to mark (songs) controller : " +
                            ctrl.getClass().getSimpleName() + " as dirty");

                    category.markAsDirty();
                }

                // Re-init the current category if it is the current one
                setCurrentCategory(mCurrentCategory);
            }
        });
        PlaylistsManager.instance().addListener(this);
    }

    // Song player

    @Override
    public void playSong(List<Song> songQueue, Song song) {
        if (song == null || songQueue == null) {
            L.warn("Can't play a null song or a null song queue");
            return;
        }

        L.debug("Going to play song: " + song);

        SongPlayer.instance().play(songQueue, songQueue.indexOf(song));
    }


    @Override
    public void onSongStart(Song song) {
        uiSongTitle.setText(song.getFilename());
        uiPlayPauseSong.setImage(PAUSE_IMAGE);
        uiCurrentSongInfo.setVisible(true);
    }

    @Override
    public void onSongProgress(Song song, double current, double duration) {
        Platform.runLater(() -> {
            String curStr = TimeUtil.millisToString(
                TimeUtil.Patterns.TIME,
                Math.round(current)
            );

            String totStr = TimeUtil.millisToString(
                TimeUtil.Patterns.TIME,
                Math.round(duration)
            );

            // Remove leading hours if == 0

            final String LEADING_ZERO_HOURS = "00:";

            if (curStr.startsWith(LEADING_ZERO_HOURS))
                curStr = curStr.substring(LEADING_ZERO_HOURS.length());

            if (totStr.startsWith(LEADING_ZERO_HOURS))
                totStr = totStr.substring(LEADING_ZERO_HOURS.length());

            uiSongTime.setText(curStr + "/" + totStr);
            uiSongProgress.setProgress(current / duration);
        });
    }

    @Override
    public void onSongStopped(Song song) {
        uiPlayPauseSong.setImage(PLAY_IMAGE);
    }

    @Override
    public void onSongFinished(Song song) {
        uiPlayPauseSong.setImage(PLAY_IMAGE);
    }

    // Song viewer

    @Override
    public void viewSong(Song song) {
        L.verbose("MainWindow has been asked about view a song");

        if (!(mCurrentCategory.getController() instanceof SongViewer)) {
            L.warn("SongViewer has been required while current controller" +
                "is not instance of SongViewer");
            return;
        }

        ((SongViewer) mCurrentCategory.getController()).viewSong(song);
    }


    // Song provider

    @Override
    public Song getProvidedSong() {
        List<Song> selection = getProvidedSongSelection();
        return CollectionsUtil.isFilled(selection) ? selection.get(0) : null;
    }

    @Override
    public List<Song> getProvidedSongList() {
        L.verbose("MainWindow has been asked about current song list");

        if (!(mCurrentCategory.getController() instanceof SongListProvider)) {
            L.warn("Get list has been required while current controller" +
                "is not instance of Selectable");
            return null;
        }

        return ((SongListProvider) mCurrentCategory.getController()).getProvidedSongList();
    }
    @Override
    public List<Song> getProvidedSongSelection() {
        L.verbose("MainWindow has been asked about current song selection");

        if (!(mCurrentCategory.getController() instanceof SongSelectionProvider)) {
            L.warn("Get selection has been required while current controller" +
                "is not instance of SongListProvider");
            return null;
        }

        return ((SongSelectionProvider) mCurrentCategory.getController()).getProvidedSongSelection();
    }


    // Song consumer

    @Override
    public List<Song> getSongQueueToPlay() {
        L.verbose("MainWindow has been asked about song queue to play");

        if (mCurrentCategory == null ||
            !(mCurrentCategory.getController() instanceof PlayableSongQueueProvider))
            return null;

        return ((PlayableSongQueueProvider) mCurrentCategory.getController())
            .getSongQueueToPlay();
    }

    @Override
    public Song getSongToPlay() {
        L.verbose("MainWindow has been asked about song to play");

        if (mCurrentCategory == null ||
            !(mCurrentCategory.getController() instanceof PlayableSongQueueProvider))
            return null;

        return ((PlayableSongQueueProvider) mCurrentCategory.getController())
            .getSongToPlay();
    }

    // Playlist

    @Override
    public Playlist getProvidedPlaylist() {
        L.verbose("MainWindow has been asked about current playlist");

        if (mCurrentCategory == null ||
            !(mCurrentCategory.getController() instanceof PlaylistProvider)) {
            L.warn("Can't provide a valid playlist");
            return null;
        }

        return ((PlaylistProvider) mCurrentCategory.getController()).getProvidedPlaylist();
    }

    @FXML
    private void initialize() {
        // Set songs as default pane
        setCurrentCategory(mSongsCategory);
        Asserts.assertTrue(
            mSongsCategory.getController(true) instanceof SongsController,
            "I wanted the default controller to be of type SongsController", L);

        // Menus
        initMenu();

        // Playlists
        onElementsAdded(PlaylistsManager.instance().getList());

        // Tooltips
        Tooltip.install(uiPrevSong, new Tooltip("Previous song"));
        Tooltip.install(uiNextSong, new Tooltip("Next song"));
        Tooltip.install(uiPlayPauseSong, new Tooltip("Play/Pause song"));

        // Shows the logo only if the width of the window is enough
        uiHeaderContainer.widthProperty().addListener(
            (observable, oldValue, newValue) ->
                uiLogo.setVisible(newValue.intValue() >
                    Config.UI.LOGO_VISIBILITY_WIDTH_THRESHOLD));
    }

    // Menus

    /** Initializes this window's menus. */
    private void initMenu() {
        // File menu

        uiFileMenu.getItems().add(new ImportFilesMenuItemAction(this, true));
        uiFileMenu.getItems().add(new ImportDirectoryMenuItemAction(this, true));
        uiFileMenu.getItems().add(new QuitMenuItemAction(this, true));

        // Edit menu

        MenuItem removeFromLibraryItem =
            new RemoveSelectedSongsFromLibraryMenuItemAction(this, this, true);

        MenuItem removeAllFromLibraryItem =
            new RemoveAllSongsFromLibraryMenuItemAction(this, true);

        MenuItem addToPlaylistMenuItemAction =
            new AddSelectionToPlaylistMenuItemAction(this, true);

        uiEditMenu.setOnShowing(
            event -> {

                Object ctrl = mCurrentCategory != null ?
                    mCurrentCategory.getController() : null;

                removeFromLibraryItem.setDisable(!(
                    (ctrl instanceof SongListProvider) &&
                    CollectionsUtil.isFilled(
                        ((SongListProvider) ctrl).getProvidedSongList()) &&
                    (ctrl instanceof SongSelectionProvider) &&
                    CollectionsUtil.isFilled(
                        ((SongSelectionProvider) ctrl).getProvidedSongSelection())
                ));

                removeAllFromLibraryItem.setDisable(!(
                    (ctrl instanceof SongListProvider) &&
                    CollectionsUtil.isFilled(
                        ((SongListProvider) ctrl).getProvidedSongList())
                ));

                addToPlaylistMenuItemAction.setDisable(!(
                    (ctrl instanceof SongSelectionProvider) &&
                    CollectionsUtil.isFilled(
                        ((SongSelectionProvider) ctrl).getProvidedSongSelection())
                ));
            }
        );

        uiEditMenu.getItems().add(removeFromLibraryItem);
        uiEditMenu.getItems().add(removeAllFromLibraryItem);
        uiEditMenu.getItems().add(addToPlaylistMenuItemAction);

        // Activity menu


        ViewSongMenuItemAction editSongMenuItem =
            new ViewSongMenuItemAction(this, this, true);

        PlaySongMenuItemAction playSongMenuItem =
            new PlaySongMenuItemAction(this, true);

        PauseResumeSongMenuItemAction pauseResumeSongMenuItem =
            new PauseResumeSongMenuItemAction(true);

        PlayNextSongMenuItemAction playNextSongMenuItem =
            new PlayNextSongMenuItemAction(true);

        PlayPrevSongMenuItemAction playPrevSongMenuItem =
            new PlayPrevSongMenuItemAction(true);


        uiActivityMenu.setOnShowing(
            event -> {

                Object ctrl = mCurrentCategory != null ?
                    mCurrentCategory.getController() : null;


                editSongMenuItem.setDisable(!(
                    (ctrl instanceof SongViewer) &&
                    (ctrl instanceof SongProvider) &&
                    ((SongProvider) ctrl).getProvidedSong() != null)
                );

                playSongMenuItem.setDisable(!(
                    (ctrl instanceof PlayableSongQueueProvider) &&
                    ((PlayableSongQueueProvider) ctrl).getSongToPlay() != null)
                );

                pauseResumeSongMenuItem.setDisable(!SongPlayer.instance().isInitialized());

                playNextSongMenuItem.setDisable(!SongPlayer.instance().isInitialized());
                playPrevSongMenuItem.setDisable(!SongPlayer.instance().isInitialized());

            }
        );

        uiActivityMenu.getItems().add(editSongMenuItem);
        uiActivityMenu.getItems().add(playSongMenuItem);
        uiActivityMenu.getItems().add(pauseResumeSongMenuItem);
        uiActivityMenu.getItems().add(playNextSongMenuItem);
        uiActivityMenu.getItems().add(playPrevSongMenuItem);
    }

    // Categories

    @FXML
    private void onSongsCategoryClicked() {
        L.debug("Songs category clicked");
        setCurrentCategory(mSongsCategory);
    }

    @FXML
    private void onArtistsCategoryClicked() {
        L.debug("Artists category clicked");
        setCurrentCategory(mArtistsCategory);
    }

    @FXML
    private void onAlbumsCategoryClicked() {
        L.debug("Albums category clicked");
        setCurrentCategory(mAlbumsCategory);
    }

    @FXML
    private void onGenresCategoryClicked() {
        L.debug("Genres category clicked");
        setCurrentCategory(mGenresCategory);
    }

    @FXML
    private void onYearsCategoryClicked() {
        L.debug("Years category clicked");
         setCurrentCategory(mYearsCategory);
    }

    /**
     * Sets the current category to the given one and updates the UI.
     * @param category the category that has to be set for this window
     */
    private void setCurrentCategory(Category category) {
        L.info("Switching to category " + category.getTitle());
        mCurrentCategory = category;
        updateCategoryPane();
        updateCategoryLabels();
        updateTitle();
    }

    /** Updates the embedded pane with the node of the current category. */
    private void updateCategoryPane() {
        FXUtil.attachToAnchorPane(uiPrimaryPane, mCurrentCategory.getNode());
    }

    /** Updates the style of the categories rows for the current category. */
    private void updateCategoryLabels() {
        clearCategoryLabelsStyle();
        FXUtil.addClass(mCurrentCategory.getCategoryLabel(), "category-selected");
    }

    /** Updates the title of the embedded controller for the current category. */
    private void updateTitle() {
        uiTitle.setText(mCurrentCategory.getTitle());
    }

    /** Clears the style of each category label. */
    private void clearCategoryLabelsStyle() {
        for (Category category : mSongsCategories) {
            FXUtil.clearClasses(category.getCategoryLabel());
        }

        for (Category category : mPlaylistCategories.values()) {
            FXUtil.clearClasses(category.getCategoryLabel());
        }
    }

    // Song player

    @FXML
    private void onPlayPauseSongClicked() {
        L.debug("Clicked play/pause song button");

        if (!SongPlayer.instance().isInitialized())
            playSong(getSongQueueToPlay(), getSongToPlay());

        else if (SongPlayer.instance().isPlaying())
            SongPlayer.instance().pause();

        else if (SongPlayer.instance().isPaused())
            SongPlayer.instance().resume();
    }

    @FXML
    private void onPrevSongClicked() {
        L.debug("Clicked prev song button");
        SongPlayer.instance().prev();
    }

    @FXML
    private void onNextSongClicked() {
        L.debug("Clicked next song button");
        SongPlayer.instance().next();
    }

    // Playlists

    @FXML
    private void onNewPlaylistClicked() {
        L.debug("New playlist required");

        // Create custom component for dialog

        StyledTextInputDialog playlistNameDialog = new StyledTextInputDialog();
        playlistNameDialog.setTitle("New playlist");
        playlistNameDialog.setContentText("Please enter the playlist name");

        playlistNameDialog.showAndWait().ifPresent(this::addPlaylist);
    }

    /**
     * Adds the given playlist to ({@link PlaylistsManager} if it doesn't exist yet.
     * <p>
     * This method doesn't directly update the UI, it will be updated
     * by the callback we receive from the {@link PlaylistsManager}.
     * @param playlistName name of the playlist
     */
    private void addPlaylist(String playlistName) {
        if (!StringUtil.isValid(playlistName)) {
            L.warn("Can't add a playlist with an invalid name");
            AlertInstance.AddPlaylistInvalidName.show();
            return;
        }

        L.info("Adding playlist with name: " + playlistName);

        if (CollectionsUtil.find(
            PlaylistsManager.instance().getList(),
            playlist -> playlist.getName().equals(playlistName)) != null) {

            // Do no allow the create a playlist with a name of an already
            // existing one

            L.warn("Playlist with name: " + playlistName + " already exists" +
                ": can't be created again");
            AlertInstance.AddPlaylistAlreadyExist.show(playlistName);
            return;

        }

        // First creation of the playlist

        FEntry newPlaylistEntry = new FEntry(playlistName);
        CacheHierarchy.instance().getPlaylistsEntry().addEntry(newPlaylistEntry);

        if (!newPlaylistEntry.create()) {
            L.warn("Playlist file creation has failed");
            return;
        }

        // Underlying playlist file exists at this point

        PlaylistsManager.instance().getList().add(
            new Playlist(newPlaylistEntry.getWrappedFile()));

        AlertInstance.PlaylistAdded.show(playlistName);
    }
}