package com.example.nagoyameshi.repository;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ReservationRepository
        extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByUser(User user);

    List<Reservation> findByUserAndStatusNot(User user, String status);
}