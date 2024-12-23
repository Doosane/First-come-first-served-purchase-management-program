package com.doosan.msa.order.util;

import com.doosan.msa.order.entity.BookStatus;
import com.doosan.msa.order.entity.ExamBook;
import com.doosan.msa.order.repository.ExamBookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

// 배송 상태 관리: 예약/스케줄링 로직 추가
@Component
@RequiredArgsConstructor
@Slf4j
public class ShippingScheduler {

    private final ExamBookRepository examBookRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    public void updateShippingStatus() {
        LocalDate today = LocalDate.now();
        log.info("배송 상태 업데이트 작업 시작: 오늘 날짜 {}", today);

        // 배송 중으로 변경 (D+1)
        List<ExamBook> toShip = examBookRepository.findAllByStatusAndBookingDate(BookStatus.BOOKED, today.minusDays(1));
        log.info("배송 중으로 변경 대상 조회 완료: 총 {}건", toShip.size());

        toShip.forEach(book -> {
            log.debug("배송 중 변경 준비: ExamBook ID={}, 현재 상태={}, 예약일={}",
                    book.getId(), book.getStatus(), book.getBookingDate());
            book.setStatus(BookStatus.DELIVERED);
            log.info("배송 중으로 상태 변경 완료: ExamBook ID={}, 새로운 상태={}",
                    book.getId(), book.getStatus());
        });
        examBookRepository.saveAll(toShip);
        log.info("배송 중으로 상태 변경된 ExamBook 저장 완료: 총 {}건", toShip.size());

        // 배송 완료로 변경 (D+2)
        List<ExamBook> toComplete = examBookRepository.findAllByStatusAndBookingDate(BookStatus.DELIVERED, today.minusDays(1));
        log.info("배송 완료로 변경 대상 조회 완료: 총 {}건", toComplete.size());

        toComplete.forEach(book -> {
            log.debug("배송 완료 변경 준비: ExamBook ID={}, 현재 상태={}, 예약일={}",
                    book.getId(), book.getStatus(), book.getBookingDate());

            book.setStatus(BookStatus.DELIVERED);
            log.info("배송 완료로 상태 변경: ExamBook ID {}", book.getId());
        });
        examBookRepository.saveAll(toComplete);
        log.info("배송 완료로 상태 변경된 ExamBook 저장 완료: 총 {}건", toComplete.size());

        log.info("배송 상태 업데이트 작업 종료: 날짜 {}", today);
    }
}