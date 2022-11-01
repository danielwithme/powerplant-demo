package com.example.demo.controller;
/*
 * @author Daniel
 */

import com.example.demo.dto.BatteryDTO;
import com.example.demo.request.BatteriesRequest;
import com.example.demo.response.GetBatteryNamesByPostCodeResponse;
import com.example.demo.service.BatteryService;
import com.example.demo.util.LongUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/batteries")
public class BatteryController {

    private BatteryService batteryService;

    @PostMapping()
    public @ResponseBody ResponseEntity upsertBatteries(@RequestBody BatteriesRequest request) {
        if (request.getBatteries().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty body, please input the valid battery list");
        }
        List<BatteryDTO> batteryDTOs = request.getBatteries().stream().filter(BatteryDTO::isValid)
                .collect(Collectors.toList());

        batteryService.upsertAll(batteryDTOs);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping()
    public @ResponseBody ResponseEntity<GetBatteryNamesByPostCodeResponse> getBatteries(@RequestParam(value="post_code_from", required = false ) Long postCodeFromParam,
                                                                                        @RequestParam(value="post_code_to", required = false ) Long postCodeToParam) {
        long postCodeFrom = LongUtils.getOrDefault(postCodeFromParam, Long.MIN_VALUE);
        long postCodeTo = LongUtils.getOrDefault(postCodeToParam, Long.MAX_VALUE);

        List<BatteryDTO> batteries = batteryService
                .getBatteriesByPostCode(postCodeFrom, postCodeTo);

        return ResponseEntity.status(HttpStatus.OK).body(new GetBatteryNamesByPostCodeResponse(batteries));
    }

}
