package com.byoutline.kickmaterial.utils;

import android.support.annotation.NonNull;

import java.util.*;

/**
 * {@link Set} that allows fast {@link #get(int)} access. <br />
 * Methods are NOT thread safe. <br />
 * Performance note: current implementation keeps references twice so using this collection for huge data
 * sets should be avoided.
 *
 * @param <T>
 */
public class OrderedSet<T> implements Set<T> {
    HashSet<T> set = new HashSet<>();
    List<T> list = new ArrayList<>();


    /**
     * @param i
     * @return
     * @see List#get(int)
     */
    public T get(int i) {
        return list.get(i);
    }

    /**
     * Ensures that collection contains only passed elements.
     *
     * @param collection new content of set
     * @return true if collection was modified, false otherwise.
     */
    public boolean setItems(Collection<? extends T> collection) {
        if (containsAll(collection) && size() == collection.size()) {
            return false;
        }
        clear();
        return addAll(collection);
    }

    @Override
    public boolean add(T t) {
        if (set.add(t)) {
            list.add(t);
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        boolean modified = false;
        for (T t : collection) {
            if (add(t)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        set.clear();
        list.clear();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return set.containsAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public boolean remove(Object o) {
        if (set.remove(o)) {
            list.remove(o);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean modified = false;
        for (Object o : collection) {
            if (remove(o)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        if (set.retainAll(collection)) {
            list.retainAll(collection);
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return list.size();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(T1[] contents) {
        return set.toArray(contents);
    }
}
