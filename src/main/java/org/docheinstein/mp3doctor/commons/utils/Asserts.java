package org.docheinstein.mp3doctor.commons.utils;

import org.docheinstein.mp3doctor.commons.logger.Logger;
import org.docheinstein.mp3doctor.commons.logger.LoggerCapable;

/**
 * Contains typically usually used asserts.
 * The asserts check a condition an throws a {@link AssertionError} if
 * the condition does not satisfy the assertion.
 */
public class Asserts {

    /**
     * Asserts that the given object is not null.
     * @param object the object that must be not null
     * @param message the message to show if the assertion fails
     *
     * @see #assertNotNull(Object, String, LoggerCapable)
     */
    public static void assertNotNull(Object object, String message) {
        assertTrue(object != null, message);
    }

    /**
     * Asserts that the given object is not null.
     * @param message the message to show if the assertion fails
     * @param logger the logger responsible for print the exception cause
     * @param object the object that must be not null
     *
     * @see #assertNotNull(Object, String)
     */
    public static void assertNotNull(Object object, String message, LoggerCapable logger) {
        assertTrue(object != null, message, logger);
    }

    /**
     * Asserts that the given condition is false.
     * @param condition the condition that must be false
     * @param message the message to show if the assertion fails
     *
     *
     * @see #assertFalse(boolean, String, LoggerCapable)
     */
    public static void assertFalse(boolean condition, String message) {
        assertBoolean(false, condition, message);
    }

    /**
     * Asserts that the given condition is false.
     * @param logger the logger responsible for print the exception cause
     * @param condition the condition that must be false
     * @param message the message to show if the assertion fails
     *
     * @see #assertFalse(boolean, String)
     */
    public static void assertFalse(boolean condition, String message, LoggerCapable logger) {
        assertBoolean(false, condition, message, logger);
    }

    /**
     * Asserts that the given condition is true.
     * @param condition the condition that must be true
     * @param message the message to show if the assertion fails
     *
     * @see #assertTrue(boolean, String, LoggerCapable)
     */
    public static void assertTrue(boolean condition, String message) {
        assertBoolean(true, condition, message);
    }

    /**
     * Asserts that the given condition is true.
     * @param logger the logger responsible for print the exception cause
     * @param condition the condition that must be true
     * @param message the message to show if the assertion fails
     *
     * @see #assertTrue(boolean, String)
     */
    public static void assertTrue(boolean condition, String message, LoggerCapable logger) {
        assertBoolean(true, condition, message, logger);
    }

    /**
     * Asserts that given condition matches the expected condition.
     * @param expected the expected value
     * @param condition the condition that must match the expected value
     * @param message the message to show if the assertion fails
     *
     * @see #assertBoolean(boolean, boolean, String, LoggerCapable)
     */
    public static void assertBoolean(boolean expected, boolean condition, String message) {
        assertBoolean(expected, condition, message, Logger.global());
    }

    /**
     * Asserts that given condition matches the expected condition.
     * @param logger the logger responsible for print the exception cause
     * @param expected the expected value
     * @param condition the condition that must match the expected value
     * @param message the message to show if the assertion fails
     *
     * @see #assertBoolean(boolean, boolean, String)
     */
    public static void assertBoolean(boolean expected, boolean condition,
                                     String message, LoggerCapable logger) {
        if (expected != condition)
            throwAssertionError(logger, message);
    }

    /**
     * Throws an exception of type {@link AssertionError}.
     * @param logger the logger responsible for print the exception cause
     * @param message the details of the assertion failure
     */
    private static void throwAssertionError(LoggerCapable logger, String message) {
        logger.error("Throwing exception; an assertion has failed: " + message);
        throw new AssertionError(message);
    }
}
