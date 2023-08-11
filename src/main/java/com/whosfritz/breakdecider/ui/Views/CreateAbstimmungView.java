package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.whosfritz.breakdecider.Data.Entities.Status;
import com.whosfritz.breakdecider.Data.Services.VotingService;
import com.whosfritz.breakdecider.Security.SecurityService;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;


@PermitAll
@Route(value = "abstimmungErstellen", layout = MainView.class)
@PageTitle("Abstimmung erstellen")
public class CreateAbstimmungView extends VerticalLayout {
    private final VotingService votingService;
    private final SecurityService securityService;

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
        TextField beschreibungTF = new TextField("Beschreibung");
        Button button = new Button("Erstellen");
        button.addClickListener(buttonClickEvent -> {
            LocalDate localDate = LocalDate.now();
            votingService.handleCreateAbstimmung(
                    securityService.getAuthenticatedUser(),
                    localDate, Status.OPEN,
                    titelTF.getValue(),
                    beschreibungTF.getValue());
            System.out.println("Abstimmung erstellt");
        });

        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("create-abstimmung-form");
        layout.setSizeFull();
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
