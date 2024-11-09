package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id", "location"})
@Entity
@Table(name = "t_events", schema = "kudago")
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_seq_generator")
  @SequenceGenerator(name = "events_seq_generator", sequenceName = "kudago.events_seq")
  @Column(unique = true, nullable = false)
  protected Long id;

  @Column(name = "c_name", nullable = false)
  private String name;

  @Column(name = "c_start_date", nullable = false)
  private Instant startDate;

  @Column(name = "c_price")
  private String price;

  @Column(name = "c_free", nullable = false)
  private boolean free;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "c_location_id", nullable = false)
  private Location location;
}
