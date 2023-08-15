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
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
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
    Tab all = new Tab("All");
    Tab pending = new Tab("Pending");
    Tab submitted = new Tab("Submitted");
    Tab confirmed = new Tab("Confirmed");
    Tab failed = new Tab("Failed");
    String selectedTab = "";
    Tabs subViews = getSecondaryNavigation();

    public PaymentListView(AccountService accountService, PaymentService paymentService) {
        this.accountService = accountService;
        this.paymentService = paymentService;
        addClassName("payment-list-view");
        setSizeFull();
        configureGrid();
        configureForm();

//        Tabs subViews = getSecondaryNavigation();

        add(getToolbar(), subViews, getContent());
        setAlignSelf(FlexComponent.Alignment.CENTER, subViews);
//        selectedTab = subViews.getSelectedTab();
        updateList();
        closeEditor();
//        setContent(subViews.getSelectedTab());
    }

    private Tabs getSecondaryNavigation() {
        Tabs tabs = new Tabs(all, pending, submitted, confirmed, failed);
        tabs.addSelectedChangeListener(
                event -> setContent(event.getSelectedTab()));
        selectedTab = tabs.getSelectedTab().getLabel();
        return tabs;
    }

    private void setContent(Tab tab) {
        if (tab.equals(pending)) {
            grid.setItems(paymentService.findByStatus("Pending"));
            selectedTab = "Pending";
        } else if (tab.equals(all)) {
            grid.setItems(paymentService.findByStatus(null));
            selectedTab = "All";
        } else if (tab.equals(submitted)) {
            grid.setItems(paymentService.findByStatus("Submitted"));
            selectedTab = "Submitted";
        } else if (tab.equals(confirmed)) {
            grid.setItems(paymentService.findByStatus("Confirmed"));
            selectedTab = "Confirmed";
        } else { // failed
            grid.setItems(paymentService.findByStatus("Failed"));
            selectedTab = "Failed";
        }
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
        selectedTab = event.getPayment().getStatusEnum();
        subViews.setSelectedTab(getSelectedTab());
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
        grid.addComponentColumn(payment -> createStatusBadge(payment.getStatusEnum()))
                .setHeader("Status");
        grid.addColumn(Payment::getStatusEnum).setHeader("Status").setSortable(true);
        grid.addColumn(payment -> payment.getAccountFrom().getOwner()).setHeader("From").setSortable(true);
        grid.addColumn(payment -> payment.getAccountTo().getOwner()).setHeader("To").setSortable(true);
        grid.addColumn(PaymentListView::formatValue)
                .setHeader("Availability ($)").setTextAlign(ColumnTextAlign.END).setSortable(true);
        grid.addColumn(new LocalDateRenderer<>(PaymentListView::getModifiedDate, "MMM dd, YYYY"))
                .setHeader("Due Date").setSortable(true);


        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event ->
                editPayment(event.getValue()));
    }

    private Span createStatusBadge(String status) {
        String theme;
        VaadinIcon icon;
        switch (status) {
            case "Pending" -> {
                theme = "badge";
                icon = VaadinIcon.CLOCK;
            }
            case "Submitted" -> {
                theme = "badge contrast";
                icon = VaadinIcon.PAPERPLANE_O;
            }
            case "Failed" -> {
                theme = "badge error";
                icon = VaadinIcon.EXCLAMATION_CIRCLE_O;
            }
            default -> { // Confirmed
                theme = "badge success";
                icon = VaadinIcon.CHECK;
            }
        }
        Span badge = new Span(createIcon(icon), new Span(status));
        badge.getElement().getThemeList().add(theme);
        return badge;
    }

    private Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        return icon;
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
        setContent(getSelectedTab());
    }
    private Tab getSelectedTab() {
        Tab currentTab;
        switch (selectedTab) {
            case "Pending" -> {
                currentTab = pending;
            }
            case "Submitted" -> {
                currentTab = submitted;
            }
            case "Failed" -> {
                currentTab = failed;
            }
            default -> { // Confirmed
                currentTab = confirmed;
            }
        }
        return currentTab;
    }
}
