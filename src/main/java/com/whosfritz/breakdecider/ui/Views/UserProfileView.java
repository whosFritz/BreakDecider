package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.whosfritz.breakdecider.Data.Entities.BreakDeciderUser;
import com.whosfritz.breakdecider.Data.Services.BreakDeciderUserService;
import com.whosfritz.breakdecider.Exception.NewEqualsOldPasswordException;
import com.whosfritz.breakdecider.Exception.PasswordIncorrectException;
import com.whosfritz.breakdecider.Security.SecurityService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.whosfritz.breakdecider.ui.utils.showNotification;

@PermitAll
@Route(value = "profile", layout = MainView.class)
@PageTitle("Abstimmung erstellen")
public class UserProfileView extends VerticalLayout {
    private final BreakDeciderUserService breakDeciderUserService;
    private final Logger logger = LoggerFactory.getLogger(UserProfileView.class);
    private final PasswordField oldPasswordField = new PasswordField();
    private final PasswordField newPasswordField = new PasswordField();
    private final PasswordField repeatPasswordField = new PasswordField();
    private final Button submitButton = new Button("Passwort ändern");

    public UserProfileView(BreakDeciderUserService breakDeciderUserService, SecurityService securityService) {
        this.breakDeciderUserService = breakDeciderUserService;
        // Create a form layout
        FormLayout formLayout = new FormLayout();

        // Add the password fields to the form layout
        formLayout.addFormItem(oldPasswordField, "Altes Passwort");
        formLayout.addFormItem(newPasswordField, "Neues Passwort");
        formLayout.addFormItem(repeatPasswordField, "Passwort wiederholen");
        formLayout.addFormItem(submitButton, "Passwort ändern");


        submitButton.addClickShortcut(Key.ENTER);
        submitButton.addClickListener(event -> {
            String oldPasswordString = oldPasswordField.getValue();
            String newPassword = newPasswordField.getValue();
            String repeatPassword = repeatPasswordField.getValue();
            newPasswordField.setErrorMessage("Das neue Passwort darf nicht leer sein.");

            // Check if the passwords match
            if (newPassword.equals(repeatPassword)) {
                pressUpdateButton(securityService.getAuthenticatedUser(), oldPasswordString, newPassword);
            } else {
                // Show an error notification
                showNotification(Notification.Position.BOTTOM_CENTER, "Die Passwörter stimmen nicht überein.", NotificationVariant.LUMO_ERROR);
            }
        });
        submitButton.addClickShortcut(Key.ENTER);
        add(formLayout);
    }

    private void pressUpdateButton(BreakDeciderUser breakDeciderUser, String oldPasswordString, String newPasswordString) {
        try {
            breakDeciderUserService.updateUser(breakDeciderUser, oldPasswordString, newPasswordString);
            logger.info("Password of user: " + breakDeciderUser.getUsername() + " successfully updated.");
            showNotification(Notification.Position.BOTTOM_END, "Passwort erfolgreich geändert!", NotificationVariant.LUMO_SUCCESS);
        } catch (NewEqualsOldPasswordException e) {
            logger.error("New password is the same as the old password of user: " + breakDeciderUser.getUsername());
            showNotification(Notification.Position.BOTTOM_CENTER, "Das neue Passwort darf nicht das alte Passwort sein.", NotificationVariant.LUMO_ERROR);
        } catch (PasswordIncorrectException e) {
            logger.error("Old password is incorrect of user: " + breakDeciderUser.getUsername());
            showNotification(Notification.Position.BOTTOM_CENTER, "Falsches Password.", NotificationVariant.LUMO_ERROR);
        } catch (UsernameNotFoundException e) {
            logger.error("User not found: " + breakDeciderUser.getUsername(), e);
            showNotification(Notification.Position.BOTTOM_CENTER, "Benutzer nicht gefunden.", NotificationVariant.LUMO_ERROR);
        } catch (IllegalArgumentException e) {
            logger.error("New password is invalid of user: " + breakDeciderUser.getUsername());
            showNotification(Notification.Position.BOTTOM_CENTER, "Das neue Passwort ist ungültig.", NotificationVariant.LUMO_ERROR);
        } catch (NullPointerException e) {
            logger.error("New password is null of user: " + breakDeciderUser.getUsername());
            showNotification(Notification.Position.BOTTOM_CENTER, "Das neue Passwort darf nicht leer sein.", NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            logger.error("Error while updating password of user: " + breakDeciderUser.getUsername());
            showNotification(Notification.Position.BOTTOM_CENTER, "Fehler beim Ändern des Passworts.", NotificationVariant.LUMO_ERROR);
        }
    }
}
