package org.deidentifier.arx.algorithm;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class MinMaxPriorityQueue<T> extends PriorityQueue<T> {

    /** SVUID */
    private static final long      serialVersionUID = -3114256836247244397L;

    private final PriorityQueue<T> queue;
    private final PriorityQueue<T> inverseQueue;

    public MinMaxPriorityQueue(int initialSize, Comparator<T> comparator) {
        this.queue = new PriorityQueue<T>(initialSize, comparator);
        this.inverseQueue = new PriorityQueue<T>(initialSize, getInverseComparator(comparator));
    }

    private Comparator<? super T> getInverseComparator(final Comparator<T> comparator) {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return -comparator.compare(o1, o2);
            }
        };
    }

    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    public T remove() {
        throw new UnsupportedOperationException();
    }

    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    public T element() {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean offer(T e) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public T peek() {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the object from both queue and the inverseQueue
     */
    public boolean remove(Object o) {
        return queue.remove(o) && inverseQueue.remove(0);
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        throw new UnsupportedOperationException();
    }

    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    public Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public Comparator<? super T> comparator() {
        throw new UnsupportedOperationException();
    }

    public boolean add(T e) {
        inverseQueue.add(e);
        return queue.add(e);
    }

    public T poll() {
        T t = queue.poll();
        inverseQueue.remove(t);
        return t;
    }

    public T removeTail() {
        T t = inverseQueue.poll();
        queue.remove(t);
        return t;
    }
}
