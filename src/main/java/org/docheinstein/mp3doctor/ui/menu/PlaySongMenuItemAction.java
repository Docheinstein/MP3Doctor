package org.docheinstein.mp3doctor.ui.menu;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.song.player.SongPlayer;
import org.docheinstein.mp3doctor.song.player.PlayableSongQueueProvider;

/**
 * {@link MenuItemAction} that performs the action of play the queue of songs
 * provided by {@link PlayableSongQueueProvider}.
 */
public class PlaySongMenuItemAction extends MenuItemAction {

    /**
     * Creates a new menu item for play a song and its queue with {@link SongPlayer}
     * without shortcut.
     * @param playerConsumer the entity responsible for provide a queue of
     *                       songs to play and the first of those that will
     *                       be played
     *
     * @see #PlaySongMenuItemAction(PlayableSongQueueProvider, boolean)
     */
    public PlaySongMenuItemAction(PlayableSongQueueProvider playerConsumer) {
        this(playerConsumer, false);
    }

    /**
     * Creates a new menu item for play a song and its queue with {@link SongPlayer}.
     * @param playerConsumer the entity responsible for provide a queue of
     *                       songs to play and the first of those that will
     *                       be played
     * @param accelerator whether this menu item can be used with its shortcut
     */
    public PlaySongMenuItemAction(PlayableSongQueueProvider playerConsumer,
                                  boolean accelerator) {
        super(
            "Play song",
            event -> SongPlayer.instance().play(
                playerConsumer.getSongQueueToPlay(),
                playerConsumer.getSongToPlay()),
            !accelerator ? null :
                new KeyCodeCombination(
                    KeyCode.O,
                    KeyCodeCombination.CONTROL_DOWN
                )
        );

        Asserts.assertNotNull(playerConsumer,
            "A valid PlayableSongQueueProvider must be provided, found a null one");
    }
}
