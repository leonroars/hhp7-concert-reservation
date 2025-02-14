package com.dataplatform.interfaces.mock;

import com.dataplatform.interfaces.dto.ReservationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/dataplatform/api")
public class DataPlatformMockController {


    @PostMapping("/reservations")
    public ResponseEntity<String> receiveReservation(@RequestBody ReservationDto reservationDto) {
        // ConcertReservation 애플리케이션으로부터 받은 Reservation 정보를 reservationDto 에 Bind 후 출력.
        System.out.println("예약 확정 : " + reservationDto);

        // 수신 양호 응답 반환
        return ResponseEntity.ok("예약 확정 정보 수신 완료");
    }

}
