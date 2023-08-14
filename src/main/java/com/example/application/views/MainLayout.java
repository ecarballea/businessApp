package com.example.application.views;

import com.example.application.data.model.Payment;
import com.example.application.security.SecurityService;
import com.example.application.views.list.AccountListView;
import com.example.application.views.list.ListView;
import com.example.application.views.list.PaymentListView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {
    private final SecurityService securityService;
    private String header;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        setHeader("");
        createHeader();
//        createDrawer();

//        Tabs tabs = getTabs();
//        addToDrawer(tabs);
        createMenu();
        setPrimarySection(Section.DRAWER);
    }

    private void createHeader() {
        H1 logo = new H1(this.header);
        logo.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.LARGE,
            LumoUtility.TextColor.PRIMARY_CONTRAST);

        String username = securityService.getAuthenticatedUser().getUsername();
        MenuBar menuBar = AvatarMenuBar(username);

        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames(
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.FontSize.LARGE);

        var header = new HorizontalLayout(toggle, logo, menuBar);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(false);
        header.expand(logo); // <4>
        header.setWidthFull();
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM,
            LumoUtility.Background.CONTRAST_90);

        addToNavbar(header); 

    }

    private MenuBar AvatarMenuBar(String username) {

        Avatar avatar = new Avatar(username);
        avatar.addClassNames(
                LumoUtility.BorderColor.PRIMARY,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.FontSize.LARGE);
        avatar.setTooltipEnabled(true);

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        MenuItem menuItem = menuBar.addItem(avatar);
        SubMenu subMenu = menuItem.getSubMenu();
        subMenu.addItem("Log out", e -> securityService.logout());

        return menuBar;
    }

    private void createMenu(){
        Avatar companyLogo = new Avatar("Abax");
//        companyLogo.setImage("data:image/png;base64,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");
        companyLogo.addThemeVariants(AvatarVariant.LUMO_XLARGE);
        H2 companyName = new H2("Abax");
        var logoAndTitle = new HorizontalLayout(companyLogo, companyName);
        logoAndTitle.setAlignItems(FlexComponent.Alignment.CENTER);

        Tabs tabs = getTabs();

        var menu = new VerticalLayout();
        menu.add(logoAndTitle);
        menu.setAlignSelf(FlexComponent.Alignment.CENTER, logoAndTitle);
        menu.setAlignItems(FlexComponent.Alignment.START);
        menu.add(new Hr(),tabs);
        menu.setHeightFull();
        menu.addClassNames(
                LumoUtility.Background.CONTRAST_5);

        addToDrawer(menu);
    }
    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        tabs.add(
                createTab(VaadinIcon.LIST, "Contacts", ListView.class),
                createTab(VaadinIcon.INSTITUTION, "Accounts", AccountListView.class),
                createTab(VaadinIcon.CREDIT_CARD, "Payments", PaymentListView.class),
                createTab(VaadinIcon.DASHBOARD, "Dashboard", DashboardView.class)
        );
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }

    private Tab createTab(VaadinIcon viewIcon, String viewName, Class component) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("margin-inline-start", "var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs)");

        RouterLink link = new RouterLink();
        link.add(icon, new Span(viewName));
        link.setRoute(component);
        link.setTabIndex(-1);

        return new Tab(link);
    }

    public void setHeader(String header) {
        this.header = header;
    }
}