package com.company.bus_mgmt.repository.schedule;

import com.company.bus_mgmt.domain.schedule.Route;
import com.company.bus_mgmt.domain.schedule.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
    List<RouteStop> findByRouteOrderByStopOrderAsc(Route route);
}
