package com.hhp7.concertreservation.interfaces;

import com.hhp7.concertreservation.interfaces.dto.ReservationDto;
import com.hhp7.concertreservation.domain.reservation.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class DataPlatformSenderController {

    @Value("${data-platform.url}")
    private String dataPlatformUrl;

    private final RestTemplate restTemplate;


    public void sendReservationData(Reservation reservation) {

        ReservationDto dto = ReservationDto.fromDomain(reservation);

        restTemplate.postForObject(dataPlatformUrl, dto, String.class);
    }
}
