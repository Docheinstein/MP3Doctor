package org.docheinstein.mp3doctor.ui.menu;

import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.playlist.Playlist;
import org.docheinstein.mp3doctor.playlist.provider.PlaylistProvider;
import org.docheinstein.mp3doctor.playlist.PlaylistsManager;
import org.docheinstein.mp3doctor.ui.commons.alert.AlertInstance;

/**
 * {@link MenuItemAction} that performs the action of remove a playlist
 * provided by {@link PlaylistProvider} from the the playlists
 * of {@link PlaylistsManager}.
 */
public class RemovePlaylistMenuItemAction extends MenuItemAction {

    /**
     * Creates a new menu item for remove the provided playlist from the
     * playlists of {@link PlaylistsManager} without shortcut.
     * @param playlistProvider the {@link PlaylistProvider} entity responsible
     *                         for provide the playlist to remove
     */
    public RemovePlaylistMenuItemAction(PlaylistProvider playlistProvider) {
        super(
            "_Remove playlist",
            event -> {
                Playlist pl = playlistProvider.getProvidedPlaylist();
                PlaylistsManager.instance().getList().remove(pl);
                AlertInstance.PlaylistRemoved.show(pl.getName());
            },
            null // No shortcut allowed
        );

        Asserts.assertNotNull(playlistProvider,
            "A valid PlaylistProvider must be provided, found a null one");
    }
}
