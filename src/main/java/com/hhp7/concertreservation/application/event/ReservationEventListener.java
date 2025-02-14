package com.hhp7.concertreservation.application.event;

import com.hhp7.concertreservation.domain.reservation.model.Reservation;
import com.hhp7.concertreservation.interfaces.DataPlatformSenderController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

        private final DataPlatformSenderController dataPlatformSenderController;


        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void handleReservationConfirmedEvent(ReservationConfirmationEvent event) {
            // 트랜잭션이 커밋된 후에 호출됨
            Reservation reservation = event.getConfirmedReservation();
            // 실제 데이터 플랫폼에 이벤트(혹은 API 호출) 전달
            dataPlatformSenderController.sendReservationData(reservation);
        }
}