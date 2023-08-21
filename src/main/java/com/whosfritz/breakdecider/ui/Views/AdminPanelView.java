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
import com.whosfritz.breakdecider.Data.Entities.Abstimmungsthema;
import com.whosfritz.breakdecider.Data.Entities.AppUserRole;
import com.whosfritz.breakdecider.Data.Entities.BreakDeciderUser;
import com.whosfritz.breakdecider.Data.Entities.Status;
import com.whosfritz.breakdecider.Data.Services.AbstimmungsthemaService;
import com.whosfritz.breakdecider.Data.Services.BreakDeciderUserService;
import com.whosfritz.breakdecider.Registration.RegistrationRequest;
import com.whosfritz.breakdecider.Registration.RegistrationService;
import com.whosfritz.breakdecider.Security.SecurityService;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static com.whosfritz.breakdecider.Registration.SecretRegistrationToken.REGISTRATION_TOKEN;
import static com.whosfritz.breakdecider.ui.utils.showNotification;

@RolesAllowed("ROLE_ADMIN")
@PageTitle("Admin Panel")
@Route(value = "adminPanel", layout = MainView.class)
public class AdminPanelView extends VerticalLayout {
    private final BreakDeciderUserService breakDeciderUserService;
    private final RegistrationService registrationService;
    private final AbstimmungsthemaService abstimmungsthemaService;
    private final SecurityService securityService;
    private final TextField usernameTF = new TextField();
    private final PasswordField passwordPF = new PasswordField();
    private final ComboBox<AppUserRole> appUserRoleCB = new ComboBox<>();
    private final Grid<BreakDeciderUser> grid = new Grid<>(BreakDeciderUser.class);
    private final Grid<Abstimmungsthema> list = new Grid<>(Abstimmungsthema.class);

    private final Logger logger = LoggerFactory.getLogger(AdminPanelView.class);


    public AdminPanelView(BreakDeciderUserService breakDeciderUserService, RegistrationService registrationService, AbstimmungsthemaService abstimmungsthemaService, SecurityService securityService) {
        this.breakDeciderUserService = breakDeciderUserService;
        this.registrationService = registrationService;
        this.abstimmungsthemaService = abstimmungsthemaService;
        this.securityService = securityService;
        // formlayout with two input fields to create a user with username and password
        // grid to show all users
        Paragraph paragraph = new Paragraph("Benutzer registrieren");
        FormLayout formLayout = new FormLayout();

        appUserRoleCB.setItems(AppUserRole.values());
        formLayout.addFormItem(usernameTF, "Benutzername");
        formLayout.addFormItem(passwordPF, "Passwort");
        formLayout.addFormItem(appUserRoleCB, "Rolle");
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
        grid.addItemClickListener(event -> {
            if (grid.getSelectedItems().contains(event.getItem())) {
                grid.deselect(event.getItem());
            } else {
                grid.select(event.getItem());
            }
        });


        Button deleteUsersButton = new Button("Benutzer löschen");
        deleteUsersButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        deleteUsersButton.addClickListener(event -> {
            grid.getSelectedItems().forEach(this::handleDeletion);
            grid.setItems(breakDeciderUserService.getAllUsers());
        });
        add(paragraph, formLayout, grid, deleteUsersButton);


        List<Abstimmungsthema> abstimmungsthemenList = abstimmungsthemaService.getAllAbstimmungsthemen(); // Get the list of Abstimmungsthemen from the service
        list.setColumns("titel", "beschreibung", "ersteller", "erstelldatum");

        // Set the column headers
        list.getColumnByKey("ersteller").setHeader("Ersteller").setSortable(true);
        list.getColumnByKey("erstelldatum").setHeader("Erstelldatum").setSortable(true);
        list.getColumnByKey("titel").setHeader("Titel").setSortable(true);
        list.getColumnByKey("beschreibung").setHeader("Beschreibung").setSortable(true);
        // Add a custom column for the "Yes" button
        // show 2 buttons in one column (yes and no)


        list.addComponentColumn(abstimmungsthema -> {
            ComboBox<Status> statusComboBox = new ComboBox<>();
            statusComboBox.setItems(Status.OPEN, Status.CLOSED);
            statusComboBox.setValue(abstimmungsthema.getStatus());
            statusComboBox.addValueChangeListener(event -> {
                Status changedStatus = event.getValue();
                if (changedStatus != abstimmungsthema.getStatus()) {
                    if (changedStatus == Status.CLOSED) {
                        abstimmungsthemaService.closeAbstimmungsthema(abstimmungsthema);
                        showNotification(Notification.Position.BOTTOM_END, "Abstimmung geschlossen", NotificationVariant.LUMO_SUCCESS);
                        logger.info("Abstimmung mit Titel: " + abstimmungsthema.getTitel() + " geschlossen von: " + this.securityService.getAuthenticatedUser().getUsername());
                        list.getDataProvider().refreshItem(abstimmungsthema);
                    } else {
                        abstimmungsthemaService.openAbstimmungsthema(abstimmungsthema);
                        showNotification(Notification.Position.BOTTOM_END, "Wieder eröffnet Abstimmung geöffnet", NotificationVariant.LUMO_SUCCESS);
                        logger.info("Abstimmung mit Titel: " + abstimmungsthema.getTitel() + " wurde geöffnet von: " + this.securityService.getAuthenticatedUser().getUsername());
                        list.getDataProvider().refreshItem(abstimmungsthema);
                    }
                }

            });

            return statusComboBox;
        }).setHeader("Status");


        list.getColumns().forEach(abstimmungsthemaColumn -> abstimmungsthemaColumn.setAutoWidth(true));


        // Set the items (data) for the grid
        list.setItems(abstimmungsthemenList);


        add(list);
    }

