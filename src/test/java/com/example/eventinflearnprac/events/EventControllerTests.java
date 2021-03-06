package com.example.eventinflearnprac.events;

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
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    EventRepository eventRepository;

    @Test
    @TestDescription("???????????? ????????? ??????")
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
                .location("????????? ????????? 869")
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

                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to updated new events"),
                                linkWithRel("profile").description("link profile")
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
                        relaxedResponseFields(
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
                                fieldWithPath("_links.update-event.href").description("link to update events"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }


    @Test
    @TestDescription("?????? ?????? ??? ?????? ?????? ????????? ????????? ????????? (??????)")
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
                .location("????????? ????????? 869")
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
    @TestDescription("???????????? ???????????? ????????? ????????? (??????)")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("???????????? ????????? ????????? ????????? (??????)")
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
                .location("????????? ????????? 869")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
//                .andExpect(jsonPath("errors[0].field").exists())   -> ??????error??? ???????????? ???????????? ?????? ??????????????? ????????? ????????? ?????? ????????? ?????? ????????? ???????????? ?????????
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
//                .andExpect(jsonPath("errors[0].rejectedValue").exists()) -> ??????error??? ???????????? ???????????? ?????? ??????????????? ????????? ????????? ?????? rejectedValue??? ?????? ????????? ???????????? ?????????
                .andExpect(jsonPath("_links.index").exists())
        ;
    }



    private Event generateEvent(int i) {
        Event event = Event.builder()
                .name("event" + i)
                .description("test event generate (i)")
                .build();

        return this.eventRepository.save(event);
    }


    @Test
    @TestDescription("30?????? ???????????? 10?????? ????????? ????????? ????????????")
    public void queryEvents() throws Exception {

        //Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        //when
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
        ;

    }

    @Test
    @TestDescription("?????? ????????? ????????? 404 ????????????")
    public void getEvent404() throws Exception{
        this.mockMvc.perform(get("/api/events/1231231"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("?????? ????????? ??? ?????? ????????????.")
    public void queryOneEvents() throws Exception{
        //Given
        Event event = this.generateEvent(100);

        //when
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                ;
    }

}
