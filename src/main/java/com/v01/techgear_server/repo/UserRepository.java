package com.v01.techgear_server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByOrderByUsernameAsc();
    boolean existsByUsername(String username);

    @Query("SELECT u from User u WHERE u.username = :username")
    Optional<User> findByUsername(String username);

    @Query("SELECT u from User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);


    @Modifying
    @Query("SELECT u FROM User u WHERE u.password = :password")
    Optional<User> findByPassword(String password);

    // update password
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.username = :username")
    void updatePassword(@Param("username") String username, @Param("password") String password);

    void deleteByUsername(String username);
    // TODO: update more handling interface for Email, phoneNo, roles and address.

}
