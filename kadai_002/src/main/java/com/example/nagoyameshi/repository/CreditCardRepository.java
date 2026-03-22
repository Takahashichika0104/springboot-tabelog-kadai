package com.example.nagoyameshi.repository;

import com.example.nagoyameshi.entity.CreditCard;
import com.example.nagoyameshi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {
    Optional<CreditCard> findByUser(User user);
}
