package org.docheinstein.mp3doctor.ui.commons.controller.base;

import javafx.scene.Node;
import javafx.stage.Stage;
import org.docheinstein.mp3doctor.commons.utils.Asserts;

/**
 * Represents a controller that is able to provide the stage of
 * the {@link StageAware} interface by retrieving it from the root element
 * of the view (which must be provided by the entities that inherit from this one).
 */
public interface RootController extends StageAware {

    @Override
    default Stage getStage() {
        Node root = getRoot();
        Asserts.assertNotNull(root,
            "Found null root element. " +
            "The class should provide a valid one");
        return (Stage) root.getScene().getWindow();
    }

    /**
     * Returns the root element of this controller which is used for retrieve
     * the {@link Stage} by {@link #getStage()}.
     * @return the root element of this controller
     */
    Node getRoot();
}
