package com.alseinn.semiwood.dao.user;

import com.alseinn.semiwood.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByUsernameOrEmailOrMobileNumber(String username, String email, String mobileNumber);
    List<User> findAllByIdNotAndMobileNumberContainingIgnoreCase(Long id, String mobileNumber);
}

