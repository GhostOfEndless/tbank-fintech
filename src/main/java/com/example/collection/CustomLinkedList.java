package com.example.collection;

import java.util.Iterator;
import java.util.Objects;

public class CustomLinkedList<E> implements Iterable<E> {

    private class Node {
        public E data;
        public Node next;

        public Node(E data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;  // reference to the first node
    private int size; // keeps track of the number of elements

    public CustomLinkedList() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Adds an element to the end of the list.
     *
     * @param element the element to be added to the list
     * @return true
     */
    public boolean add(E element) {
        if (this.head == null) {
            this.head = new Node(element);
        } else {
            Node node = this.head;
            // loop until the last node
            while (node.next != null) {
                node = node.next;
            }
            node.next = new Node(element);
        }
        this.size++;
        return true;
    }

    /**
     * Returns the element at the specified index.
     *
     * @param index the index of the element to retrieve
     * @return the element at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public E get(int index) {
        Node node = getNode(index);
        return node.data;
    }

    /**
     * Removes the element at the specified index from the list.
     *
     * @param index the index of the element to be removed
     * @return the removed element
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public E remove(int index) {
        E element = get(index);

        if (index == 0) {
            this.head = this.head.next;
        } else {
            Node node = getNode(index);
            this.head.next = node.next;
        }
        this.size--;

        return element;
    }

    /**
     * Adds all elements from the specified collection to this list.
     * Each element from the collection is added to the end of the list.
     *
     * @param collection the collection containing elements to be added to the list
     * @return {@code true} if all elements were successfully added to the list, {@code false} otherwise
     * @throws NullPointerException if the specified collection is {@code null}
     */
    public boolean addAll(Iterable<? extends E> collection) {
        boolean flag = true;
        for (E element : collection) {
            flag &= add(element);
        }
        return flag;
    }

    /**
     * Checks if the list contains the specified element.
     *
     * @param obj the element to be checked for presence in the list
     * @return {@code true} if the element is found in the list, {@code false} otherwise
     */
    public boolean contains(Object obj) {
        Node node = this.head;
        while (node != null) {
            if (Objects.equals(obj, node.data)) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    /**
     * Returns the current size of linked list
     *
     * @return the current size of linked list
     */
    public int size() {
        return this.size;
    }

    /**
     * Creates a new {@code CustomLinkedList} containing the specified elements.
     *
     * @param elements the elements to be added to the list
     * @return a new {@code CustomLinkedList} with the specified elements
     * @throws NullPointerException if any of the elements is null
     */
    @SafeVarargs
    public static <E> CustomLinkedList<E> of(E... elements) {
        CustomLinkedList<E> list = new CustomLinkedList<>();
        for (E element : elements) {
            list.add(element);
        }
        return list;
    }

    /**
     * Returns the node at the given index.
     *
     * @param index the index of the node to retrieve
     * @return the node at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    private Node getNode(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index %d out of bounds".formatted(index));
        }
        Node node = this.head;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node;
    }

    /**
     * Returns an iterator over the elements in this linked list.
     * It provides methods to check if more elements are available ({@code hasNext()})
     * and to retrieve the next element ({@code next()}).
     *
     * @return an {@code Iterator<E>} over the elements in this list
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private Node current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                E data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    /**
     * Returns a string representation of the linked list.
     * Example: {@code [element1, element2, element3]}.
     *
     * @return a string representation of the linked list
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node node = this.head;
        sb.append("[");
        while (node != null) {
            sb.append(node.data);
            if (node.next != null) {
                sb.append(", ");
            }
            node = node.next;
        }
        sb.append("]");
        return sb.toString();
    }
}
