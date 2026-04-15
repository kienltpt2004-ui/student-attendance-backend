package com.example.cdtn.service;

import com.example.cdtn.dto.request.SessionRequest;
import com.example.cdtn.dto.response.SessionResponse;
import com.example.cdtn.entity.Classes;
import com.example.cdtn.entity.Session;
import com.example.cdtn.entity.enums.SessionStatus;
import com.example.cdtn.exception.BadRequestException;
import com.example.cdtn.exception.ResourceNotFoundException;
import com.example.cdtn.repository.ClassesRepository;
import com.example.cdtn.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final ClassesRepository classesRepository;

    public SessionService(SessionRepository sessionRepository, ClassesRepository classesRepository){
        this.sessionRepository = sessionRepository;
        this.classesRepository = classesRepository;
    }

    public SessionResponse createSession(SessionRequest request){
            Classes classes = classesRepository.findById(request.getClassId())
                    .orElseThrow(()->new ResourceNotFoundException("không tìm thấy lớp học"));

            if(request.getEndTime().isBefore(request.getStartTime())){
                throw new BadRequestException("EndTime phải sau StartTime");
            }

            Session session = new Session();
            session.setClasses(classes);
            session.setTitle(request.getTitle());
            session.setStartTime(request.getStartTime());
            session.setEndTime(request.getEndTime());
            session.setLatitude(request.getLatitude());
            session.setLongitude(request.getLongitude());
            session.setRadius(request.getRadius());

            sessionRepository.save(session);

            return mapToResponse(session);
    }

    public List<SessionResponse> getAllSessionByClass(Long classId) {
        return sessionRepository.findByClasses_Id(classId)
                .stream()
                .map(this::mapToResponse)
                .toList();

    }

    public SessionResponse getSessionById(Long id){
        Session session = sessionRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Không tìm thấy session"));

        return mapToResponse(session);
    }

    public void closeSession(Long id){
        Session session = sessionRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Không tìm thấy session"));

        session.setStatus(SessionStatus.ClOSED);
        sessionRepository.save(session);
    }

    private SessionResponse mapToResponse(Session session) {

        return SessionResponse.builder()
                .id(session.getId())
                .classId(session.getClasses().getId())
                .title(session.getTitle())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .latitude(session.getLatitude())
                .longitude(session.getLongitude())
                .radius(session.getRadius())
                .status(session.getStatus().name())
                .build();
    }
}
