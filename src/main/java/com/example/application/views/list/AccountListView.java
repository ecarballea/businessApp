package com.example.application.views.list;

import com.example.application.data.model.Account;
import com.example.application.data.service.AccountService;
import com.example.application.views.MainLayout;
import com.example.application.views.form.AccountFormView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@PermitAll
@Route(value = "account", layout = MainLayout.class)
@PageTitle("Accounts | Abax")
public class AccountListView extends VerticalLayout {

    private final AccountService accountService;

    Grid<Account> grid = new Grid<>(Account.class, false);
    TextField filterText = new TextField();
    AccountFormView form;

    public AccountListView(AccountService accountService) {
        this.accountService = accountService;
        addClassName("account-list-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new AccountFormView();
        form.setWidth("25em");
        form.addSaveListener(this::saveAccount); // <1>
        form.addDeleteListener(this::deleteAccount); // <2>
        form.addCloseListener(e -> closeEditor()); // <3>
    }

    private void saveAccount(AccountFormView.SaveEvent event) {
        accountService.saveAccount(event.getAccount());
        updateList();
        closeEditor();
    }

    private void deleteAccount(AccountFormView.DeleteEvent event) {
        accountService.deleteAccount(event.getAccount());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("account-grid");
        grid.setSizeFull();
        grid.setColumns("id", "owner", "bankAccount");
        grid.addColumn(AccountListView::formatValue)
                .setHeader("Availability ($)").setTextAlign(ColumnTextAlign.END).setSortable(true);
//        grid.addColumn(new LocalDateRenderer<>(AccountListView::getModifiedDate, "MM/dd/yyyy"))
//                .setHeader("Updated").setSortable(true);


        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event ->
                editAccount(event.getValue()));
    }

    private static LocalDate getModifiedDate(Account account) {
//        return account.getUpdated();
        return account.getUpdated().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private static final NumberFormat currencyFormatter = NumberFormat
            .getCurrencyInstance(new Locale("en", "US"));

    private static String formatValue(Account account) {
        double value = account.getAvailability();

        return currencyFormatter.format(value);
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by Owner Name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addAccountButton = new Button("Add account");
        addAccountButton.addClickListener(click -> addAccount());

        var toolbar = new HorizontalLayout(filterText, addAccountButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    public void editAccount (Account account) {
        if (account == null) {
            closeEditor();
        } else {
            form.setAccount(account);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setAccount(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addAccount() {
        grid.asSingleSelect().clear();
        editAccount(new Account());
    }


    private void updateList() {
        grid.setItems(accountService.findAllAccounts(
                null
//                filterText.getValue()
        ));
    }
}
