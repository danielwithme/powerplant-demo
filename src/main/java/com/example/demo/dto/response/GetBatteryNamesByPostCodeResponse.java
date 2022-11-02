package com.example.demo.dto.response;
/*
 * @author Daniel
 */

import com.example.demo.dto.BatteryDTO;
import lombok.Getter;

import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.Collectors;

@Getter
public class GetBatteryNamesByPostCodeResponse {
    private final List<String> batteryNames;
    private final long totalWattCapacity;
    private final double avgWattCapacity;
    public GetBatteryNamesByPostCodeResponse(List<BatteryDTO> batteries){
        this.batteryNames = batteries.stream().map(BatteryDTO::getName).collect(Collectors.toList());
        LongSummaryStatistics statistic = batteries.stream().map(BatteryDTO::getWattCapacity)
                .mapToLong(Double::longValue)
                .summaryStatistics();
        totalWattCapacity = statistic.getSum();
        avgWattCapacity = statistic.getAverage();
    }
}
