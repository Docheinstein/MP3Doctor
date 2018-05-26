package org.docheinstein.mp3doctor.ui.commons.dialog;

import javafx.scene.control.ChoiceDialog;
import org.docheinstein.mp3doctor.commons.utils.FXUtil;
import org.docheinstein.mp3doctor.commons.utils.ResourceUtil;

import java.util.Collection;

/**
 * A {@link ChoiceDialog} that is automatically styled with the stylesheet
 * used by this application.
 * @param <T> the type of the items to show to the user
 *
 * @see ChoiceDialog
 */
public class StyledChoiceDialog<T> extends ChoiceDialog<T> {

    /**
     * Creates a new styled choice dialog for the given choices
     * @param defaultChoice the default choice
     * @param choices the choices
     */
    public StyledChoiceDialog(T defaultChoice, @SuppressWarnings("unchecked") T... choices) {
        super(defaultChoice, choices);
        setGraphic(null);
        attachStylesheet();
    }

    /**
     * Creates a new styled choice dialog for the given choices
     * @param defaultChoice the default choice
     * @param choices the choices
     */
    public StyledChoiceDialog(T defaultChoice, Collection<T> choices) {
        super(defaultChoice, choices);
        setGraphic(null);
        attachStylesheet();
    }

    /**
     * Attaches the stylesheet used by this application to this dialog so
     * that it will be styled with the style of the stylesheet.
     */
    private void attachStylesheet() {
        FXUtil.addStylesheet(getDialogPane(),
            ResourceUtil.getStyleURL("style.css"));
    }
}
