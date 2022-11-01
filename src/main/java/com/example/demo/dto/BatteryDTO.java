package com.example.demo.dto;
/*
 * @author Daniel
 */

import com.example.demo.entity.Battery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BatteryDTO {
    private Long postCode;
    private String name;
    private Double wattCapacity;

    public BatteryDTO(Battery battery){
        this(battery.getPostCode(), battery.getName(), battery.getWattCapacity());
    }
    @JsonIgnore
    public boolean isValid(){
        return Objects.nonNull(this.getName()) && Objects.nonNull(this.getPostCode()) && Objects.nonNull(this.getWattCapacity());
    }
}
