package com.v01.techgear_server.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserName(String userName);

    Optional<User> findByUserName(String userName);

    // update password
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.userName = :userName")
    void updatePassword(@Param("userName") String userName, @Param("password") String password);

    void deleteByUserName(String username);
    // TODO: update more handling interface for Email, phoneNo, roles and address.

}
