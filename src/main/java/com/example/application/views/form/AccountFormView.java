package com.example.application.views.form;

import com.example.application.data.model.Account;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
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

public class AccountFormView extends FormLayout {
//    TextField firstName = new TextField("First name");
    TextField owner = new TextField("Owner");
    TextField bankAccount = new TextField("Bank Account");
    TextField availability = new TextField("Availability");
//    TextField updated = new TextField("Updated");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    // Other fields omitted
    Binder<Account> binder = new BeanValidationBinder<>(Account.class);

    public AccountFormView() {
        addClassName("account-form");
        binder.bindInstanceFields(this);

        add(owner,
                bankAccount,
                availability,
//                updated,
                createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave()); // <1>
        delete.addClickListener(event -> fireEvent(new AccountFormView.DeleteEvent(this, binder.getBean()))); // <2>
        close.addClickListener(event -> fireEvent(new AccountFormView.CloseEvent(this))); // <3>
//
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid())); // <4>
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if(binder.isValid()) {
            fireEvent(new AccountFormView.SaveEvent(this, binder.getBean())); // <6>
        }
    }


    public void setAccount(Account account) {
        binder.setBean(account); // <1>
    }

    // Events
    @Getter
    public static abstract class AccountFormEvent extends ComponentEvent<AccountFormView> {
        private Account account;

        protected AccountFormEvent(AccountFormView source, Account account) {
            super(source, false);
            this.account = account;
        }

    }

    public static class SaveEvent extends AccountFormView.AccountFormEvent {
        SaveEvent(AccountFormView source, Account account) {
            super(source, account);
        }
    }

    public static class DeleteEvent extends AccountFormView.AccountFormEvent {
        DeleteEvent(AccountFormView source, Account account) {
            super(source, account);
        }

    }

    public static class CloseEvent extends AccountFormView.AccountFormEvent {
        CloseEvent(AccountFormView source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<AccountFormView.DeleteEvent> listener) {
        return addListener(AccountFormView.DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<AccountFormView.SaveEvent> listener) {
        return addListener(AccountFormView.SaveEvent.class, listener);
    }
    public Registration addCloseListener(ComponentEventListener<AccountFormView.CloseEvent> listener) {
        return addListener(AccountFormView.CloseEvent.class, listener);
    }


}
