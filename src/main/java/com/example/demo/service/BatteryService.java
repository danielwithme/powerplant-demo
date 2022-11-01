package com.example.demo.service;
/*
 * @author Daniel
 */

import com.example.demo.dto.BatteryDTO;
import com.example.demo.entity.Battery;
import com.example.demo.repository.BatteryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BatteryService {
    BatteryRepository batteryRepository;

    public List<BatteryDTO> getBatteriesByPostCode(long postCodeFrom, long postCodeTo) {
        List<Battery> batteries = batteryRepository.findByPostCodeBetweenOrderByName(postCodeFrom, postCodeTo);
        return batteries.stream()
                .map(BatteryDTO::new)
                .collect(Collectors.toList());
    }

    public void upsertAll(List<BatteryDTO> batteryDTOs) {
        List<Battery> batteries = batteryDTOs.stream().map(Battery::new).collect(Collectors.toList());
        batteryRepository.saveAll(batteries);
    }
}
