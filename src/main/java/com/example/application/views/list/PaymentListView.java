package com.example.application.views.list;

import com.example.application.data.model.Payment;
import com.example.application.data.service.AccountService;
import com.example.application.data.service.PaymentService;
import com.example.application.views.MainLayout;
import com.example.application.views.form.PaymentFormView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

@PermitAll
@Route(value = "payment", layout = MainLayout.class)
@PageTitle("Payments | Abax")
public class PaymentListView extends VerticalLayout {

    private final AccountService accountService;
    private final PaymentService paymentService;

    Grid<Payment> grid = new Grid<>(Payment.class, false);
    PaymentFormView form;

    public PaymentListView(AccountService accountService, PaymentService paymentService) {
        this.accountService = accountService;
        this.paymentService = paymentService;
        addClassName("payment-list-view");
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
        form = new PaymentFormView(accountService.findAllAccounts(null), accountService.findAllAccounts(null));
        form.setWidth("25em");
        form.addSaveListener(this::savePayment); // <1>
        form.addDeleteListener(this::deletePayment); // <2>
        form.addCloseListener(e -> closeEditor()); // <3>
    }

    private void savePayment(PaymentFormView.SaveEvent event) {
        paymentService.savePayment(event.getPayment());
        updateList();
        closeEditor();
    }

    private void deletePayment(PaymentFormView.DeleteEvent event) {
        paymentService.deletePayment(event.getPayment());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("payment-grid");
        grid.setSizeFull();
        grid.addColumn(Payment::getStatusEnum).setHeader("Status").setSortable(true);
        grid.addColumn(payment -> payment.getAccountFrom().getOwner()).setHeader("From").setSortable(true);
        grid.addColumn(payment -> payment.getAccountTo().getOwner()).setHeader("To").setSortable(true);
        grid.addColumn(PaymentListView::formatValue)
                .setHeader("Availability ($)").setTextAlign(ColumnTextAlign.END).setSortable(true);
        grid.addColumn(new LocalDateRenderer<>(PaymentListView::getModifiedDate, "MMM dd, yyyy"))
                .setHeader("Due Date").setSortable(true);


        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event ->
                editPayment(event.getValue()));
    }

    private static LocalDate getModifiedDate(Payment payment) {
        return payment.getDueDate();
//        return payment.getDueDate().toInstant().atZone(ZoneId.systemDefault())
//                .toLocalDate();
    }

    private static final NumberFormat currencyFormatter = NumberFormat
            .getCurrencyInstance(new Locale("en", "US"));

    private static String formatValue(Payment payment) {
        double value = payment.getAmount();

        return currencyFormatter.format(value);
    }

    private Component getToolbar() {
        Button addPaymentButton = new Button("Add payment");
        addPaymentButton.addClickListener(click -> addPayment());

        var toolbar = new HorizontalLayout(addPaymentButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    public void editPayment(Payment payment) {
        if (payment == null) {
            closeEditor();
        } else {
            form.setPayment(payment);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setPayment(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addPayment() {
        grid.asSingleSelect().clear();
        editPayment(new Payment());
    }


    private void updateList() {

        grid.setItems(paymentService.findAllPayments(
                ""
//                filterText.getValue()
        ));
    }
}
