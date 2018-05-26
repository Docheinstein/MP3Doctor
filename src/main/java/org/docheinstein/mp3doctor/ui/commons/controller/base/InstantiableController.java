package org.docheinstein.mp3doctor.ui.commons.controller.base;

import javafx.scene.Node;
import org.docheinstein.mp3doctor.commons.utils.FXUtil;
import org.docheinstein.mp3doctor.commons.utils.ResourceUtil;

/**
 * Represents a controller that is able to create a {@link Node} for
 * a given FXML asset and bound itself to that node.
 *
 * @see InstantiableControllerView
 */
public interface InstantiableController {

    /**
     * Creates a {@link Node} for the given .fxml asset and bounds
     * this entity to that view.
     * @param fxml the filename of the FXML asset to load
     * @return the node of the required view, which this controller is bound with
     */
    default Node createNode(String fxml) {
        return FXUtil.createNode(this, ResourceUtil.getAssetURL(fxml));
    }
}
