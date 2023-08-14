package com.example.application.data.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    private Double amount = 0.0;

    @NotNull
    private LocalDate dueDate = LocalDate.now();

    @NotNull
    private String statusEnum = "";
//    @Enumerated(EnumType.STRING)
//    private Status_Enum statusEnum;

    @NotNull
    @ManyToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "accountFrom_id")
    @JsonBackReference(value = "fromPaymentsFrom")
//    @JsonIgnore
    private Account accountFrom;

    @NotNull
    @ManyToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "accountTo_id")
    @JsonBackReference(value = "toPaymentsTo")
//    @JsonIgnore
    private Account accountTo;

}