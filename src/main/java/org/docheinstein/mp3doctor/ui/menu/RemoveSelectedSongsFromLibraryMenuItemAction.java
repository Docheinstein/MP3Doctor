package org.docheinstein.mp3doctor.ui.menu;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.song.provider.SongListProvider;
import org.docheinstein.mp3doctor.song.provider.SongSelectionProvider;
import org.docheinstein.mp3doctor.ui.commons.alert.AlertInstance;

import java.util.List;

/**
 * {@link MenuItemAction} that performs the action of remove the selected songs
 * provided by {@link SongSelectionProvider} from a list of songs provided by
 * a {@link SongListProvider}.
 */
public class RemoveSelectedSongsFromLibraryMenuItemAction extends MenuItemAction {

    /**
     * Creates a new menu item for remove the selected songs from a list
     * without shortcut.
     * @param songSelectionProvider the {@link SongSelectionProvider} entity
     *                              responsible for provide the selected songs
     *                              to remove from the list
     * @param underlyingSongs the {@link SongSelectionProvider} entity
     *                        responsible for provide the list from which
     *                        remove the songs
     *
     * @see #RemoveSelectedSongsFromLibraryMenuItemAction(SongSelectionProvider, SongListProvider, boolean)
     */
    public RemoveSelectedSongsFromLibraryMenuItemAction(SongSelectionProvider songSelectionProvider,
                                                        SongListProvider underlyingSongs) {
        this(songSelectionProvider, underlyingSongs, false);
    }

    /**
     * Creates a new menu item for remove the selected songs from a list
     * without shortcut.
     * @param songSelectionProvider the {@link SongSelectionProvider} entity
     *                              responsible for provide the selected songs
     *                              to remove from the list
     * @param underlyingSongs the {@link SongSelectionProvider} entity
     *                        responsible for provide the list from which
     *                        remove the songs
     * @param accelerator whether this menu item can be used with its shortcut
     */
    public RemoveSelectedSongsFromLibraryMenuItemAction(SongSelectionProvider songSelectionProvider,
                                                        SongListProvider underlyingSongs,
                                                        boolean accelerator) {
        super(
            "_Remove from library",
            event -> {
                List<Song> songsToRemove = songSelectionProvider.getProvidedSongSelection();
                int size = songsToRemove.size();
                underlyingSongs.getProvidedSongList().removeAll(songsToRemove);
                AlertInstance.SongsRemoved.show(size);
            },
            !accelerator ? null :
            new KeyCodeCombination(
                KeyCode.R,
                KeyCodeCombination.CONTROL_DOWN
            )
        );


        Asserts.assertNotNull(songSelectionProvider,
            "A valid SongSelectionProvider must be provided, found a null one");
        Asserts.assertNotNull(underlyingSongs,
            "A valid SongListProvider must be provided, found a null one");
    }
}
