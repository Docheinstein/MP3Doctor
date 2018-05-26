package org.docheinstein.mp3doctor.ui.menu;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.DirectoryChooser;
import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.utils.Asserts;
import org.docheinstein.mp3doctor.commons.utils.CollectionsUtil;
import org.docheinstein.mp3doctor.song.SongsManager;
import org.docheinstein.mp3doctor.ui.commons.alert.AlertInstance;
import org.docheinstein.mp3doctor.ui.commons.controller.base.StageAware;

import java.io.File;

/**
 * {@link MenuItemAction} that performs the action of import a directory
 * into the songs library of {@link SongsManager}.
 */
public class ImportDirectoryMenuItemAction extends MenuItemAction {

    private static final Logger L =
        Logger.createForClass(ImportDirectoryMenuItemAction.class);

    /**
     * Creates a new menu item for import a directory into the songs library
     * without shortcut.
     * @param controller the {@link StageAware} controller responsible for
     *                   show the directory chooser
     *
     * @see #ImportDirectoryMenuItemAction(StageAware, boolean)
     */
    public ImportDirectoryMenuItemAction(StageAware controller) {
        this(controller, false);
    }

    /**
     * Creates a new menu item for import a directory into the songs library.
     * @param controller the {@link StageAware} controller responsible for
     *                   show the directory chooser
     * @param accelerator whether this menu item can be used with its shortcut
     */
    public ImportDirectoryMenuItemAction(StageAware controller, boolean accelerator) {
        super(
            "Import _directory...",
            event -> {
                L.debug("Showing open directories window");
                DirectoryChooser dirChooser = new DirectoryChooser();
                dirChooser.setTitle("Import directories of MP3 files");

                File selectedDir = dirChooser.showDialog(controller.getStage());

                if (selectedDir == null) {
                    Logger.global().warn("No dir to open has been specified; doing nothing");
                    return;
                }

                L.debug("Selected directory: " + selectedDir.getAbsolutePath());

                Asserts.assertTrue(selectedDir.isDirectory(),
                    "System DirectoryChooser returned a non-directory file. " +
                    "This should not happen");

                File[] fs = selectedDir.listFiles();

                if (CollectionsUtil.isFilled(fs)) {
                    SongsManager.instance().addFromFiles(fs);
                    AlertInstance.SongsAdded.show(fs.length, "library");
                }
            },
            !accelerator ? null :
            new KeyCodeCombination(
                KeyCode.D,
                KeyCodeCombination.CONTROL_DOWN
            )
        );


        Asserts.assertNotNull(controller,
            "A valid StageAware controller must be provided, found a null one");

    }
}
