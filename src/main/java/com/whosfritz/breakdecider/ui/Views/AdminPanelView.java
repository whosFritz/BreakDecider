package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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

import java.util.Collection;

import static com.whosfritz.breakdecider.Registration.SecretRegistrationToken.REGISTRATION_TOKEN;
import static com.whosfritz.breakdecider.ui.utils.formatDateString;
import static com.whosfritz.breakdecider.ui.utils.showNotification;

@RolesAllowed("ROLE_ADMIN")
@PageTitle("Admin Panel")
@Route(value = "adminPanel", layout = MainView.class)
public class AdminPanelView extends VerticalLayout {
    private final BreakDeciderUserService breakDeciderUserService;
    private final RegistrationService registrationService;
    private final SecurityService securityService;
    private final AbstimmungsthemaService abstimmungsthemaService;
    private final ListDataProvider<Abstimmungsthema> abstimmungsthemaListDataProvider;
    private final ListDataProvider<BreakDeciderUser> breakDeciderUserGridDataProvider;

    private final TextField usernameTF = new TextField();
    private final PasswordField passwordPF = new PasswordField();
    private final ComboBox<AppUserRole> appUserRoleCB = new ComboBox<>();
    private final Logger logger = LoggerFactory.getLogger(AdminPanelView.class);


