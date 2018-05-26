package org.docheinstein.mp3doctor.ui.menu;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.docheinstein.mp3doctor.song.player.SongPlayer;

/**
 * {@link MenuItemAction} that performs the action of pause/resume the song
 * that is currently played by {@link SongPlayer}.
 */
public class PauseResumeSongMenuItemAction extends MenuItemAction {

    /**
     * Creates a new menu item for pause/resume the song that is currently played
     * by {@link SongPlayer} without shortcut.
     *
     * @see #PauseResumeSongMenuItemAction(boolean)
     */
    public PauseResumeSongMenuItemAction() {
        this(false);
    }

    /**
     * Creates a new menu item for pause/resume the song that is currently played
     * by {@link SongPlayer}.
     * @param accelerator whether this menu item can be used with its shortcut
     */
    public PauseResumeSongMenuItemAction(boolean accelerator) {
        super(
            "Pause/Resume song",
            event -> {
                if (SongPlayer.instance().isPlaying())
                    SongPlayer.instance().pause();
                else if (SongPlayer.instance().isPaused())
                    SongPlayer.instance().resume();
            },
            !accelerator ? null :
                new KeyCodeCombination(
                    KeyCode.L,
                    KeyCodeCombination.CONTROL_DOWN
                )
        );
    }
}
