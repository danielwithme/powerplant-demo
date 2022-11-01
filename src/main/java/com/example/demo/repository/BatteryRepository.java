package com.example.demo.repository;
/*
 * @author Daniel
 */

import com.example.demo.entity.Battery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatteryRepository extends JpaRepository<Battery, Long> {

    List<Battery> findByPostCodeBetweenOrderByName(long from, long to);
}
