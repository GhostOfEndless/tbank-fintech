package com.example.collection;

import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
            assertThat(list.add(1)).isTrue();
            assertThat(list.get(0)).isEqualTo(1);
        }

        @Test
        @DisplayName("Add multiple elements and verify their order in the list")
        void testAddMultipleElements() {
            list.addAll(CustomLinkedList.of(1, 2, 3));
            assertThat(list).containsExactlyElementsOf(CustomLinkedList.of(1, 2, 3));
        }

        @Test
        @DisplayName("Remove an element by valid index and check the list updates correctly")
        void testRemoveValidIndex() {
            list.addAll(CustomLinkedList.of(1, 2));
            assertThat(list.remove(0)).isEqualTo(1);
            assertThat(list.get(0)).isEqualTo(2);
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
            assertThatThrownBy(() -> list.get(1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("Attempt to remove an element with an invalid index and expect an exception")
        void testRemoveInvalidIndex() {
            list.add(1);
            assertThatThrownBy(() -> list.remove(1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("Check if a non-existing element is not found in an empty list")
        void testContainsEmptyList() {
            assertThat(list).doesNotContain(1);
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
            assertThat(list).containsExactlyElementsOf(List.of(1, 2, 3));
        }

        @Test
        @DisplayName("Add an empty collection and expect no elements in the list")
        void testAddAllEmpty() {
            assertThat(list.addAll(Collections.emptyList())).isTrue();
            assertThatThrownBy(() -> list.get(0))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("Check if an existing element is present in the list")
        void testContainsElementFound() {
            list.addAll(CustomLinkedList.of(1, 2));
            assertThat(list).contains(2);
        }

        @Test
        @DisplayName("Check if a non-existing element is not present in the list")
        void testContainsElementNotFound() {
            list.addAll(CustomLinkedList.of(1, 2));
            assertThat(list).doesNotContain(3);
        }
    }

    @Nested
    @Tag("UtilityMethods")
    @DisplayName("Utility methods")
    class UtilityMethodsTest {

        @Test
        @DisplayName("Convert an empty list to a string representation")
        void testToStringEmptyList() {
            assertThat(list).hasToString("[]");
        }

        @Test
        @DisplayName("Convert a list with elements to a string representation")
        void testToStringWithElements() {
            list.addAll(CustomLinkedList.of(1, 2, 3));
            assertThat(list).hasToString("[1, 2, 3]");
        }

        @Test
        @DisplayName("Verify size of an empty list is zero")
        void testSizeEmptyList() {
            assertThat(list).isEmpty();
        }

        @Test
        @DisplayName("Verify size after adding one element")
        void testSizeAfterAddingOneElement() {
            list.add(1);
            assertThat(list).hasSize(1);
        }

        @Test
        @DisplayName("Verify size after removing an element")
        void testSizeAfterRemovingElement() {
            list.addAll(CustomLinkedList.of(1, 2));
            list.remove(0);
            assertThat(list).hasSize(1);
        }
    }
}