package org.docheinstein.mp3doctor.ui.menu;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.playlist.Playlist;
import org.docheinstein.mp3doctor.playlist.PlaylistsManager;
import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.song.provider.SongSelectionProvider;
import org.docheinstein.mp3doctor.ui.commons.alert.AlertInstance;
import org.docheinstein.mp3doctor.ui.commons.dialog.StyledChoiceDialog;

import java.util.List;

/**
 * {@link MenuItemAction} that performs the addition of a songs to a playlist.
 */
public class AddSelectionToPlaylistMenuItemAction extends MenuItemAction {

    private static final Logger L =
        Logger.createForClass(AddSelectionToPlaylistMenuItemAction.class);

    /**
     * Creates a new menu item for add a song to a playlist without shortcut.
     * @param songSelectable the entity that provides the songs to add to the playlist
     *
     * @see #AddSelectionToPlaylistMenuItemAction(SongSelectionProvider, boolean)
     */
    public AddSelectionToPlaylistMenuItemAction(SongSelectionProvider songSelectable) {
        this(songSelectable, false);
    }

    /**
     * Creates a new menu item for add a song to a playlist.
     * @param songSelectable the entity that provides the songs to add to the playlist
     * @param accelerator whether this menu item can be used with its shortcut
     */
    public AddSelectionToPlaylistMenuItemAction(SongSelectionProvider songSelectable,
                                                boolean accelerator) {
        super(
            "_Add to playlist...",
            event -> {
                L.debug("Showing playlist chooser");

                List<Playlist> playlists = PlaylistsManager.instance().getList();

                if (playlists.size() <= 0) {
                    L.warn("No playlist found, can't add a song");
                    return;
                }

                StyledChoiceDialog<Playlist> playlistChooser =
                    new StyledChoiceDialog<>(playlists.get(0), playlists);
                playlistChooser.setTitle("Add song(s) to playlist");
                playlistChooser.setHeaderText("Select the playlist the song(s) will be added to");

                playlistChooser.showAndWait().ifPresent(pl -> {
                    L.debug("User selected a valid playlist: " + pl.getName());
                    List<Song> selectedSongs = songSelectable.getProvidedSongSelection();
                    L.verbose("Selectable: " + songSelectable.hashCode());
                    L.debug("Going to add " + selectedSongs.size() + " songs to playlist");
                    pl.getList().addAll(selectedSongs);
                    AlertInstance.SongsAdded.show(selectedSongs.size(), pl.getName());
                });
            },
            !accelerator ? null :
            new KeyCodeCombination(
                KeyCode.A,
                KeyCodeCombination.CONTROL_DOWN,
                KeyCodeCombination.ALT_DOWN)
            );

        Asserts.assertNotNull(songSelectable,
            "A valid SongSelectionProvider must be provided, found a null one");
    }
}