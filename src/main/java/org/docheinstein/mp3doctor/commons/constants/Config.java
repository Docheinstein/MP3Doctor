package org.docheinstein.mp3doctor.commons.constants;

import javafx.scene.image.Image;
import org.docheinstein.mp3doctor.commons.utils.FXUtil;

/** Container for the global configuration and shared resources. */
public class Config {

    /** Contains application's config. */
    public static class App {
        public static final String TITLE = "MP3 Doctor";

        public static final Image ICONS[] = new Image[] {
            FXUtil.createImage(Config.class, "mp3-doctor-icon.png"),
            FXUtil.createImage(Config.class, "mp3-doctor.png"),
        };
    }

    /** Contains names of the directories used by the application. */
    public static class Paths {
        /** Contains the path used for save/load files on the disk. */
        public static class Cache {
            // System.getProperty("user.home") + "/.cache/mp3doctor/",
            public static final String ROOT_PATH = ".";
            public static final String ROOT_FOLDER_NAME = "mp3doctor";
            public static final String SONGS_FOLDER_NAME = "songs";
            public static final String PLAYLISTS_FOLDER_NAME = "playlists";
            public static final String ARTISTS_FOLDER_NAME = "artists";

        }

        /** Contains the relative paths of the resources of this application. */
        public static class Resources {
            public static final String ASSETS = "assets/";
            public static final String IMAGES = "images/";
            public static final String CSS = "css/";
        }
    }

    /** Contains common user interface preference s */
    public static class UI {
        public static final int MIN_WIDTH = 600;
        public static final int MIN_HEIGHT = 400;

        /** Below this threshold the application logo should be hidden. */
        public static final int LOGO_VISIBILITY_WIDTH_THRESHOLD = 600;

        /** Shared image for items (e.g. albums) without a cover. */
        public static final Image DEFAULT_COVER_IMAGE =
            FXUtil.createImage(Config.class, "unknown-cover.jpg");

    }
}
