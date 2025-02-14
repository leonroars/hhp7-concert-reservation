package com.hhp7.concertreservation.application.event;

import com.hhp7.concertreservation.domain.reservation.model.Reservation;

/**
 * 예약 확정 이벤트.
 * <br></br>
 * 해당 이벤트는 좌석 확정 트랜잭션이 온전히 Commit 된 이후에 발생.
 */
public class ReservationConfirmationEvent {

    private final Reservation confirmedReservation;

    public ReservationConfirmationEvent(Reservation confirmedReservation) {
        this.confirmedReservation = confirmedReservation;
    }

    public Reservation getConfirmedReservation() {
        return confirmedReservation;
    }
}
