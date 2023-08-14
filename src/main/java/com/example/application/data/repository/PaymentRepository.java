package com.example.application.data.repository;

import com.example.application.data.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    @Query("""
            select p from Payment p
            where lower(p.accountFrom.owner) like lower(concat('%', :searchTerm, '%')) 
            or lower(p.accountTo.owner) like lower(concat('%', :searchTerm, '%'))""")
    List<Payment> findByOwner(@Param("searchTerm") String searchTerm);
}