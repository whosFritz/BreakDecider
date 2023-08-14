package com.whosfritz.breakdecider.ui.Views;

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
import com.whosfritz.breakdecider.Security.SecurityService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.whosfritz.breakdecider.ui.utils.showNotification;

@PermitAll
@Route(value = "profile", layout = MainView.class)
@PageTitle("Abstimmung erstellen")
public class UserProfileView extends VerticalLayout {
    private final BreakDeciderUserService breakDeciderUserService;
    private final Logger logger = LoggerFactory.getLogger(UserProfileView.class);

    public UserProfileView(BreakDeciderUserService breakDeciderUserService, SecurityService securityService) {
        this.breakDeciderUserService = breakDeciderUserService;
        // Create a form layout
        FormLayout formLayout = new FormLayout();

        // Create the password fields
        PasswordField oldPasswordField = new PasswordField("Altes Passwort");
        PasswordField newPasswordField = new PasswordField("Neues Passwort");
        PasswordField repeatPasswordField = new PasswordField("Passwort wiederholen");

        // Add the password fields to the form layout
        formLayout.add(newPasswordField, repeatPasswordField);

        // Create a button for submitting the form
        Button submitButton = new Button("Passwort ändern", event -> {
            // Get the values entered in the password fields
            String oldPassword = oldPasswordField.getValue();
            String newPassword = newPasswordField.getValue();
            String repeatPassword = repeatPasswordField.getValue();
            // Check if the passwords match
            if (newPassword.equals(repeatPassword)) {
                pressUpdateButton(securityService.getAuthenticatedUser(), oldPassword, newPassword);


            } else {
                // Show an error notification
                showNotification(Notification.Position.BOTTOM_CENTER, "Die Passwörter stimmen nicht überein.", NotificationVariant.LUMO_ERROR);
            }
        });

        // Add the form layout and submit button to the view
        add(formLayout, submitButton);
    }

    private void pressUpdateButton(BreakDeciderUser breakDeciderUser, String oldPassword, String newPassword) {
        try {
            breakDeciderUserService.updateUser(breakDeciderUser, oldPassword, newPassword);
            showNotification(Notification.Position.BOTTOM_CENTER, "Fehler beim Ändern des Passworts.", NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            logger.error("Error while updating password of user: " + breakDeciderUser.getUsername(), e);
            showNotification(Notification.Position.BOTTOM_END, "Passwort erfolgreich geändert!", NotificationVariant.LUMO_SUCCESS);
        }
    }
}
