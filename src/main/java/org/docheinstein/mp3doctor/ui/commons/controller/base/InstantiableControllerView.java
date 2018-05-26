package org.docheinstein.mp3doctor.ui.commons.controller.base;

import javafx.scene.Node;
import org.docheinstein.mp3doctor.commons.logger.Logger;

/**
 * Represents a controller that is able to create a {@link Node}
 * for an already known FXML asset.
 * <p>
 * The difference between this entity and {@link InstantiableController}
 * is that the former is associated with a custom FXML asset while the latter
 * can be bound to any FXML asset.
 *
 * @see InstantiableController
 */
public interface InstantiableControllerView extends InstantiableController {
    /**
     * Creates a {@link Node} for the .fxml asset this controller
     * is associated with.
     * @return the node of this controller's view, which this controller is bound with
     */
    default Node createNode() {
        return InstantiableController.super.createNode(getFXMLAsset());
    }

    /**
     * Returns the FXML asset this controller is associated with
     * @return the filename of the FXML asset of this controller
     */
    String getFXMLAsset();
    @Override
    default Node createNode(String fxml) {
        Logger.global().warn("Creating a node for a given FXML is " +
            "not recommended since ControllerView already knows its FXML resource");
        return InstantiableController.super.createNode(getFXMLAsset());
    }

    // ^^ This should not be called ^^
}
