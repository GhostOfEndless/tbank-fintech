package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
@Entity
@Table(name = "t_categories", schema = "kudago")
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_seq_generator")
  @SequenceGenerator(name = "categories_seq_generator", sequenceName = "kudago.categories_seq")
  @Column(unique = true, nullable = false)
  protected Long id;

  @Column(name = "c_slug", nullable = false, unique = true)
  private String slug;

  @Column(name = "c_name", nullable = false)
  private String name;
}
