package org.docheinstein.mp3doctor.commons.utils;

import org.docheinstein.mp3doctor.commons.logger.Logger;

import java.io.*;
import java.util.Scanner;

/** Contains utility methods for files. */
public class FileUtil {

    private static final Logger L = Logger.createForClass(FileUtil.class);

    /**
     * Writes the given content to a file.
     * This is convenient if the file should be written just once, but it
     * is not recommended for more consecutive use.
     * For consecutive writes is better use {@link #getWriter(File)}.
     * @param path the path of the file
     * @param content the content to write
     * @return true if the file has been written successfully
     *
     * @see #getWriter(File)
     */
    public static boolean write(String path, String content) {
        try {
            PrintWriter w = new PrintWriter(path, "UTF-8");
            w.print(content);
            w.close();
            return true;
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            return false;
        }
    }

    /**
     * Returns the filename of the file without the trailing extension.
     * @param file the file
     * @return the file name of the file without extension
     */
    public static String getFileNameWithoutExtension(File file) {
        if (file == null)
            return null;

        String filename = file.getName();

        int dotPos = filename.lastIndexOf(".");

        if (dotPos == -1)
            return filename;

        return filename.substring(0, dotPos);
    }

    /**
     * Returns the extension of the file (the part of the file name
     * after the last ".").
     * @param file the file
     * @return the extension of the file
     */
    public static String getFileExtension(File file) {
        if (file == null)
            return null;

        String filename = file.getName();

        int dotPos = filename.lastIndexOf(".");

        if (dotPos == -1)
            return filename;

        return filename.substring(dotPos + 1);
    }

    /**
     * Returns a {@link PrintWriter} that is able to write on the given file.
     * @param file the file for which create a writer
     * @return a writer for the file
     */
    public static PrintWriter getWriter(File file) {
        Asserts.assertTrue(file != null, "Can't require a writer for an invalid file");

        L.verbose("PrintWriter required for file: " + file.getAbsolutePath());

        try {

            boolean fileExists = file.exists() || file.createNewFile();

            if (fileExists)
                return new PrintWriter(new BufferedWriter(new FileWriter(file)));
        } catch (IOException e) { /* The error is reported below */ }

        L.error("Error occurred while creating PrintWriter for file: " + file.getPath());
        return null;
    }

    /**
     * Returns a {@link Scanner} that is able to read the given file.
     * @param file the file for which create a scanner
     * @return a scanner for the file
     */
    public static Scanner getScanner(File file) {
        Asserts.assertTrue(file != null, "Can't require a writer for an invalid file");

        L.verbose("Required Scanner for file: " + file.getAbsolutePath());

        try {

            boolean fileExists = file.exists() || file.createNewFile();

            if (fileExists)
                return new Scanner(file);
        } catch (IOException e) { /* The error is reported below */ }

        L.error("Error occurred while creating Scanner for file: " + file.getPath());
        return null;
    }
}
