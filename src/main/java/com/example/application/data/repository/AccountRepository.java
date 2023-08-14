package com.example.application.data.repository;

import com.example.application.data.model.Account;
import com.example.application.data.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("select a from Account a " +
            "where lower(a.owner) like lower(concat('%', :searchTerm, '%')) ")
    List<Account> findByOwner(String searchTerm);
}