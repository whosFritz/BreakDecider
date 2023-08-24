package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.whosfritz.breakdecider.Data.Entities.Status;
import com.whosfritz.breakdecider.Data.Services.VotingService;
import com.whosfritz.breakdecider.Security.SecurityService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static com.whosfritz.breakdecider.ui.utils.showNotification;


@PermitAll
@Route(value = "abstimmungErstellen", layout = MainView.class)
@PageTitle("Abstimmung erstellen")
public class CreateAbstimmungView extends VerticalLayout {
    private final VotingService votingService;
    private final SecurityService securityService;
    private final Logger logger = LoggerFactory.getLogger(CreateAbstimmungView.class);

    public CreateAbstimmungView(VotingService votingService, SecurityService securityService) {
        this.votingService = votingService;
        this.securityService = securityService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(createAbstimmungForm());
    }

    private VerticalLayout createAbstimmungForm() {
        TextField titelTF = new TextField("Abstimmungstitel");
        titelTF.setRequiredIndicatorVisible(true);
        titelTF.setPlaceholder("Urlaubsbilder in der Cloud");
        titelTF.setSizeFull();
        TextField beschreibungTF = new TextField("Beschreibung");

        beschreibungTF.setRequiredIndicatorVisible(true);
        beschreibungTF.setPlaceholder("Sind Urlaubsbilder in der Cloud sicher?");
        beschreibungTF.setSizeFull();
        Button createButton = new Button("Erstellen");
        createButton.setEnabled(false);
        titelTF.addValueChangeListener(valueChangeEvent -> createButton.setEnabled(!titelTF.getValue().isEmpty() && !beschreibungTF.getValue().isEmpty()));
        beschreibungTF.addValueChangeListener(valueChangeEvent -> createButton.setEnabled(!titelTF.getValue().isEmpty() && !beschreibungTF.getValue().isEmpty()));
        createButton.addClickShortcut(Key.ENTER);
        createButton.addClickListener(buttonClickEvent -> {
            // check if user is logged in
            if (!securityService.isUserLoggedIn()) {
                showNotification(Notification.Position.BOTTOM_END, "Bitte einloggen", NotificationVariant.LUMO_ERROR);
                return;
            }
            // check if titel is not too long
            if (titelTF.getValue().length() > 50) {
                showNotification(Notification.Position.BOTTOM_END, "Titel darf nicht länger als 50 Zeichen sein", NotificationVariant.LUMO_ERROR);
                return;
            }
            // check if beschreibung is not too long
            if (beschreibungTF.getValue().length() > 150) {
                showNotification(Notification.Position.BOTTOM_END, "Beschreibung darf nicht länger als 150 Zeichen sein", NotificationVariant.LUMO_ERROR);
                return;
            }
            // create voting
            try {
                votingService.handleCreateAbstimmung(
                        securityService.getAuthenticatedUser(),
                        LocalDateTime.now(),
                        Status.OPEN,
                        titelTF.getValue(),
                        beschreibungTF.getValue());
                titelTF.clear();
                beschreibungTF.clear();
                logger.info("Benutzer: " + securityService.getAuthenticatedUser().getUsername() + " hat eine Abstimmung erstellt mit Titel: " + titelTF.getValue());
                showNotification(Notification.Position.BOTTOM_END, "Abstimmung erfolgreich erstellt", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception e) {
                logger.error("Fehler beim Erstellen der Abstimmung: " + e.getMessage());
                showNotification(Notification.Position.BOTTOM_END, "Fehler beim Erstellen der Abstimmung", NotificationVariant.LUMO_ERROR);
            }
        });


        VerticalLayout layout = new VerticalLayout();
        layout.setMaxWidth("700px");
        layout.setMinWidth("700px");
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.add(
                titelTF,
                beschreibungTF,
                createButton
        );
        return layout;
    }

}
