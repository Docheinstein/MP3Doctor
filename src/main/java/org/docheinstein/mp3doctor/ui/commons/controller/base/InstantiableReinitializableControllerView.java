package org.docheinstein.mp3doctor.ui.commons.controller.base;

/**
 * Represents an {@link InstantiableControllerView} that can
 * be reinitialized for make the view consistent with the model.
 *
 * @see InstantiableControllerView
 */
public interface InstantiableReinitializableControllerView
    extends InstantiableControllerView, Reinitializable {}