    private void createUser() {
        String username = usernameTF.getValue();
        String password = passwordPF.getValue();
        AppUserRole appUserRole = appUserRoleCB.getValue();

        if (username != null && password != null && appUserRole != null) {
            try {
                registrationService.register(new RegistrationRequest(username, password, appUserRole, REGISTRATION_TOKEN));
                usernameTF.clear();
                passwordPF.clear();
                appUserRoleCB.clear();
                grid.setItems(breakDeciderUserService.getAllUsers());
                showNotification(Notification.Position.BOTTOM_END, "Benutzer erstellt", NotificationVariant.LUMO_SUCCESS);
                logger.info("Benutzer: " + username + " erstellt.");
            } catch (IllegalStateException e) {
                showNotification(Notification.Position.BOTTOM_END, "Dieser Benutzername existiert schon", NotificationVariant.LUMO_ERROR);
                logger.error("Benutzer: " + username + " existiert schon.");
            } catch (Exception e) {
                showNotification(Notification.Position.BOTTOM_END, "Irgendwas lief schief", NotificationVariant.LUMO_ERROR);
                logger.error("Fehler beim Erstellen von Benutzer: " + e.getMessage());
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
        } catch (NullPointerException exception) {
            showNotification(Notification.Position.BOTTOM_END, "Benutzer konnte nicht gelöscht werden", NotificationVariant.LUMO_ERROR);
            logger.error("Benutzer konnte nicht gelöscht werden: " + exception);
        } catch (DataIntegrityViolationException e) {
            showNotification(Notification.Position.BOTTOM_END, "Benutzer konnte SQL mäßig nicht gelöscht werden", NotificationVariant.LUMO_ERROR);
            logger.error("Benutzer konnte nicht gelöscht werden: " + e);
        } catch (Exception e) {
            showNotification(Notification.Position.BOTTOM_END, "Irgendwas lief schief", NotificationVariant.LUMO_ERROR);
            logger.error("Es trat ein Fehler auf: " + e);
        }
    }

}
