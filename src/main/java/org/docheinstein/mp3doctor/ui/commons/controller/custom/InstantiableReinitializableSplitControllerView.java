package org.docheinstein.mp3doctor.ui.commons.controller.custom;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.ui.commons.controller.base.InstantiableReinitializableControllerView;

/**
 * Represents an {@link InstantiableReinitializableControllerView} that has
 * a split pane and thus is able to add and remove panes to it.
 * <p>
 * This controller automatically handles the adjustment of the split pane
 * dividers by using the last value the dividers had.
 *
 * @see InstantiableReinitializableControllerView
 */
public abstract class InstantiableReinitializableSplitControllerView
    implements InstantiableReinitializableControllerView {

    protected static final Logger L =
        Logger.createForClass(InstantiableReinitializableSplitControllerView.class);

    @FXML
    protected SplitPane uiSplitPane;

    // This is kept so that when the user closes and reopens a
    // pane it has the same size
    protected double[] mLastDividers = { 0.6 };

    /**
     * Adds a pane to the split pane on the last position.
     * <p>
     * The new node count can't exceed the declared count ({@link #getMaximumPaneCount()}).
     * @param node the node to add
     */
    protected void addLastPane(Node node) {
        if (uiSplitPane.getItems().size() >= getMaximumPaneCount()) {
            L.warn("Can't add a node since the split pane already contains" +
                    " the maximum declared number of panes or more");
            return;
        }
        uiSplitPane.setDividerPositions(mLastDividers);
        uiSplitPane.getItems().add(node);
    }

    /**
     * Removes the last pane of the split pane.
     * @param removeOnlyIfFulfilled if true then the last node is removed
     *                               only if the current count of panes inside
     *                               the split panes match the quantity of
     *                               {@link #getMaximumPaneCount()}, otherwise
     *                               the last pane is removed without this check
     */
    protected void removeLastPane(boolean removeOnlyIfFulfilled) {
        if (!removeOnlyIfFulfilled ||
            uiSplitPane.getItems().size() == getMaximumPaneCount())
            removePane(getMaximumPaneCount() - 1);
        else if (uiSplitPane.getItems().size() >= getMaximumPaneCount()){
            L.warn("The split pane contains more children then the declared " +
                "one, this should not happen and may lead to unexpected behaviours");
        }
        else if (uiSplitPane.getItems().size() < getMaximumPaneCount()) {
            L.debug("Pane removal is skipped since removeOnlyIfFulfilled is " +
                "specified but the split pane is not full");
        }
    }

    /**
     * Removes the pane from the split pane at the given index
     * @param index the index of the pane to remove from the list of panes of
     *              the split pane. The index must be in a valid bound
     */
    protected void removePane(int index) {
        L.verbose("Removing pane at index: " + index);
        if (index >= 0 && index < uiSplitPane.getItems().size()) {
            mLastDividers = uiSplitPane.getDividerPositions();
            uiSplitPane.getItems().remove(index);
        }
        else {
            L.warn("Skipping pane removal since index is out of bound");
        }

    }

    /**
     * Returns the maximum number of panes the split pane can host.
     * @return the max count of panes inside the split pane
     */
    protected abstract int getMaximumPaneCount();
}
