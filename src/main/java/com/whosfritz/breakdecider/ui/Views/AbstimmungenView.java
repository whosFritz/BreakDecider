package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.whosfritz.breakdecider.Data.Entities.Abstimmungsthema;
import com.whosfritz.breakdecider.Data.Entities.Entscheidung;
import com.whosfritz.breakdecider.Data.Entities.Status;
import com.whosfritz.breakdecider.Data.Entities.Stimmzettel;
import com.whosfritz.breakdecider.Data.Services.AbstimmungsthemaService;
import com.whosfritz.breakdecider.Data.Services.VotingService;
import com.whosfritz.breakdecider.Security.SecurityService;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.util.List;

@PermitAll
@Route(value = "", layout = MainView.class)
@PageTitle("Abstimmungen")
public class AbstimmungenView extends VerticalLayout {
    private final AbstimmungsthemaService abstimmungsthemaService;
    private final SecurityService securityService;

    public AbstimmungenView(AbstimmungsthemaService abstimmungsthemaService, SecurityService securityService) {
        this.abstimmungsthemaService = abstimmungsthemaService;
        this.securityService = securityService;
        // Create a BreakDeciderUser instance (if not already created)
        // Create an Abstimmungsthema instance (if not already created)
        LocalDate date = LocalDate.now();
        Abstimmungsthema abstimmungsthema1 = new Abstimmungsthema("John Doe", date, Status.OPEN, "Favorite Color Poll", "Vote for your favorite color.");

        // Create a Stimmzettel instance and associate it with the user
        Stimmzettel stimmzettel1 = new Stimmzettel(Entscheidung.JA, date, securityService.getAuthenticatedUser(), abstimmungsthema1);
        abstimmungsthema1.getStimmzettelList().add(stimmzettel1);
        // Add the Stimmzettel instance to the Abstimmungsthema's stimmzettelList

        Button testButton = new Button("Test Button", event -> {
            abstimmungsthemaService.saveAbstimmungsthema(abstimmungsthema1);
        });
        add(new Text(securityService.getAuthenticatedUser().getUsername()));
        add(testButton);
        add(new Button("logout", event -> {
            securityService.logout();
        }));

        List<Abstimmungsthema> abstimmungsthemen = abstimmungsthemaService.getAllAbstimmungsthemen(); // Get the list of Abstimmungsthemen from the service

        VerticalLayout list = new VerticalLayout();
        for (Abstimmungsthema abstimmungsthema : abstimmungsthemen) {
            HorizontalLayout cardLayout = new HorizontalLayout();
            cardLayout.setMargin(true);

            VerticalLayout infoLayout = new VerticalLayout();
            infoLayout.setSpacing(false);
            infoLayout.setPadding(false);
            infoLayout.getElement().appendChild(
                    ElementFactory.createStrong(abstimmungsthema.getTitel()));
            infoLayout.add(new Div(new Text(abstimmungsthema.getBeschreibung())));

            VerticalLayout contactLayout = new VerticalLayout();
            contactLayout.setSpacing(false);
            contactLayout.setPadding(false);
            contactLayout.add(new Div(new Text(abstimmungsthema.getErsteller())));
            contactLayout
                    .add(new Div(new Text(abstimmungsthema.getErstelldatum().toString())));
            infoLayout
                    .add(new Details("Details", contactLayout));

            Dialog dialog = new Dialog();
            dialog.setDraggable(true);
            dialog.setHeaderTitle("Abstimmen über Thema: " + abstimmungsthema.getTitel());
            HorizontalLayout yes_no_buttons = new HorizontalLayout();
            Button yesButton = new Button("YES", (e) -> {
                applicateVote(Entscheidung.JA, abstimmungsthema);
                dialog.close();
            });
            yesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                    ButtonVariant.LUMO_SUCCESS);
            Button noButton = new Button("NO", (e) -> {
                applicateVote(Entscheidung.NEIN, abstimmungsthema);
                dialog.close();
            });
            noButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                    ButtonVariant.LUMO_ERROR);

            yes_no_buttons.add(yesButton, noButton);

            Button cancelButton = new Button("Cancel", (e) -> {
                System.out.println("Cancel");
                dialog.close();
            });
            dialog.add(yes_no_buttons);
            cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog.getFooter().add(cancelButton);
            Button clickButton = new Button(new Icon(VaadinIcon.CHECK), event -> {
                dialog.open();
            });

            cardLayout.add(new Icon(VaadinIcon.USER), infoLayout, clickButton, dialog);
            list.add(cardLayout);
        }
        add(list);
    }

    private void applicateVote(
            Entscheidung entscheidung,
            Abstimmungsthema abstimmungsthema
    ) {
        VotingService votingService = new VotingService(
                abstimmungsthemaService
        );
        LocalDate localDate = LocalDate.now();
        if (entscheidung.equals(Entscheidung.JA)) {
            votingService.handleVote(entscheidung, localDate, securityService.getAuthenticatedUser(), abstimmungsthema);
        } else {
            votingService.handleVote(entscheidung, localDate, securityService.getAuthenticatedUser(), abstimmungsthema);

        }
    }


}
