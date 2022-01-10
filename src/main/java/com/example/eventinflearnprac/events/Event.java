package com.example.eventinflearnprac.events;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
/*
docker run --name rest -p 1234:1234 -e POSTGRES_HOST_AUTH_MET
        HOD=trust -d postgres

        도커 설정
*/

public class Event {
    @Id@GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;

    private String location;

    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    public void updateFree() {
        if(this.basePrice == 0 && this.maxPrice == 0){
            this.free = true;
        }else {
            this.free = false;
        }
    }

    public void updateOffline(){
        if(this.location.isBlank() || this.location == null){
            this.offline = false;
        }else {
            this.offline=true;
        }
    }

}
