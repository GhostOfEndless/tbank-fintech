package com.example.collection;

import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomLinkedListTest {

    private CustomLinkedList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new CustomLinkedList<>();
    }

    @Nested
    @Tag("BasicOperations")
    @DisplayName("Basic operations")
    class BasicOperationsTest {

        @Test
        @DisplayName("Add a single element to the list and verify it's correctly added")
        void testAddSingleElement() {
            assertTrue(list.add(1));
            assertEquals(1, list.get(0));
        }

        @Test
        @DisplayName("Add multiple elements and verify their order in the list")
        void testAddMultipleElements() {
            list.add(1);
            list.add(2);
            list.add(3);
            assertEquals(1, list.get(0));
            assertEquals(2, list.get(1));
            assertEquals(3, list.get(2));
        }

        @Test
        @DisplayName("Remove an element by valid index and check the list updates correctly")
        void testRemoveValidIndex() {
            list.add(1);
            list.add(2);
            assertEquals(1, list.remove(0));
            assertEquals(2, list.get(0));
        }
    }

    @Nested
    @Tag("EdgeCases")
    @DisplayName("Edge cases")
    class EdgeCasesTest {

        @Test
        @DisplayName("Attempt to get an element with an invalid index and expect an exception")
        void testGetWithInvalidIndex() {
            list.add(1);
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        }

        @Test
        @DisplayName("Attempt to remove an element with an invalid index and expect an exception")
        void testRemoveInvalidIndex() {
            list.add(1);
            assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
        }

        @Test
        @DisplayName("Check if a non-existing element is not found in an empty list")
        void testContainsEmptyList() {
            assertFalse(list.contains(1));
        }
    }

    @Nested
    @Tag("CollectionOperations")
    @DisplayName("Collection operations")
    class CollectionOperationsTest {

        @Test
        @DisplayName("Add a collection of elements to the list and verify their order")
        void testAddAll() {
            list.addAll(List.of(1, 2, 3));
            assertEquals(1, list.get(0));
            assertEquals(2, list.get(1));
            assertEquals(3, list.get(2));
        }

        @Test
        @DisplayName("Add an empty collection and expect no elements in the list")
        void testAddAllEmpty() {
            assertTrue(list.addAll(Collections.emptyList()));
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        }

        @Test
        @DisplayName("Check if an existing element is present in the list")
        void testContainsElementFound() {
            list.add(1);
            list.add(2);
            assertTrue(list.contains(2));
        }

        @Test
        @DisplayName("Check if a non-existing element is not present in the list")
        void testContainsElementNotFound() {
            list.add(1);
            list.add(2);
            assertFalse(list.contains(3));
        }
    }

    @Nested
    @Tag("UtilityMethods")
    @DisplayName("Utility methods")
    class UtilityMethodsTest {

        @Test
        @DisplayName("Convert an empty list to a string representation")
        void testToStringEmptyList() {
            assertEquals("[]", list.toString());
        }

        @Test
        @DisplayName("Convert a list with elements to a string representation")
        void testToStringWithElements() {
            list.add(1);
            list.add(2);
            list.add(3);
            assertEquals("[1, 2, 3]", list.toString());
        }

        @Test
        @DisplayName("Verify size of an empty list is zero")
        void testSizeEmptyList() {
            assertEquals(0, list.size());
        }

        @Test
        @DisplayName("Verify size after adding one element")
        void testSizeAfterAddingOneElement() {
            list.add(1);
            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("Verify size after removing an element")
        void testSizeAfterRemovingElement() {
            list.add(1);
            list.add(2);
            list.remove(0);
            assertEquals(1, list.size());
        }
    }
}