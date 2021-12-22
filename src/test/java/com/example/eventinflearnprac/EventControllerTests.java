package com.example.eventinflearnprac;

import com.example.eventinflearnprac.common.TestDescription;
import com.example.eventinflearnprac.events.Event;
import com.example.eventinflearnprac.events.EventDto;
import com.example.eventinflearnprac.events.EventStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;



    @Test
    @TestDescription("정상적인 이벤트 생성")
    public void createEvent() throws Exception {

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 20))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 20))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 12, 10))
                .endEventDateTime(LocalDateTime.of(2019, 11, 25, 12, 10))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("관악구 봉천동 869")
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
//                .andExpect(jsonPath("offline").value(Matchers.not(false)))
        ;
    }


    @Test
    @TestDescription("입력 받을 수 없는 값이 들어온 이벤트 태스트 (에러)")
    public void createEvent_BadRequest() throws Exception {

        Event event = Event.builder()
                .id(100) //unknown Properties
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 20))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 20))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 12, 10))
                .endEventDateTime(LocalDateTime.of(2019, 11, 25, 12, 10))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("관악구 봉천동 869")
                .free(true) //unknown Properties
                .offline(false) //unknown Properties
                .eventStatus(EventStatus.PUBLISHED) //unknown Properties
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력값이 비어있는 이벤트 태스트 (에러)")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못된 이벤트 태스트 (에러)")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {

        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 11, 23, 14, 20))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 20))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 12, 10))
                .endEventDateTime(LocalDateTime.of(2017, 11, 25, 12, 10))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("관악구 봉천동 869")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
//                .andExpect(jsonPath("$[0].field").exists())   -> 필드error일 경우에는 태스트가 정상 작동하지만 글로벌 애러의 경우 필드가 없기 때문에 태스트가 깨져요
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
//                .andExpect(jsonPath("$[0].rejectedValue").exists()) -> 필드error일 경우에는 태스트가 정상 작동하지만 글로벌 애러의 경우 rejectedValue가 없기 때문에 태스트가 깨져요
        ;
    }
}
