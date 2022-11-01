package com.example.demo.controller;
/*
 * @author Daniel
 */

import com.example.demo.dto.BatteryDTO;
import com.example.demo.entity.Battery;
import com.example.demo.repository.BatteryRepository;
import com.example.demo.request.BatteriesRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Objects;
import java.util.stream.DoubleStream;

import static com.example.demo.util.JsonUtils.toJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class BatteryControllerITTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BatteryRepository batteryRepository;

    private final BatteryDTO inputBatteryPostCode1 = new BatteryDTO( 1L,"Blk 123 Bishan Street 11", 100.0);

    private final BatteryDTO inputBatteryPostCode5 = new BatteryDTO( 5L,"53 Ang Mo Kio Avenue 3", 1100.0);

    private final BatteryDTO inputBatteryPostCode9 = new BatteryDTO( 9L,"Blk 145 Lorong 2 Toa Payoh", 200.0);

    @Test
    public void givenEmptyBody_whenUpsertBatteries_thenBadRequestStatusReturned() throws Exception {
        this.mockMvc.perform(post("/v1/batteries"))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void givenEmptyBatteryBody_whenUpsertBatteries_thenBadRequestStatusReturned() throws Exception {
        performPostUpsertBatteriesRequest()
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenValidBatteryPostalCode1_whenUpsertBatteries_thenUpdatedRecordInDBAndReturnNoContentStatus() throws Exception {
        performPostUpsertBatteriesRequest(inputBatteryPostCode1)
                .andExpect(status().isNoContent());

        Battery expectedBattery = new Battery(inputBatteryPostCode1);
        Battery actualBattery = batteryRepository.findById(inputBatteryPostCode1.getPostCode()).get();
        assertThat(actualBattery, equalTo(expectedBattery));
    }

    @Test
    public void given1ValidAnd3InvalidBattyObject_whenUpsertBatteries_thenUpsertOnlyValidOne() throws Exception {
        BatteryDTO invalidBatteryDTOWithoutPostCode = new BatteryDTO(null, inputBatteryPostCode5.getName(), inputBatteryPostCode5.getWattCapacity());
        BatteryDTO invalidBatteryDTOWithoutName = new BatteryDTO(inputBatteryPostCode5.getPostCode(), null, inputBatteryPostCode5.getWattCapacity());
        BatteryDTO invalidBatteryDTOWithoutWattCapacity = new BatteryDTO(inputBatteryPostCode9.getPostCode(), inputBatteryPostCode9.getName(), null);

        performPostUpsertBatteriesRequest(inputBatteryPostCode1, invalidBatteryDTOWithoutPostCode,
                invalidBatteryDTOWithoutName, invalidBatteryDTOWithoutWattCapacity)
                .andExpect(status().isNoContent());

        Battery expectedBattery = new Battery(inputBatteryPostCode1);
        Battery actualBattery = batteryRepository.findById(inputBatteryPostCode1.getPostCode()).get();
        assertThat(actualBattery, equalTo(expectedBattery));

        Long count = batteryRepository.count();
        assertThat(count, equalTo(1L));
    }

    @Test
    public void givenSamePostCodeDifferentAttributes_whenUpsertBatteries_thenOnlyLatterIsUpdatedInDB() throws Exception {
        BatteryDTO inputBatteryPostCode1Updated = new BatteryDTO( 1L,"battery2", 110.0);
        performPostUpsertBatteriesRequest(inputBatteryPostCode1, inputBatteryPostCode1Updated)
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();

        Battery expectedBattery = new Battery(inputBatteryPostCode1Updated);
        Battery actualBattery = batteryRepository.findById(inputBatteryPostCode1.getPostCode()).get();
        assertThat(actualBattery, equalTo(expectedBattery));
    }


    @Test
    public void givenUpsertBatteryPostCode1AndPostCode5AndPostCode11_whenGetBatteriesByPostCodeRange4To5_thenOnlyPostCode5Returned() throws Exception {
        long postCodeFrom=4L;
        long postCodeTo=6L;

        performPostUpsertBatteriesRequest(inputBatteryPostCode1, inputBatteryPostCode5, inputBatteryPostCode9);
        performGetBatteriesByPostCodeRangesRequest(postCodeFrom,postCodeTo)
                .andExpect(status().isOk())
                .andExpect(jsonPath("batteryNames.[0]").value(inputBatteryPostCode5.getName()))
                .andExpect(jsonPath("batteryNames.[1]").doesNotExist())
                .andExpect(jsonPath("totalWattCapacity").value(inputBatteryPostCode5.getWattCapacity()))
                .andExpect(jsonPath("avgWattCapacity").value(inputBatteryPostCode5.getWattCapacity()));

    }
    @Test
    public void givenUpsertBatteryPostCode1AndPostCode5AndPostCode11_whenGetBatteriesByPostCodeRangeFrom0_thenAllReturnedSortedByName() throws Exception {
        long postCodeFrom=0L;

        performPostUpsertBatteriesRequest(inputBatteryPostCode1, inputBatteryPostCode5, inputBatteryPostCode9);
        DoubleSummaryStatistics statistics = DoubleStream.of(inputBatteryPostCode1.getWattCapacity(), inputBatteryPostCode5.getWattCapacity(), inputBatteryPostCode9.getWattCapacity())
                        .summaryStatistics();
        performGetBatteriesByPostCodeRangesRequest(postCodeFrom,null)
                .andExpect(status().isOk())

                .andExpect(jsonPath("batteryNames.[0]").value(inputBatteryPostCode5.getName()))
                .andExpect(jsonPath("batteryNames.[1]").value(inputBatteryPostCode1.getName()))
                .andExpect(jsonPath("batteryNames.[2]").value(inputBatteryPostCode9.getName()))
                .andExpect(jsonPath("totalWattCapacity").value(statistics.getSum()))
                .andExpect(jsonPath("avgWattCapacity").value(statistics.getAverage()));

    }
    @Test
    public void givenUpsertBatteryPostCode1AndPostCode5AndPostCode11_whenGetBatteriesByPostCodeRangeTo0_thenNoneReturned() throws Exception {
        long postCodeTo=0L;

        performPostUpsertBatteriesRequest(inputBatteryPostCode1, inputBatteryPostCode5, inputBatteryPostCode9);
        performGetBatteriesByPostCodeRangesRequest(null,postCodeTo)
                .andExpect(status().isOk())

                .andExpect(jsonPath("batteryNames").isArray())
                .andExpect(jsonPath("batteryNames.[0]").doesNotExist())

                .andExpect(jsonPath("totalWattCapacity").value(0))
                .andExpect(jsonPath("avgWattCapacity").value(0));

    }

    private ResultActions performPostUpsertBatteriesRequest(BatteryDTO... batteryDTOs) throws Exception {
        List<BatteryDTO> batteries = Arrays.asList(batteryDTOs);
        BatteriesRequest request = new BatteriesRequest(batteries);

        return this.mockMvc.perform(post("/v1/batteries")
                .content(toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performGetBatteriesByPostCodeRangesRequest(Long from, Long to) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/v1/batteries");
        if (!Objects.isNull(from)){
            requestBuilder.param("post_code_from", from.toString());
        }
        if (!Objects.isNull(to)){
            requestBuilder.param("post_code_to", to.toString());
        }
        return mockMvc.perform(requestBuilder.contentType(MediaType.APPLICATION_JSON));
    }
}
