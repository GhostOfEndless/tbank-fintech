package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_events", schema = "kudago")
public class Event extends AbstractEntity {

    @Column(name = "c_name", nullable = false)
    private String name;

    @Column(name = "c_start_date", nullable = false)
    private Instant start_date;

    @Column(name = "c_price")
    private String price;

    @Column(name = "c_free", nullable = false)
    private boolean free;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_location_id", nullable = false)
    private Location location;
}
