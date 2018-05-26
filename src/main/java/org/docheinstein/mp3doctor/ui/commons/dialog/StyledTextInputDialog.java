package org.docheinstein.mp3doctor.ui.commons.dialog;

import javafx.scene.control.TextInputDialog;
import org.docheinstein.mp3doctor.commons.utils.FXUtil;
import org.docheinstein.mp3doctor.commons.utils.ResourceUtil;

/**
 * A {@link TextInputDialog} that is automatically styled with the stylesheet
 * used by this application.
 */
public class StyledTextInputDialog extends TextInputDialog {

    /** Creates a new styled text input dialog with no choices. */
    public StyledTextInputDialog() {
        super();
        setHeaderText(null);
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
