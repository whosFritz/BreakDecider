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

import static com.whosfritz.breakdecider.ui.utils.showNotification;

@PermitAll
@Route(value = "profile", layout = MainView.class)
@PageTitle("Abstimmung erstellen")
public class UserProfileView extends VerticalLayout {
    BreakDeciderUserService breakDeciderUserService;
    SecurityService securityService;

    public UserProfileView(BreakDeciderUserService breakDeciderUserService, SecurityService securityService) {
        this.breakDeciderUserService = breakDeciderUserService;
        this.securityService = securityService;
        // Create a form layout
        FormLayout formLayout = new FormLayout();

        // Create the password fields
        PasswordField newPasswordField = new PasswordField("Neues Passwort");
        PasswordField repeatPasswordField = new PasswordField("Passwort wiederholen");

        // Add the password fields to the form layout
        formLayout.add(newPasswordField, repeatPasswordField);

        // Create a button for submitting the form
        Button submitButton = new Button("Passwort ändern", event -> {
            // Get the values entered in the password fields
            String newPassword = newPasswordField.getValue();
            String repeatPassword = repeatPasswordField.getValue();
            // Check if the passwords match
            if (newPassword.equals(repeatPassword)) {
                try {

                    pressUpdateButton(securityService.getAuthenticatedUser(), newPassword);
                } catch (Exception e) {
                    e.printStackTrace();
                    showNotification(Notification.Position.BOTTOM_CENTER, "Fehler beim Ändern des Passworts.", NotificationVariant.LUMO_ERROR);
                }
                showNotification(Notification.Position.TOP_END, "Passwort erfolgreich geändert!", NotificationVariant.LUMO_SUCCESS);
            } else {
                // Show an error notification
                showNotification(Notification.Position.BOTTOM_CENTER, "Die Passwörter stimmen nicht überein.", NotificationVariant.LUMO_ERROR);
            }
        });

        // Add the form layout and submit button to the view
        add(formLayout, submitButton);
    }

    private void pressUpdateButton(BreakDeciderUser breakDeciderUser, String newPassword) {
        breakDeciderUserService.updateUser(breakDeciderUser, newPassword);
    }

}
