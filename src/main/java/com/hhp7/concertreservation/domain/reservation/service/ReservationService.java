package com.hhp7.concertreservation.domain.reservation.service;

import com.hhp7.concertreservation.domain.reservation.model.Reservation;
import com.hhp7.concertreservation.domain.reservation.model.ReservationStatus;
import com.hhp7.concertreservation.domain.reservation.repository.ReservationRepository;
import com.hhp7.concertreservation.exceptions.UnavailableRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    /**
     * 예약을 생성합니다. 생성 시 예약 상테는 {@code ReservationStatus.BOOKED} 입니다.
     * <br></br>
     * 또한, 도메인 모델 인스턴스 생성 당시엔 확정이 불가했던 expiredAt 필드를 초기화한 후 명시적으로 한 번 더 저장하여 갱신해줍니다.
     * <br></br>
     * 예약 생성 시 중복 검증을 시행합니다. 중복 시 {@code UnavailableRequestException} 발생합니다.
     * <br>
     * 이때 중복이란, 해당 좌석에 대해 {@code ReservationStatus.EXPIRED} 혹은 {@code ReservationStatus.CANCELLED} 가 아닌 예약이 이미 존재하는 경우를 의미합니다.
     * @param userId
     * @param concertScheduleId
     * @param seatId
     * @return
     */
    public Reservation createReservation(String userId, String concertScheduleId, String seatId) {
        reservationRepository.findByConcertScheduleIdAndSeatId(concertScheduleId, seatId)
                .filter(reservation ->
                        reservation.getStatus() != ReservationStatus.EXPIRED &&
                                reservation.getStatus() != ReservationStatus.CANCELLED
                )
                .ifPresent(reservation -> {
                    throw new UnavailableRequestException("해당 좌석에 대한 예약이 이미 존재하므로 예약이 불가합니다.");
                });

        Reservation reservation = Reservation.create(userId, seatId, concertScheduleId);
        Reservation savedReservation = reservationRepository.save(reservation);
        savedReservation.initiateExpiredAt();
        return reservationRepository.save(savedReservation);
    }

    /**
     * 예약 ID로 예약 조회
     * @param reservationId
     * @return
     */
    public Reservation getReservation(String reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new UnavailableRequestException("해당 예약이 존재하지 않습니다."));
    }

    /**
     * 특정 사용자의 예약 전체 조회
     * @param userId
     * @return
     */
    public List<Reservation> getUserReservation(String userId) {
        List<Reservation> reservations =  reservationRepository.findByUserId(userId);
        if(reservations.isEmpty()){
            throw new UnavailableRequestException("해당 사용자의 예약이 존재하지 않습니다.");
        }
        return reservations;
    }

    /**
     * 예약 취소. 물리적 삭제는 이루어지지 않고 상태 변경만 이루어집니다.
     * @param reservationId
     */
    public Reservation cancelReservation(String reservationId) {
        Reservation reservation = getReservation(reservationId);
        reservation.cancel();
        return reservationRepository.save(reservation);
    }

    /**
     * 예약 확정. 결제 처리를 통해 예약을 완료합니다.
     * <br></br>
     * 해당 예약 상태를 {@code ReservationStatus.PAID} 로 변경합니다.
     * @param reservationId
     */
    public Reservation confirmReservation(String reservationId) {
        Reservation reservation = getReservation(reservationId);
        reservation.reserve();
        return reservationRepository.save(reservation);
    }

    /**
     * 예약 단 건 만료 처리. 이후 가예약 건에 대한 만료 시 활용됩니다.
     * @param reservationId
     * @return
     */
    public Reservation expireReservation(String reservationId) {
        Reservation reservation = getReservation(reservationId);
        reservation.expire();
        return reservationRepository.save(reservation);
    }

    /**
     * 만료 처리 대상인 예약 전체 조회. 이후 가예약 건에 대한 만료 시 활용됩니다.
     * <br></br>
     * 만료 대상 : 현재 상태가 {@code ReservationStatus.BOOKED} 이지만 만료 시간이 지난 예약
     * @return
     */
    public List<Reservation> getReservationsToBeExpired() {
        return reservationRepository.findAllByExpirationCriteria();
    }

    public Reservation getReservationByConcertScheduleIdAndSeatId(String concertScheduleId, String seatId) {
        return reservationRepository.findByConcertScheduleIdAndSeatId(concertScheduleId, seatId)
                .orElseThrow(() -> new UnavailableRequestException("해당 공연 일정과 좌석에 대한 예약이 존재하지 않습니다."));
    }

    public Reservation getReservationByConcertScheduleIdAndUserId(String concertScheduleId, String userId){
        return reservationRepository.findByConcertScheduleIdAndUserId(concertScheduleId, userId)
                .orElseThrow(() -> new UnavailableRequestException("해당 사용자의 예약이 존재하지 않습니다."));
    }
}
