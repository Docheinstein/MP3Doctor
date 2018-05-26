package org.docheinstein.mp3doctor.ui.menu;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.ui.commons.controller.base.StageAware;

/**
 * {@link MenuItemAction} that performs the action of quit the application.
 */
public class QuitMenuItemAction extends MenuItemAction {

    private static final Logger L =
        Logger.createForClass(QuitMenuItemAction.class);

    /**
     * Creates a new menu item for quit the application without shortcut.
     * @param controller the {@link StageAware} controller responsible for
     *                   quit the application
     *
     * @see #QuitMenuItemAction(StageAware, boolean)
     */
    public QuitMenuItemAction(StageAware controller) {
        this(controller, false);
    }

    /**
     * Creates a new menu item for quit the application without shortcut.
     * @param controller the {@link StageAware} controller responsible for
     *      *                   quit the application
     * @param accelerator whether this menu item can be used with its shortcut
     */
    public QuitMenuItemAction(StageAware controller, boolean accelerator) {
        super(
            "_Quit",
            e -> {
                L.info("Quitting application");
                controller.getStage().close();
            },
            !accelerator ? null :
            new KeyCodeCombination(
                KeyCode.Q,
                KeyCodeCombination.CONTROL_DOWN
            )
        );

        Asserts.assertNotNull(controller,
            "A valid StageAware controller must be provided, found a null one");
    }
}
