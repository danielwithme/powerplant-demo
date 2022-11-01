package com.example.demo.entity;
/*
 * @author Daniel
 */

import com.example.demo.dto.BatteryDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="battery")
@Data
@NoArgsConstructor
public class Battery {
    @Id
    private long postCode;
    private String name;
    private double wattCapacity;

    public Battery(BatteryDTO battery){
        this();
        postCode = battery.getPostCode();
        name = battery.getName();
        wattCapacity = battery.getWattCapacity();
    }

    @Override
    public boolean equals(Object o){
        if (o == this)
            return true;
        if (!(o instanceof Battery))
            return false;
        Battery that = (Battery) o;
        return new EqualsBuilder()
                .append(this.postCode, that.postCode)
                .append(this.wattCapacity, that.getWattCapacity())
                .append(this.name, that.name)
                .isEquals();
    }
}