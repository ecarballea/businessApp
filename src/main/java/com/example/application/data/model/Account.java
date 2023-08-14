package com.example.application.data.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "account")
public class Account extends AbstractEntity{

//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgenerator")
//    @Column(name = "id", nullable = false)
//    private Long id;

    @NotEmpty
    private String owner = "";

    @NotEmpty
    private String bankAccount = "";

    @NotNull
    private Double availability = 0.0;

    @LastModifiedDate
    private Date updated;

    @OneToMany(mappedBy = "accountTo", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "toPaymentsTo")
    private List<Payment> paymentsTo = new ArrayList<>();


    @OneToMany(mappedBy = "accountFrom", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "fromPaymentsFrom")
    private List<Payment> paymentsFrom = new ArrayList<>();

}