package org.docheinstein.mp3doctor.ui.menu;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import org.docheinstein.mp3doctor.commons.utils.Asserts;

/**
 * Represents a {@link MenuItem} that embeds an action an optionally
 * allow to use through a shortcut (accelerator).
 */
public class MenuItemAction extends MenuItem {

    /**
     * Creates a new menu item for the given name and action, and optionally
     * allow to use it through a shortcut
     * @param name the name of the menu item
     * @param action the action to perform when the menu item is required
     * @param accelerator the shortcut
     */
    public MenuItemAction(String name, EventHandler<ActionEvent> action,
                          KeyCombination accelerator) {
        super(name);
        Asserts.assertNotNull(action,
            "Create a MenuItemAction with null action doesn't make sense!");
        setOnAction(action);
        if (accelerator != null)
            setAccelerator(accelerator);
    }

    /**
     * Creates a new menu item for the given name and action without any shortcut
     * @param name the name of the menu item
     * @param action the action to perform when the menu item is required
     *
     * @see #MenuItemAction(String, EventHandler, KeyCombination)
     */
    public MenuItemAction(String name, EventHandler<ActionEvent> action) {
        this(name, action, null);
    }
}
