package tn.platform.user.instructorrequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.platform.user.instructorrequest.entity.InstructorRequest;
import tn.platform.user.instructorrequest.entity.RequestStatus;
import tn.platform.user.user.entity.User;

import java.util.List;

public interface InstructorRequestRepository
        extends JpaRepository<InstructorRequest, Long> {

    // 🔥 utile pour admin
    boolean existsByUserAndStatus(User user, RequestStatus status);
}