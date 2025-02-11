/*
 * Copyright (c) 2008 Harold Cooper. All rights reserved.
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 */

package de.labystudio.game.libraries.org.pcollections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * A persistent vector of elements.
 *
 * <p>
 * This implementation is backed by an IntTreePMap and supports logarithmic-time querying,
 * setting, insertion, and removal.
 *
 * <p>
 * This implementation is thread-safe (assuming Java's AbstractList is thread-safe) although its
 * iterators may not be.
 *
 * <p>
 * Null values are supported.
 *
 * @author harold
 * @param <E>
 */
public class TreePVector<E> extends AbstractUnmodifiableList<E> implements PVector<E>, Serializable {

    private static final long serialVersionUID = 1L;

    //// STATIC FACTORY METHODS ////
    private static final TreePVector<Object> EMPTY = new TreePVector<Object>(IntTreePMap.empty());

    /**
     * @param <E>
     * @return an empty vector
     */
    @SuppressWarnings("unchecked")
    public static <E> TreePVector<E> empty() {
        return (TreePVector<E>) EMPTY;
    }

    /**
     * @param <E>
     * @param e
     * @return empty().plus(e)
     */
    public static <E> TreePVector<E> singleton(final E e) {
        return TreePVector.<E>empty()
            .plus(e);
    }

    /**
     * @param <E>
     * @param list
     * @return empty().plusAll(list)
     */
    @SuppressWarnings("unchecked")
    public static <E> TreePVector<E> from(final Collection<? extends E> list) {
        if (list instanceof TreePVector) return (TreePVector<E>) list; // (actually we only know it's TreePVector<?
                                                                       // extends E>)
        // but that's good enough for an immutable
        // (i.e. we can't mess someone else up by adding the wrong type to it)
        return TreePVector.<E>empty()
            .plusAll(list);
    }

    //// PRIVATE CONSTRUCTORS ////
    private final IntTreePMap<E> map;

    private TreePVector(final IntTreePMap<E> map) {
        this.map = map;
    }

    //// REQUIRED METHODS FROM AbstractList ////
    @Override
    public int size() {
        return map.size();
    }

    @Override
    public E get(final int index) {
        if (index < 0 || index >= size()) throw new IndexOutOfBoundsException();
        return map.get(index);
    }

    //// OVERRIDDEN METHODS FROM AbstractList ////
    @Override
    public Iterator<E> iterator() {
        return map.values()
            .iterator();
    }

    @Override
    public TreePVector<E> subList(int start, int end) {
        final int size = size();
        if (start < 0 || end > size || start > end) throw new IndexOutOfBoundsException();
        if (start == 0 && end == size) return this;
        if (start == end) return empty();

        // remove from end, then remove before start:
        return new TreePVector<E>(
            this.map.minusRange(end, size)
                .minusRange(0, start)
                .withKeysChangedAbove(start, -start));
    }

    //// IMPLEMENTED METHODS OF PVector ////
    public TreePVector<E> plus(final E e) {
        return new TreePVector<E>(map.plus(size(), e));
    }

    public TreePVector<E> plus(final int i, final E e) {
        if (i < 0 || i > size()) throw new IndexOutOfBoundsException();
        return new TreePVector<E>(
            map.withKeysChangedAbove(i, 1)
                .plus(i, e));
    }

    public TreePVector<E> minus(final Object e) {
        for (Entry<Integer, E> entry : map.entrySet())
            if (Objects.equals(entry.getValue(), e)) return minus((int) entry.getKey());
        return this;
    }

    public TreePVector<E> minus(final int i) {
        if (i < 0 || i >= size()) throw new IndexOutOfBoundsException();
        return new TreePVector<E>(
            map.minus(i)
                .withKeysChangedAbove(i, -1));
    }

    public TreePVector<E> plusAll(final Collection<? extends E> list) {
        TreePVector<E> result = this;
        for (E e : list) result = result.plus(e);
        return result;
    }

    public TreePVector<E> minusAll(final Collection<?> list) {
        TreePVector<E> result = this;
        for (Object e : list) result = result.minus(e);
        return result;
    }

    public TreePVector<E> plusAll(int i, final Collection<? extends E> list) {
        if (i < 0 || i > size()) throw new IndexOutOfBoundsException();
        if (list.size() == 0) return this;
        IntTreePMap<E> map = this.map.withKeysChangedAbove(i, list.size());
        for (E e : list) map = map.plus(i++, e);
        return new TreePVector<E>(map);
    }

    public PVector<E> with(final int i, final E e) {
        if (i < 0 || i >= size()) throw new IndexOutOfBoundsException();
        IntTreePMap<E> map = this.map.plus(i, e);
        if (map == this.map) return this;
        return new TreePVector<E>(map);
    }
}
