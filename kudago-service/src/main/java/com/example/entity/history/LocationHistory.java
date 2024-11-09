package com.example.entity.history;

import com.example.entity.CrudAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "t_locations_history", schema = "kudago")
public class LocationHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locations_history_seq_generator")
  @SequenceGenerator(name = "locations_history_seq_generator", sequenceName = "kudago.locations_history_seq")
  @Column(unique = true, nullable = false)
  protected Long id;

  @Column(name = "c_location_id", nullable = false)
  private Long locationId;

  @Column(name = "c_slug", nullable = false)
  private String slug;

  @Column(name = "c_name", nullable = false)
  private String name;

  @Column(name = "c_timestamp", nullable = false)
  private LocalDateTime timestamp;

  @Enumerated(EnumType.STRING)
  @Column(name = "c_action", nullable = false)
  private CrudAction action;
}
