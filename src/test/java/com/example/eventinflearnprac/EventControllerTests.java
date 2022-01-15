package com.example.eventinflearnprac;

import com.example.eventinflearnprac.common.RestDocsConfiguration;
import com.example.eventinflearnprac.common.TestDescription;
import com.example.eventinflearnprac.events.Event;
import com.example.eventinflearnprac.events.EventDto;
import com.example.eventinflearnprac.events.EventStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

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
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to updated new events")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type")
                        ),
                        requestFields(
                                fieldWithPath("name").description("event's name"),
                                fieldWithPath("description").description("event's detail description"),
                                fieldWithPath("beginEnrollmentDateTime").description("start of enrollment date"),
                                fieldWithPath("closeEnrollmentDateTime").description("close of enrollment date"),
                                fieldWithPath("beginEventDateTime").description("begin of event date"),
                                fieldWithPath("endEventDateTime").description("end of event date"),
                                fieldWithPath("location").description("event location(only offline not for online)"),
                                fieldWithPath("basePrice").description("minimum price of event. if other participation pay more and participation is full, less chargers are cannot join the event."),
                                fieldWithPath("maxPrice").description("maximum price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of participation")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location of response Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("event's identifier"),
                                fieldWithPath("name").description("event's name"),
                                fieldWithPath("description").description("event's detail description"),
                                fieldWithPath("beginEnrollmentDateTime").description("start of enrollment date"),
                                fieldWithPath("closeEnrollmentDateTime").description("close of enrollment date"),
                                fieldWithPath("beginEventDateTime").description("begin of event date"),
                                fieldWithPath("endEventDateTime").description("end of event date"),
                                fieldWithPath("location").description("event location(only offline not for online)"),
                                fieldWithPath("basePrice").description("minimum price of event. if other participation pay more and participation is full, less chargers are cannot join the event."),
                                fieldWithPath("maxPrice").description("maximum price of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of participation"),
                                fieldWithPath("offline").description("online if event location is null"),
                                fieldWithPath("free").description("free if basePrice and maxPrice are null"),
                                fieldWithPath("eventStatus").description("DRAFT or PUBLISHED or BEGAN_ENROLLMENT (ENUM)"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query events"),
                                fieldWithPath("_links.update-event.href").description("link to update events")
                        )
                ))
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
