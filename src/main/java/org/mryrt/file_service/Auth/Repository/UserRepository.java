package org.mryrt.file_service.Auth.Repository;

import org.mryrt.file_service.Auth.Model.User;
import org.mryrt.file_service.Utility.Annotation.TrackExecutionTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@TrackExecutionTime
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

}
