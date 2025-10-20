package com.company.bus_mgmt.domain.schedule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(
        name = "route_stops",
        uniqueConstraints = @UniqueConstraint(name = "uq_route_stop_order", columnNames = { "route_id", "stop_order" }),
        indexes = { @Index(name = "ix_route_stop_route", columnList = "route_id"),
                @Index(name = "ix_route_stop_stop", columnList = "stop_id") }
)
public class RouteStop {

    // getters/setters
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id")
    private Route route;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "stop_id")
    private Stop stop;

    @Setter
    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    @Setter
    @Column(name = "arrival_offset_min", nullable = false)
    private Integer arrivalOffsetMin;

}
