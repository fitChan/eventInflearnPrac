package com.example.eventinflearnprac;

import com.example.eventinflearnprac.events.Event;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("Inflearn Spring Rest Api")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){

        //Given
        String name = "Event";
        String description = "Spring";

        //when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);

    }

    @Test
    public void testFree(){
        //Given
        Event event = Event.builder().
                basePrice(0)
                .maxPrice(0)
                .build();
        //when
        event.update();

        //then
        assertThat(event.isFree()).isTrue();

        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        event.update();

        assertThat(event.isFree()).isFalse();

        event = Event.builder()
                .basePrice(100)
                .maxPrice(10)
                .build();

        event.update();

        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffline(){
        //Given
        Event event = Event.builder().
                location("장소있음")
                .build();
        //when
        event.update();

        assertThat(event.isOffline()).isTrue();

        event = Event.builder().
                build();
        //when
        event.update();

        assertThat(event.isOffline()).isFalse();
    }

}
