package com.dataplatform.interfaces.dto;

import lombok.Data;

@Data
public class ReservationDto {
    private String id;
    private String concertScheduleId;
    private String userId;
    private String seatId;
}