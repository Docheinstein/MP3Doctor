package org.docheinstein.mp3doctor.ui.commons.alert;


import javafx.scene.Scene;
import javafx.scene.control.Alert;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.ui.main.MainWindow;

/**
 * Alert types used by the application.
 */
public enum AlertInstance {
    AddPlaylistAlreadyExist(
        Alert.AlertType.ERROR,
        "Playlist already exists",
        "Playlist with name %s already exists"
    ),
    AddPlaylistInvalidName(
        Alert.AlertType.ERROR,
        "Invalid playlist name",
        "Please provide a valid playlist name"
    ),
    DuplicatedSongs(
        Alert.AlertType.WARNING,
        "Duplicated songs found",
        "Found %d duplicated songs; ignoring them"
    ),
    SongFileNotExist(
        Alert.AlertType.ERROR,
        "Song doesn't exist",
        "%s file not found"
    ),
    SongsAdded(
        Alert.AlertType.INFORMATION,
        "Songs added",
        "%d songs added to %s"
    ),
    SongsRemoved(
        Alert.AlertType.INFORMATION,
        "Songs removed",
        "%d songs removed"
    ),
    SongSaveFailedNumericFieldException(
        Alert.AlertType.WARNING,
        "Numeric field exception",
        "Save failed: %s must be numeric"
    ),
    SongSaved(
        Alert.AlertType.INFORMATION,
        "Song saved",
        "%s saved successfully"
    ),
    PlaylistAdded(
        Alert.AlertType.INFORMATION,
        "Playlist added",
        "Playlist %s has been added"
    ),
    PlaylistRemoved(
        Alert.AlertType.INFORMATION,
        "Playlist removed",
        "Playlist %s has been removed"
    ),
    ArtistSaved(
        Alert.AlertType.INFORMATION,
        "Artist saved",
        "%s's details have been saved"
    )
    ;

    /**
     * Creates a new alert type
     * @param type type of the alert
     * @param title title of the alert
     * @param content content of the alert
     */
    AlertInstance(Alert.AlertType type, String title, String content) {
        mType = type;
        mTitle = title;
        mContent = content;
    }

    private Alert.AlertType mType;
    private String mTitle;
    private String mContent;

    /**
     * Shows the alert using the given args for format the content
     * with {@link String#format(String, Object...)}.
     * @param contentArgs the args of the content, if needed
     */
    public void show(Object... contentArgs) {
        Alert a = AlertFactory.newAlert(
            mType, mTitle, String.format(mContent, contentArgs));
        centerAlertInScene(a);
        a.show();
    }

    /**
     * Shows the alert using the given args for format the content
     * with {@link String#format(String, Object...)} and waits for it.
     * @param contentArgs the args of the content, if needed
     */
    public void showAndWait(Object... contentArgs) {
        Alert a = AlertFactory.newAlert(
            mType, mTitle, String.format(mContent, contentArgs));
        centerAlertInScene(a);
        a.showAndWait();
    }

    /**
     * Centers the alert in the window.
     * @param a the alert
     */
    public static void centerAlertInScene(Alert a) {
        Scene s = MainWindow.getWindow().getScene();
        Asserts.assertNotNull(s, "Expected non null scene for MainWindow");
        a.setX(s.getX() + (s.getWidth()) / 2);
        a.setY(s.getY() + (s.getHeight()) / 2);
        a.show();

    }
}
