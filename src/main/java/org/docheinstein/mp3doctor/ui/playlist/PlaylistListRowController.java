package org.docheinstein.mp3doctor.ui.playlist;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.docheinstein.mp3doctor.ui.commons.controller.base.InstantiableControllerView;

/** Represents a playlist row which contains just the name of the playlist. */
public class PlaylistListRowController implements InstantiableControllerView {

    @FXML
    private Label uiName;

    private String mName;

    /**
     * Creates a new playlist row controller
     * @param playlistName the name of the playlist
     */
    public PlaylistListRowController(String playlistName) {
        mName = playlistName;
    }

    @Override
    public String getFXMLAsset() {
        return "playlist_list_row.fxml";
    }

    @FXML
    private void initialize() {
        uiName.setText(mName);
    }
}
