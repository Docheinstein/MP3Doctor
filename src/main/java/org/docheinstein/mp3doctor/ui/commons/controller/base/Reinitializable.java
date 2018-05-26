package org.docheinstein.mp3doctor.ui.commons.controller.base;

/**
 * Represents an entity that can be reinitialized.
 * <p>
 * Entity that implements this interface should let external entities
 * to change the fields the reinitialization is based on (otherwise the
 * 'reinitialization' doesn't make much sense).
 */
public interface Reinitializable {

    /** Reinitializes this entity. */
    void reinit();
}
