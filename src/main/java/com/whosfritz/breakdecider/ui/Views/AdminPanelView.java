package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.whosfritz.breakdecider.Data.Entities.AppUserRole;
import com.whosfritz.breakdecider.Data.Entities.BreakDeciderUser;
import com.whosfritz.breakdecider.Data.Services.BreakDeciderUserService;
import com.whosfritz.breakdecider.Registration.RegistrationRequest;
import com.whosfritz.breakdecider.Registration.RegistrationService;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import static com.whosfritz.breakdecider.ui.utils.showNotification;

@RolesAllowed("ROLE_ADMIN")
@PageTitle("Admin Panel")
@Route(value = "adminPanel", layout = MainView.class)
public class AdminPanelView extends VerticalLayout {
    private final BreakDeciderUserService breakDeciderUserService;
    private final RegistrationService registrationService;
    private final TextField usernameTF = new TextField();
    private final PasswordField passwordPF = new PasswordField();
    private final ComboBox<AppUserRole> appUserRoleCB = new ComboBox<>();
    private final Grid<BreakDeciderUser> grid = new Grid<>(BreakDeciderUser.class);

    private final Logger logger = LoggerFactory.getLogger(AdminPanelView.class);


    public AdminPanelView(BreakDeciderUserService breakDeciderUserService, RegistrationService registrationService) {
        this.breakDeciderUserService = breakDeciderUserService;
        this.registrationService = registrationService;
        // formlayout with two input fields to create a user with username and password
        // grid to show all users
        Paragraph paragraph = new Paragraph("Benutzer registrieren");
        FormLayout formLayout = new FormLayout();

        appUserRoleCB.setItems(AppUserRole.values());
        formLayout.addFormItem(usernameTF, "Username");
        formLayout.addFormItem(passwordPF, "Password");
        formLayout.addFormItem(appUserRoleCB, "Role");
        Button createUserButton = new Button("Benutzer registrieren");
        formLayout.addFormItem(createUserButton, "Benutzer registrieren");
        createUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createUserButton.addClickListener(event -> createUser());

        grid.setColumns("username", "appUserRole", "locked", "enabled");
        grid.setItems(breakDeciderUserService.getAllUsers());
        grid.getColumnByKey("username").setHeader("Beschreibung").setSortable(true);
        grid.getColumnByKey("appUserRole").setHeader("Rolle").setSortable(true);
        grid.getColumnByKey("locked").setHeader("Zugang gesperrt").setSortable(true);
        grid.getColumnByKey("enabled").setHeader("Freigeschaltet").setSortable(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        Button deleteUsersButton = new Button("Benutzer löschen");
        deleteUsersButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        deleteUsersButton.addClickListener(event -> {
            grid.getSelectedItems().forEach(this::handleDeletion);
            grid.setItems(breakDeciderUserService.getAllUsers());
        });
        add(paragraph, formLayout, grid, deleteUsersButton);
    }

    private void createUser() {
        String username = usernameTF.getValue();
        String password = passwordPF.getValue();
        AppUserRole appUserRole = appUserRoleCB.getValue();

        if (username != null && password != null && appUserRole != null) {
            try {
                registrationService.register(new RegistrationRequest(username, password, appUserRole));
                usernameTF.clear();
                passwordPF.clear();
                appUserRoleCB.clear();
                grid.setItems(breakDeciderUserService.getAllUsers());
                showNotification(Notification.Position.BOTTOM_END, "Benutzer erstellt", NotificationVariant.LUMO_SUCCESS);
                logger.info("User: " + username + " created");
            } catch (IllegalStateException e) {
                showNotification(Notification.Position.BOTTOM_END, "Dieser Benutzername existiert schon", NotificationVariant.LUMO_ERROR);
                logger.error("User already exists");
            } catch (Exception e) {
                showNotification(Notification.Position.BOTTOM_END, "Irgendwas lief schief", NotificationVariant.LUMO_ERROR);
                logger.error("Error while creating user: " + e.getMessage());
            }
        } else {
            showNotification(Notification.Position.BOTTOM_END, "Bitte alle Felder ausfüllen", NotificationVariant.LUMO_ERROR);
        }
    }

    private void handleDeletion(BreakDeciderUser breakDeciderUser) {
        try {
            breakDeciderUserService.deleteUserWithStimmzettel(breakDeciderUser.getId());
            showNotification(Notification.Position.BOTTOM_END, "Ausgewählte Benutzer gelöscht", NotificationVariant.LUMO_SUCCESS);
            logger.info("Benutzer: " + breakDeciderUser.getUsername() + " gelöscht.");
        } catch (DataIntegrityViolationException e) {
            showNotification(Notification.Position.BOTTOM_END, "User konnte SQL mäßig nicht gelöscht werden", NotificationVariant.LUMO_ERROR);
            logger.error("Benutzer konnte nicht gelöscht werden: " + e.getMessage());
        } catch (Exception e) {
            showNotification(Notification.Position.BOTTOM_END, "Irgendwas lief schief", NotificationVariant.LUMO_ERROR);
            logger.error("Benutzer konnte nicht gelöscht werden: " + e.getMessage());
        }
    }

}
