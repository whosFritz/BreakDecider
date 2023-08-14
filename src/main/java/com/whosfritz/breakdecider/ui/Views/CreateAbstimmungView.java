package com.whosfritz.breakdecider.ui.Views;

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

import java.time.LocalDate;

import static com.whosfritz.breakdecider.ui.utils.showVoteNotification;


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
        addClassName("create-abstimmung-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(
                createAbstimmungForm()
        );
    }

    public VerticalLayout createAbstimmungForm() {
        TextField titelTF = new TextField("Abstimmungstitel");
        titelTF.setSizeFull();
        TextField beschreibungTF = new TextField("Beschreibung");
        beschreibungTF.setSizeFull();
        Button button = new Button("Erstellen", buttonClickEvent -> {
            try {
                votingService.handleCreateAbstimmung(
                        securityService.getAuthenticatedUser(),
                        LocalDate.now(), Status.OPEN,
                        titelTF.getValue(),
                        beschreibungTF.getValue());
                titelTF.clear();
                beschreibungTF.clear();
                logger.info("User: " + securityService.getAuthenticatedUser().getUsername() + " hat eine Abstimmung erstellt");
                showVoteNotification(Notification.Position.TOP_END, "Abstimmung erfolgreich erstellt", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception e) {
                logger.error("Fehler beim Erstellen der Abstimmung: " + e.getMessage());
                showVoteNotification(Notification.Position.TOP_END, "Fehler beim Erstellen der Abstimmung", NotificationVariant.LUMO_ERROR);
            }
        });
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("create-abstimmung-form");
        layout.setMaxWidth("500px");
        layout.setMinWidth("500px");
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.add(
                titelTF,
                beschreibungTF,
                button
        );
        return layout;
    }

}
