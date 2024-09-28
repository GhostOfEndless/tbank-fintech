package com.example.repository;

import com.example.entity.Location;
import com.example.repository.impl.InMemoryRepositoryImpl;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class InMemoryRepositoryTest {

    private InMemoryRepositoryImpl<Location> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepositoryImpl<>();
    }

    @Test
    @Tag("FindAll")
    @DisplayName("FindAll should return all saved entities")
    void findAll_notEmpty() {
        var locationOne = new Location();
        var locationTwo = new Location();
        repository.save(locationOne);
        repository.save(locationTwo);

        var result = repository.findAll();

        assertThat(result)
                .hasSize(2)
                .contains(locationOne, locationTwo);
    }

    @Nested
    @Tag("FindById")
    @DisplayName("FindById method tests")
    class FindByIdTest {

        @Test
        @DisplayName("FindById should return empty when entity not found")
        void findById_notFound() {
            var result = repository.findById(1L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("FindById should return the correct entity when found")
        void findById_success() {
            var location = new Location();
            repository.save(location);

            var result = repository.findById(location.getId());

            assertThat(result)
                    .isPresent()
                    .contains(location);
        }
    }

    @Nested
    @Tag("FindById")
    @DisplayName("Save method tests")
    class SaveTest {

        @Test
        @DisplayName("Save should successfully store a new entity")
        void save_success() {
            var location = new Location();

            var savedLocation = repository.save(location);

            assertThat(savedLocation.getId()).isNotNull();
            assertThat(repository.findAll()).hasSize(1);
        }


        @Test
        @DisplayName("Save should successfully update an existing entity")
        void save_updatedSuccess() {
            var location = new Location();
            repository.save(location);
            location.setName("Updated");

            var updatedLocation = repository.save(location);

            assertThat(updatedLocation.getName()).isEqualTo("Updated");
            assertThat(repository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("Save should throw IllegalArgumentException when entity is null")
        void save_entityIsNull() {
            assertThatThrownBy(() -> repository.save(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Entity cannot be null!");
        }
    }

    @Test
    @Tag("Delete")
    @DisplayName("Delete should remove the entity from repository")
    void delete_success() {
        var location = new Location();
        repository.save(location);

        repository.delete(location.getId());

        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @Tag("existsById")
    @DisplayName("ExistsById should return true for existing and false for non-existing entities")
    void existsById_existedAndNotExisted() {
        var location = new Location();
        repository.save(location);

        assertThat(repository.existsById(location.getId())).isTrue();
        assertThat(repository.existsById(location.getId() + 1)).isFalse();
    }
}
