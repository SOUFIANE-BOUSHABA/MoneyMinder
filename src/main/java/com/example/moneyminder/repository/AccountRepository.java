package com.example.moneyminder.repository;

import com.example.moneyminder.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
   List<Account> findAllByUserId(Long userId);
}
