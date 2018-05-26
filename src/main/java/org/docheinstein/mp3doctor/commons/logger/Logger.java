package org.docheinstein.mp3doctor.commons.logger;

import org.docheinstein.mp3doctor.commons.utils.StringUtil;
import org.docheinstein.mp3doctor.commons.utils.TimeUtil;

import java.io.PrintStream;

/**
 * Represents a logger associated with a tag (usually a class) that
 * is able to print messages with different log levels.
 */
public class Logger implements LoggerCapable {

    /** Level of logging. */
    private enum LogLevel {
        Verbose("V", System.out),
        Debug("D", System.out),
        Info("I", System.out),
        Warn("W", System.err),
        Error("E", System.err)
        ;

        LogLevel(String identifier, PrintStream stream) {
            this.identifier = identifier;
            this.stream = stream;
        }

        private String identifier;
        private PrintStream stream;
    }

    /**
     * Creates a logger and uses the class name for build the logger's tag.
     * @param clazz the class to use for define the logger tag
     * @return a logger with a tag associated with the given class
     *
     * @see #createForTag(String)
     */
    public static Logger createForClass(Class clazz) {
        return createForTag(getTagForClass(clazz));
    }

    /**
     * Creates a logger with the given tag.
     * @param tag the tag of the logger
     * @return a logger with the given tag
     *
     * @see #createForClass(Class)
     */
    public static Logger createForTag(String tag) {
        return new Logger(tag);
    }

    /**
     * Returns a tag for the given class.
     * <p>
     * This is actually created by making all upper case, separating the
     * upper case characters with underscores.
     * @param clazz the class to use for define the tag
     * @return the tag associated with the given class
     */
    public static String getTagForClass(Class clazz) {
        return "{" +
            clazz.getSimpleName().replaceAll(
                "(.)([A-Z]+)",
                "$1_$2").toUpperCase() +
            "}";
    }

    /**
     * Returns the global logger that is not associated with any tag
     * or class.
     * <p>
     * The use of this logger is not encouraged since the information
     * about the entity that prints the messages is lost.
     * @return the application global logger
     */
    public static Logger global() {
        return GLOBAL_LOGGER;
    }

    /** Application's global logger. */
    private static final Logger GLOBAL_LOGGER = createForTag("{GLOBAL}");

    /** The tag of this logger. */
    private final String mTag;

    /**
     * Creates a logger for the given tag
     * @param tag the tag of the logger
     */
    private Logger(String tag) {
        mTag = tag;
    }

    // Basic LoggerCapable log methods

    /**
     * Prints the given message as a verbose message.
     * @param message the message
     */
    @Override
    public void verbose(String message) {
        log(mTag, LogLevel.Verbose, message);
    }

    /**
     * Prints the given message as a debug message.
     * @param message the message
     */
    @Override
    public void debug(String message) {
        log(mTag, LogLevel.Debug, message);
    }

    /**
     * Prints the given message as an info message.
     * @param message the message
     */
    @Override
    public void info(String message) {
        log(mTag, LogLevel.Info, message);
    }

    /**
     * Prints the given message as a warn message.
     * @param message the message
     */
    @Override
    public void warn(String message) {
        log(mTag, LogLevel.Warn, message);
    }

    /**
     * Prints the given message as an error message.
     * @param message the message
     */
    @Override
    public void error(String message) {
        log(mTag, LogLevel.Error, message);
    }

    // Advanced log methods

    /**
     * Prints the given message and exception as a warn message.
     * @param message the message
     * @param e the exception
     */
    public void warn(String message, Exception e) {
        warn(message + "\n" + StringUtil.toString(e));
    }

    /**
     * Prints the given message and exception as an error message.
     * @param message the message
     * @param e the exception
     */
    public void error(String message, Exception e) {
        error(message + "\n" + StringUtil.toString(e));
    }

    /**
     * Prints the given message for the given log level using the given tag.
     * @param tag the tag
     * @param lv the log level
     * @param message the message
     */
    private static void log(String tag, LogLevel lv, String message) {
        lv.stream.println(
            "[" + lv.identifier + "] " +
            TimeUtil.millisToString(
                TimeUtil.Patterns.DATE_TIME,
                System.currentTimeMillis()
            ) + " " + tag + " " + message);
    }
}
