package org.docheinstein.mp3doctor.commons.utils;


import java.io.PrintWriter;
import java.io.StringWriter;

/** Contains utilities for strings. */
public class StringUtil {

    /**
     * Returns whether all the given strings are valid.
     * @param strs the string to check
     * @return whether the strings are valid
     *
     * @see #isValid(String)
     */
    public static boolean areValid(String... strs) {
        for (String str : strs) {
            if (!isValid(str))
                return false;
        }
        return true;
    }

    /**
     * Returns whether the string is valid; i.e. not null nor empty.
     * @param str the string to check
     * @return whether the string is valid
     *
     * @see #areValid(String...)
     */
    public static boolean isValid(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * Returns the stack trace of the exception as a string.
     * <p>
     * This is convenient for print the exception to any stream.
     * @param e an exception
     * @return the stack trace of the exception
     */
    public static String toString(Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    /**
     * Returns whether a string contains another string without
     * evaluating the case of those.
     * @param outer the outer string
     * @param inner the inner string which must be contained inside the outer
     * @return whether the outer string contains the inner string, case insensitive
     */
    public static boolean containsIgnoreCase(String outer, String inner) {
        return outer.toLowerCase().contains(inner.toLowerCase());
    }

    /**
     * Returns the given string if it is not null or
     * an empty string if it is null.
     * @param s a string
     * @return the string or "" if it is null
     */
    public static String toEmptyIfNull(String s) {
        return s != null ? s : "";
    }

    /**
     * Ensures that the given strings ends with the given trailing
     * pattern.
     * <p>
     * The string is returned unchanged if it already ends with the
     * trailing pattern. A new string that is the concatenation of
     * the base string with the trailing pattern is returned otherwise.
     * @param string the string
     * @param trailing the trailing pattern
     * @return a string that ends with the given pattern
     */
    public static String ensureTrailing(String string, String trailing) {
        return string.endsWith(trailing) ? string : string + trailing;
    }
}
