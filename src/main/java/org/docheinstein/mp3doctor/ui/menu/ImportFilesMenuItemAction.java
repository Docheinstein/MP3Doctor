package org.docheinstein.mp3doctor.ui.menu;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.FileChooser;;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.commons.utils.CollectionsUtil;
import org.docheinstein.mp3doctor.song.SongsManager;
import org.docheinstein.mp3doctor.ui.commons.alert.AlertInstance;
import org.docheinstein.mp3doctor.ui.commons.controller.base.StageAware;

import java.io.File;
import java.util.List;

/**
 * {@link MenuItemAction} that performs the action of import one or more files
 * into the songs library of {@link SongsManager}.
 */
public class ImportFilesMenuItemAction extends MenuItemAction {

    private static final Logger L =
        Logger.createForClass(ImportFilesMenuItemAction.class);


    /**
     * Creates a new menu item for import files into the songs library
     * without shortcut.
     * @param controller the {@link StageAware} controller responsible for
     *                   show the files chooser
     *
     * @see #ImportFilesMenuItemAction(StageAware, boolean)
     */
    public ImportFilesMenuItemAction(StageAware controller) {
        this(controller, false);
    }

    /**
     * Creates a new menu item for import files into the songs library.
     * @param controller the {@link StageAware} controller responsible for
     *                   show the files chooser
     * @param accelerator whether this menu item can be used with its shortcut
     */
    public ImportFilesMenuItemAction(StageAware controller, boolean accelerator) {
        super(
            "Import _files...",
            event -> {
                L.debug("Showing open files window");
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Import MP3 files");

                // Accept only .mp3 files
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("MP3 file", "*.mp3"));

                List<File> selectedFiles = fileChooser.showOpenMultipleDialog(controller.getStage());

                if (selectedFiles == null) {
                    L.warn("No file to open has been specified; doing nothing");
                    return;
                }

                if (CollectionsUtil.isFilled(selectedFiles)) {
                    SongsManager.instance().addFromFiles(selectedFiles);
                    AlertInstance.SongsAdded.show(selectedFiles.size(), "library");
                }
            },
            !accelerator ? null :
            new KeyCodeCombination(
                KeyCode.F,
                KeyCodeCombination.CONTROL_DOWN
            )
        );

        Asserts.assertNotNull(controller,
            "A valid StageAware controller must be provided, found a null one");
    }
}
