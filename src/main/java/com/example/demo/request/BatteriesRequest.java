package com.example.demo.request;
/*
 * @author Daniel
 */

import com.example.demo.dto.BatteryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BatteriesRequest {
    private List<BatteryDTO> batteries;
}