    public AdminPanelView(BreakDeciderUserService breakDeciderUserService, RegistrationService registrationService, AbstimmungsthemaService abstimmungsthemaService, SecurityService securityService) {
        this.breakDeciderUserService = breakDeciderUserService;
        this.registrationService = registrationService;
        this.securityService = securityService;
        this.abstimmungsthemaService = abstimmungsthemaService;
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
        createUserButton.addClickShortcut(Key.ENTER);

        Grid<BreakDeciderUser> breakDeciderUserGrid = new Grid<>();
        breakDeciderUserGridDataProvider = DataProvider.ofCollection(breakDeciderUserService.getAllUsers());
        breakDeciderUserGrid.setDataProvider(breakDeciderUserGridDataProvider);

        breakDeciderUserGrid.getColumns().forEach(breakDeciderUserColumn -> breakDeciderUserColumn.setAutoWidth(true));

        breakDeciderUserGrid.addColumn(BreakDeciderUser::getUsername).setHeader("Benutzername").setSortable(true).setResizable(true);
        Grid.Column<BreakDeciderUser> rolleColumn = breakDeciderUserGrid.addColumn(BreakDeciderUser::getAppUserRole).setHeader("Rolle").setSortable(true).setResizable(true).setFooter(createMembershipFooterText(breakDeciderUserGridDataProvider.getItems()));
        breakDeciderUserGrid.addColumn(BreakDeciderUser::getLocked).setHeader("Locked").setSortable(true).setResizable(true);

        Grid.Column<BreakDeciderUser> enabledColumn = breakDeciderUserGrid.addComponentColumn(breakDeciderUser -> {
            ComboBox<Boolean> enabledComboBox = new ComboBox<>();
            enabledComboBox.setItems(true, false);
            enabledComboBox.setValue(breakDeciderUser.isEnabled());
            enabledComboBox.addValueChangeListener(event -> {
                Boolean changedEnabled = event.getValue();
                if (changedEnabled != breakDeciderUser.isEnabled()) {
                    if (changedEnabled) {
                        breakDeciderUserService.enableUser(breakDeciderUser);
                        showNotification(Notification.Position.BOTTOM_END, "Benutzer freigeschaltet", NotificationVariant.LUMO_SUCCESS);
                        logger.info("Benutzer: " + breakDeciderUser.getUsername() + " freigeschaltet von: " + this.securityService.getAuthenticatedUser().getUsername());
                    } else {
                        breakDeciderUserService.disableUser(breakDeciderUser);
                        showNotification(Notification.Position.BOTTOM_END, "Benutzer gesperrt", NotificationVariant.LUMO_SUCCESS);
                        logger.info("Benutzer: " + breakDeciderUser.getUsername() + " gesperrt von: " + this.securityService.getAuthenticatedUser().getUsername());
                    }
                    breakDeciderUserGridDataProvider.refreshItem(breakDeciderUser);
                }
            });
            return enabledComboBox;
        }).setHeader("Enabled").setSortable(true).setResizable(true).setFooter(createEnabledFooter(breakDeciderUserGridDataProvider.getItems()));
        breakDeciderUserGrid.addColumn(
                new ComponentRenderer<>(Button::new, (button, breakDeciderUser) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> {
                        this.handleDeletionOfBreakDeciderUser(breakDeciderUser);
                        breakDeciderUserGridDataProvider.getItems().remove(breakDeciderUser);
                        breakDeciderUserGridDataProvider.refreshAll();
                    });
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setHeader("Töten").setSortable(true).setResizable(true);


        Grid<Abstimmungsthema> abstimmungsthemaGridAdmin = new Grid<>();
        abstimmungsthemaListDataProvider = DataProvider.ofCollection(abstimmungsthemaService.getAllAbstimmungsthemen());
        abstimmungsthemaGridAdmin.setDataProvider(abstimmungsthemaListDataProvider);
        abstimmungsthemaGridAdmin.getColumns().forEach(abstimmungsthemaColumn -> abstimmungsthemaColumn.setAutoWidth(true));
        abstimmungsthemaGridAdmin.setSelectionMode(Grid.SelectionMode.MULTI);
        abstimmungsthemaGridAdmin.addColumn(Abstimmungsthema::getTitel).setHeader("Titel").setSortable(true).setResizable(true);
        abstimmungsthemaGridAdmin.addColumn(Abstimmungsthema::getBeschreibung).setHeader("Beschreibung").setSortable(true).setResizable(true);
        abstimmungsthemaGridAdmin.addColumn(Abstimmungsthema::getErsteller).setHeader("Ersteller").setSortable(true).setResizable(true);
        abstimmungsthemaGridAdmin.addColumn(abstimmungsthema -> formatDateString(abstimmungsthema.getErstelldatum().toString())).setHeader("Erstellungsdatum").setSortable(true).setResizable(true);

        Grid.Column<Abstimmungsthema> statusColumn = abstimmungsthemaGridAdmin.addComponentColumn(abstimmungsthema -> {
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
                    } else {
                        abstimmungsthemaService.openAbstimmungsthema(abstimmungsthema);
                        showNotification(Notification.Position.BOTTOM_END, "Wieder eröffnet Abstimmung geöffnet", NotificationVariant.LUMO_SUCCESS);
                        logger.info("Abstimmung mit Titel: " + abstimmungsthema.getTitel() + " wurde geöffnet von: " + this.securityService.getAuthenticatedUser().getUsername());
                    }
                    abstimmungsthemaListDataProvider.refreshItem(abstimmungsthema);
                }

            });
            return statusComboBox;
        }).setHeader("Status").setSortable(true).setResizable(true).setFooter(createStatusFooter(abstimmungsthemaListDataProvider.getItems()));

        abstimmungsthemaGridAdmin.addItemClickListener(event -> {
            // add selected item to the list of selected items
            if (abstimmungsthemaGridAdmin.getSelectedItems().contains(event.getItem())) {
                abstimmungsthemaGridAdmin.getSelectionModel().deselect(event.getItem());
            } else {
                abstimmungsthemaGridAdmin.getSelectionModel().select(event.getItem());
            }
        });

        Button deleteAbstimmungButton = new Button("Abstimmungen löschen");
        deleteAbstimmungButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        deleteAbstimmungButton.addClickListener(event -> {
            for (Abstimmungsthema abstimmungsthema : abstimmungsthemaGridAdmin.getSelectedItems()) {
                handleDeletionOfAbstimmung(abstimmungsthema);
                abstimmungsthemaListDataProvider.getItems().remove(abstimmungsthema);
            }
            abstimmungsthemaListDataProvider.refreshAll();
        });

        breakDeciderUserGridDataProvider.addDataProviderListener(event -> {
            rolleColumn.setFooter(createMembershipFooterText(breakDeciderUserGridDataProvider.getItems()));
            enabledColumn.setFooter(createEnabledFooter(breakDeciderUserGridDataProvider.getItems()));
        });

        abstimmungsthemaListDataProvider.addDataProviderListener(event -> {
            statusColumn.setFooter(createStatusFooter(abstimmungsthemaListDataProvider.getItems()));
        });

        add(paragraph, formLayout, breakDeciderUserGrid, abstimmungsthemaGridAdmin, deleteAbstimmungButton);
    }

    private static String createMembershipFooterText(Collection<BreakDeciderUser> breakDeciderUsers) {
        long regularCount = breakDeciderUsers.size();
        long userCount = breakDeciderUsers.stream().filter(person -> AppUserRole.ROLE_USER.equals(person.getAppUserRole())).count();

        long adminCount = breakDeciderUsers.stream()
                .filter(person -> AppUserRole.ROLE_ADMIN.equals(person.getAppUserRole()))
                .count();

        return String.format("%s Users insgesamt, %s Nicht-Admins, %s Admins", regularCount,
                userCount, adminCount);
    }

    private static String createEnabledFooter(Collection<BreakDeciderUser> breakDeciderUsers) {
        long enabled = breakDeciderUsers.stream().filter(BreakDeciderUser::isEnabled).count();

        long disabled = breakDeciderUsers.stream().filter(breakDeciderUser -> !breakDeciderUser.isEnabled()).count();

        return String.format("%s enabled, %s disabled", enabled, disabled);
    }

    private static String createStatusFooter(Collection<Abstimmungsthema> abstimmungsthemen) {
        long open = abstimmungsthemen.stream().filter(abstimmungsthema -> Status.OPEN.equals(abstimmungsthema.getStatus())).count();
        long closed = abstimmungsthemen.stream().filter(abstimmungsthema -> Status.CLOSED.equals(abstimmungsthema.getStatus())).count();


        return String.format("%s offen, %s geschlossen", open, closed);
    }


    private void createUser() {
        String username = usernameTF.getValue();
        String password = passwordPF.getValue();
        AppUserRole appUserRole = appUserRoleCB.getValue();

        if (username != null && password != null && appUserRole != null) {
            try {
                BreakDeciderUser newBreakDeciderUser = registrationService.register(new RegistrationRequest(username, password, appUserRole, REGISTRATION_TOKEN));
                breakDeciderUserGridDataProvider.getItems().add(newBreakDeciderUser);
                breakDeciderUserGridDataProvider.refreshAll();
                usernameTF.clear();
                passwordPF.clear();
                appUserRoleCB.clear();
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

    private void handleDeletionOfBreakDeciderUser(BreakDeciderUser breakDeciderUser) {
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

    private void handleDeletionOfAbstimmung(Abstimmungsthema abstimmungsthema) {
        try {
            abstimmungsthemaService.deleteAbstimmungsthema(abstimmungsthema);
            showNotification(Notification.Position.BOTTOM_END, "Abstimmung gelöscht", NotificationVariant.LUMO_SUCCESS);
            logger.info("Abstimmung mit Titel: " + abstimmungsthema.getTitel() + " gelöscht von: " + this.securityService.getAuthenticatedUser().getUsername());
        } catch (NullPointerException exception) {
            showNotification(Notification.Position.BOTTOM_END, "Abstimmung konnte nicht gelöscht werden", NotificationVariant.LUMO_ERROR);
            logger.error("Abstimmung konnte nicht gelöscht werden: " + exception.getMessage());
        } catch (DataIntegrityViolationException exception) {
            showNotification(Notification.Position.BOTTOM_END, "Abstimmung konnte SQL mäßig nicht gelöscht werden", NotificationVariant.LUMO_ERROR);
            logger.error("Abstimmung konnte nicht gelöscht werden: " + exception.getMessage());
        } catch (Exception exception) {
            showNotification(Notification.Position.BOTTOM_END, "Irgendwas lief schief", NotificationVariant.LUMO_ERROR);
            logger.error("Es trat ein Fehler auf: " + exception.getMessage());
        }
    }
}
