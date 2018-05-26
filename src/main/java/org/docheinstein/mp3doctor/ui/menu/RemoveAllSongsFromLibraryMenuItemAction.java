package org.docheinstein.mp3doctor.ui.menu;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.song.provider.SongListProvider;
import org.docheinstein.mp3doctor.ui.commons.alert.AlertInstance;

/**
 * {@link MenuItemAction} that performs the action of remove every song
 * from a list of songs provided by a {@link SongListProvider}.
 */
public class RemoveAllSongsFromLibraryMenuItemAction extends MenuItemAction {

    /**
     * Creates a new menu item for remove all the songs from a list without
     * shortcut.
     * @param songsModel the {@link SongListProvider} entity responsible for
     *                   provide the list that has to be cleared
     *
     * @see #RemoveAllSongsFromLibraryMenuItemAction(SongListProvider, boolean)
     */
    public RemoveAllSongsFromLibraryMenuItemAction(SongListProvider songsModel) {
        this(songsModel, false);
    }

    /**
     * Creates a new menu item for remove all the songs from a list without
     * shortcut.
     * @param songsModel the {@link SongListProvider} entity responsible for
     *                    provided the list that has to be cleared
     * @param accelerator whether this menu item can be used with its shortcut
     */
    public RemoveAllSongsFromLibraryMenuItemAction(SongListProvider songsModel,
                                                   boolean accelerator) {
        super(
            "_Remove all from library",
            event -> {
                int size = songsModel.getProvidedSongList().size();
                songsModel.getProvidedSongList().clear();
                AlertInstance.SongsRemoved.show(size);
            },
            !accelerator ? null :
            new KeyCodeCombination(
                KeyCode.R,
                KeyCodeCombination.CONTROL_DOWN,
                KeyCodeCombination.ALT_DOWN
            )
        );

        Asserts.assertNotNull(songsModel,
            "A valid SongListProvider must be provided, found a null one");
    }
}
