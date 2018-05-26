package org.docheinstein.mp3doctor.ui.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.docheinstein.mp3doctor.cache.CacheHierarchy;
import org.docheinstein.mp3doctor.commons.constants.Config;
import org.docheinstein.mp3doctor.commons.utils.ResourceUtil;

/**
 * Main application class that is responsible for load the main window (
 * {@link MainWindowController}) and show it.
 */
public class MainWindow extends Application {

    /** Root node of the application window. */
    private static Parent sRoot;

    public static void main(String[] args) {
        // Ensure that the cache hierarchy exist before start the application
        CacheHierarchy.instance().getRoot().create();
        // Starts the application (calls start())
        launch(args);
    }

    /**
     * Returns the root node of the application's window
     * @return the root node of the window
     */
    public static Parent getWindow() {
        return sRoot;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        sRoot = FXMLLoader.load(ResourceUtil.getAssetURL("main_window.fxml"));

        primaryStage.setTitle(Config.App.TITLE);
        primaryStage.getIcons().setAll(Config.App.ICONS);

        primaryStage.setMinWidth(Config.UI.MIN_WIDTH);
        primaryStage.setMinHeight(Config.UI.MIN_HEIGHT);

        primaryStage.setScene(new Scene(sRoot));

        primaryStage.show();
    }
}
