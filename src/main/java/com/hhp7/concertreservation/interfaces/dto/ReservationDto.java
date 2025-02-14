package com.hhp7.concertreservation.interfaces.dto;

import com.hhp7.concertreservation.domain.reservation.model.Reservation;
import lombok.Data;

@Data
public class ReservationDto {
    private String id;
    private String concertScheduleId;
    private String userId;
    private String seatId;

    public Reservation toDomain(){
        return Reservation.create(concertScheduleId, userId, seatId);
    }

    public static ReservationDto fromDomain(Reservation reservation){
        ReservationDto dto = new ReservationDto();
        dto.id = reservation.getId();
        dto.concertScheduleId = reservation.getConcertScheduleId();
        dto.userId = reservation.getUserId();
        dto.seatId = reservation.getSeatId();
        return dto;
    }
}