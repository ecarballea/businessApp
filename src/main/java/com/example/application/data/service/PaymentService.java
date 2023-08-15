package com.example.application.data.service;

import com.example.application.data.model.Payment;
import com.example.application.data.model.Status_Enum;
import com.example.application.data.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void savePayment(Payment payment) {
        if (payment == null) {
            System.err.println("Payment is null. Are you sure you have connected your form to the application?");
            return;
        }
        paymentRepository.save(payment);
    }

    public void deletePayment(Payment payment) {
        paymentRepository.delete(payment);
    }

    public List<Payment> findAllPayments (String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return paymentRepository.findAll();
        } else {
            return paymentRepository.findByOwner(stringFilter);
        }
    }

    public List<Payment> findByStatus(String stringStatus) {
        if (stringStatus == null || stringStatus.isEmpty()) {
            return paymentRepository.findAll();
        } else {
            return paymentRepository.findByStatusEnum(stringStatus);
        }
    }

//    public List<Status_Enum> findAllStatusEnum() {
//        return List.of(statusEnum.values());
//    }


}
