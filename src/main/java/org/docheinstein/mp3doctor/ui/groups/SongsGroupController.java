package org.docheinstein.mp3doctor.ui.groups;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.ui.commons.controller.base.InstantiableControllerView;

/**
 * Controller that represents a group of songs and thus have a name, an
 * image and a list of songs (actually the count of the songs).
 */
public class SongsGroupController implements InstantiableControllerView {

    private static final Logger L =
        Logger.createForClass(SongsGroupController.class);

    @FXML
    private ImageView uiImage;

    @FXML
    private Label uiName;

    @FXML
    private Label uiCount;

    private final String mName;
    private final Image mImage;
    private final int mCount;

    /**
     * Creates a new songs group controller for the given name, image and
     * song count.
     * @param name the name of the group of songs
     * @param image the image of the group of songs, null can be passed
     *              for use the default one
     * @param count the count of the group of songs
     */
    public SongsGroupController(String name, Image image, int count) {
        mName = name;
        mImage = image;
        mCount = count;
    }

    @Override
    public String getFXMLAsset() {
        return "songs_group.fxml";
    }

    @FXML
    public void initialize() {
        L.verbose("Initializing group for name: " + mName);
        if (mImage != null)
            uiImage.setImage(mImage);
        uiName.setText(mName);
        uiCount.setText("(" + mCount + ")");
    }
}
