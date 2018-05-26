package org.docheinstein.mp3doctor.commons.logger;

/**
 * Represents the capabilities that a logger must have for prints messages.
 */
public interface LoggerCapable {

    /**
     * Prints the given message as a verbose message.
     * @param message the message
     */
    void verbose(String message);

    /**
     * Prints the given message as a debug message.
     * @param message the message
     */
    void debug(String message);

    /**
     * Prints the given message as an info message.
     * @param message the message
     */
    void info(String message);

    /**
     * Prints the given message as a warn message.
     * @param message the message
     */
    void warn(String message);

    /**
     * Prints the given message as an error message.
     * @param message the message
     */
    void error(String message);
}
