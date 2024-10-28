package com.example.collection;

import java.util.function.Consumer;

public interface CustomIterator<E> {
    /**
     * Returns true if the iteration has more elements.
     *
     * @return true if the iteration has more elements
     */
    boolean hasNext();

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws java.util.NoSuchElementException if the iteration has no more elements
     */
    E next();

    /**
     * Performs the given action for each remaining element until all elements
     * have been processed or the action throws an exception.
     *
     * @param action The action to be performed for each element
     * @throws NullPointerException if the specified action is null
     */
    void forEachRemaining(Consumer<? super E> action);
}
