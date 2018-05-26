package org.docheinstein.mp3doctor.ui.menu;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.docheinstein.mp3doctor.song.player.SongPlayer;

/**
 * {@link MenuItemAction} that performs the action of play the previous song
 * of {@link SongPlayer}.
 */
public class PlayPrevSongMenuItemAction extends MenuItemAction {

    /**
     * Creates a new menu item for play the previous song of {@link SongPlayer}
     * without shortcut.
     *
     * @see #PlayPrevSongMenuItemAction(boolean)
     */
    public PlayPrevSongMenuItemAction() {
        this(false);
    }

    /**
     * Creates a new menu item for play the previous song of {@link SongPlayer}
     * @param accelerator whether this menu item can be used with its shortcut
     */
    public PlayPrevSongMenuItemAction(boolean accelerator) {
        super(
            "Previous song",
            event -> SongPlayer.instance().prev(),
            !accelerator ? null :
                new KeyCodeCombination(
                    KeyCode.I,
                    KeyCodeCombination.CONTROL_DOWN
                )
        );
    }
}
