package com.example.storage.repository;

import com.example.storage.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserData, Integer> {
    Optional<UserData> findByLogin(String login);

}
