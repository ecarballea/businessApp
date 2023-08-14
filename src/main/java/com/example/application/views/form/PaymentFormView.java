package com.example.application.views.form;

import com.example.application.data.model.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

public class PaymentFormView extends FormLayout {
    ComboBox<String> statusEnum = new ComboBox<>("Status");
    ComboBox<Account> accountFrom = new ComboBox<>("Sender");
    ComboBox<Account> accountTo = new ComboBox<>("Receiver");
    TextField amount = new TextField("Amount");
    DatePicker dueDate = new DatePicker("Due Date");

//    TextField date = new TextField("Date");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    // Other fields omitted
    Binder<Payment> binder = new BeanValidationBinder<>(Payment.class);

    public PaymentFormView(List<Account> senderList, List<Account> receiverList) {
        addClassName("payment-form");
        binder.bindInstanceFields(this);

        statusEnum.setItems("Pending", "Submitted", "Confirmed", "Failed");
        accountFrom.setItems(senderList);
        accountFrom.setItemLabelGenerator(Account::getOwner);
        accountTo.setItems(receiverList);
        accountTo.setItemLabelGenerator(Account::getOwner);
        Locale usLocale = new Locale("en", "US");
        dueDate.setLocale(usLocale);
        dueDate.setValue(LocalDate.now(ZoneId.systemDefault()));
//        dueDate.setHelperText(
//                "Date picker configured to use Finnish date format");

        add(statusEnum,
                accountFrom,
                accountTo,
                amount,
                dueDate,
                createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave()); // <1>
        delete.addClickListener(event -> fireEvent(new PaymentFormView.DeleteEvent(this, binder.getBean()))); // <2>
        close.addClickListener(event -> fireEvent(new PaymentFormView.CloseEvent(this))); // <3>
//
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid())); // <4>
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if(binder.isValid()) {
            fireEvent(new PaymentFormView.SaveEvent(this, binder.getBean())); // <6>
        }
    }


    public void setPayment(Payment payment) {
        binder.setBean(payment); // <1>
    }

    // Events
    @Getter
    public static abstract class PaymentFormEvent extends ComponentEvent<PaymentFormView> {
        private Payment payment;

        protected PaymentFormEvent(PaymentFormView source, Payment payment) {
            super(source, false);
            this.payment = payment;
        }

    }

    public static class SaveEvent extends PaymentFormView.PaymentFormEvent {
        SaveEvent(PaymentFormView source, Payment payment) {
            super(source, payment);
        }
    }

    public static class DeleteEvent extends PaymentFormView.PaymentFormEvent {
        DeleteEvent(PaymentFormView source, Payment payment) {
            super(source, payment);
        }

    }

    public static class CloseEvent extends PaymentFormView.PaymentFormEvent {
        CloseEvent(PaymentFormView source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<PaymentFormView.DeleteEvent> listener) {
        return addListener(PaymentFormView.DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<PaymentFormView.SaveEvent> listener) {
        return addListener(PaymentFormView.SaveEvent.class, listener);
    }
    public Registration addCloseListener(ComponentEventListener<PaymentFormView.CloseEvent> listener) {
        return addListener(PaymentFormView.CloseEvent.class, listener);
    }


}
