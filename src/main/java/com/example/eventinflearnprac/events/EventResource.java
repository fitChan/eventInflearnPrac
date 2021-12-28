package com.example.eventinflearnprac.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;

public class EventResource extends EntityModel<Event> {

    @JsonUnwrapped
    private final Event event;

    public EventResource(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
