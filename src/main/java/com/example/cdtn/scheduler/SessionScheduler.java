package com.example.cdtn.scheduler;

import com.example.cdtn.entity.Session;
import com.example.cdtn.entity.enums.SessionStatus;
import com.example.cdtn.repository.SessionRepository;
import com.example.cdtn.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SessionScheduler {

    private final SessionRepository sessionRepo;
    private final AttendanceService attendanceService;

    // chạy mỗi 1 phút
    @Scheduled(fixedRate = 60000)
    public void autoCloseSession() {

        //System.out.println("Scheduler running...");

        List<Session> sessions =
                sessionRepo.findByStatusAndEndTimeBefore(
                        SessionStatus.OPEN,
                        LocalDateTime.now()
                );

        for (Session session : sessions) {

            System.out.println("Đóng session: " + session.getId());

            session.setStatus(SessionStatus.ClOSED);

            attendanceService.generateAbsent(session);

            sessionRepo.save(session);
        }
    }
}