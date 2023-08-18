package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.whosfritz.breakdecider.Security.SecurityService;

import java.util.Optional;

public class MainView extends AppLayout {
    private final SecurityService securityService;
    private final Tabs menu;

    public MainView(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
        setPrimarySection(Section.DRAWER);
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));

    }

    /**
     * Creates a tab with a router link.
     *
     * @param text             the text to display in the tab
     * @param icon             the icon to display in the tab
     * @param navigationTarget the navigation target for the router link inside the tab
     * @return a new tab
     */
    private static Tab createTab(String text, VaadinIcon icon, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();

        Div linkContent = new Div();
        linkContent.addClassName("link-content");

        Icon linkIcon = new Icon(icon);
        linkIcon.setSize("1.5em");
        linkContent.add(linkIcon);

        RouterLink routerLink = new RouterLink();
        routerLink.add(linkContent);
        routerLink.add(text);
        routerLink.setRoute(navigationTarget);

        tab.add(routerLink);
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    /**
     * Creates the content of the drawer - a list of menu items.
     *
     * @param menu the menu
     * @return the content of the drawer
     */
    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        H2 topMenu = new H2("Menü");
        topMenu.addClassNames("menu-titel",
                LumoUtility.Margin.Bottom.MEDIUM,
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.Top.LARGE
        );
        layout.add(topMenu);

        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        layout.add(logoLayout, menu);
        return layout;
    }


    /**
     * Creates the tabs which are the menu items of the application.
     *
     * @return the tabs
     */
    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(createMenuItems());
        return tabs;
    }

    private Tab[] createMenuItems() {
        return new Tab[]{
                createTab("Jetzt Abstimmen", VaadinIcon.CHECK, AbstimmungenView.class),
                createTab("Abstimmung erstellen", VaadinIcon.PLUS, CreateAbstimmungView.class),
                createTab("Dein Profil", VaadinIcon.USER, UserProfileView.class),
                createTab("Admin Panel", VaadinIcon.USER_STAR, AdminPanelView.class)
        };
    }

    private void createHeader() {
        H2 logo = new H2("BreakDecider");
        logo.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.MEDIUM);
        Button logoutButton = new Button("Logout", event -> {
            securityService.logout();
        });
        logoutButton.addClassName("logout-button");
        logoutButton.setTooltipText("Hier kannst du dich ausloggen.");

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                logo,
                new Text("Eingeloggt als: "),
                new Text(securityService.getAuthenticatedUser().getUsername()),
                logoutButton);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        /* Select the tab corresponding to the currently shown view */
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);

    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren()
                .filter(tab ->
                        {
                            tab.addClassNames(
                                    LumoUtility.Padding.Vertical.MEDIUM,
                                    LumoUtility.FontSize.MEDIUM
                            );
                            return ComponentUtil.getData(tab, Class.class)
                                    .equals(component.getClass());
                        }
                )
                .findFirst().map(Tab.class::cast);
    }


}