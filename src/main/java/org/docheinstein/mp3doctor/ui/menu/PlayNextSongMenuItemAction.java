package org.docheinstein.mp3doctor.ui.menu;


import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.docheinstein.mp3doctor.song.player.SongPlayer;

/**
 * {@link MenuItemAction} that performs the action of play the next song
 * of {@link SongPlayer}.
 */
public class PlayNextSongMenuItemAction extends MenuItemAction {

    /**
     * Creates a new menu item for play the next song of {@link SongPlayer}
     * without shortcut.
     *
     * @see #PlayNextSongMenuItemAction(boolean)
     */
    public PlayNextSongMenuItemAction() {
        this(false);
    }

    /**
     * Creates a new menu item for play the next song of {@link SongPlayer}
     * @param accelerator whether this menu item can be used with its shortcut
     */
    public PlayNextSongMenuItemAction(boolean accelerator) {
        super(
            "Next song",
            event -> SongPlayer.instance().next(),
            !accelerator ? null :
                new KeyCodeCombination(
                    KeyCode.P,
                    KeyCodeCombination.CONTROL_DOWN
                )
        );
    }
}
