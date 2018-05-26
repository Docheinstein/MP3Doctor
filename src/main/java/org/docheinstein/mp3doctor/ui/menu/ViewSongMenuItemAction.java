package org.docheinstein.mp3doctor.ui.menu;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.song.Song;
import org.docheinstein.mp3doctor.song.viewer.SongViewer;
import org.docheinstein.mp3doctor.song.provider.SongProvider;

/**
 * {@link MenuItemAction} that performs the action of invoke
 * {@link SongViewer#viewSong(Song)} on a song provided by {@link SongProvider}.
 * <p>
 * This actually means show the song, and thus its tags.
 */
public class ViewSongMenuItemAction extends MenuItemAction {

    /**
     * Creates a new menu item for view a song without shortcut.
     * @param songViewer the entity that is responsible for 'view' the song
     * @param songProvider the entity that provides the song to show
     */
    public ViewSongMenuItemAction(SongViewer songViewer,
                                  SongProvider songProvider) {
        this(songViewer, songProvider, false);
    }

    /**
     * Creates a new menu item for view a song without shortcut.
     * @param songViewer the entity that is responsible for 'view' the song
     * @param songProvider the entity that provides the song to show
     * @param accelerator whether this menu item can be used with its shortcut
     */
    public ViewSongMenuItemAction(SongViewer songViewer,
                                  SongProvider songProvider,
                                  boolean accelerator) {
        super(
            "_Edit tags",
            event -> songViewer.viewSong(songProvider.getProvidedSong()),
            !accelerator ? null :
            new KeyCodeCombination(
                KeyCode.E,
                KeyCodeCombination.CONTROL_DOWN
            )
        );

        Asserts.assertNotNull(songViewer,
            "A valid SongViewer must be provided, found a null one");
        Asserts.assertNotNull(songProvider,
            "A valid SongProvider must be provided, found a null one");
    }
}
