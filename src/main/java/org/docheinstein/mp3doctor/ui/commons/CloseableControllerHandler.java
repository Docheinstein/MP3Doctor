package org.docheinstein.mp3doctor.ui.commons;

/**
 * Represents an handler that can be asked to close a controller in a sense
 * that depends on the entity that implements this interface (e.g. remove
 * a pane from a split pane).
 */
public interface CloseableControllerHandler {

    /**
     * Requires this handler to close the given controller.
     * @param controller the controller to close
     */
    void closeController(Object controller);
}
