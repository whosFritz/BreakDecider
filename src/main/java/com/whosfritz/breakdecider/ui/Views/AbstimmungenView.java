package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.whosfritz.breakdecider.Data.Entities.Abstimmungsthema;
import com.whosfritz.breakdecider.Data.Entities.BreakDeciderUser;
import com.whosfritz.breakdecider.Data.Entities.Entscheidung;
import com.whosfritz.breakdecider.Data.Entities.Stimmzettel;
import com.whosfritz.breakdecider.Data.Services.AbstimmungsthemaService;
import com.whosfritz.breakdecider.Data.Services.VotingService;
import com.whosfritz.breakdecider.Security.SecurityService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@PermitAll
@Route(value = "", layout = MainView.class)
@PageTitle("Abstimmungen")
public class AbstimmungenView extends VerticalLayout {
    private final SecurityService securityService;
    private final VotingService votingService;
    private final Logger logger = LoggerFactory.getLogger(AbstimmungenView.class);

    public AbstimmungenView(AbstimmungsthemaService abstimmungsthemaService, SecurityService securityService, VotingService votingService) {
        this.securityService = securityService;
        this.votingService = votingService;


//        add(new Button("Test Button", event -> {
//            abstimmungsthemaService.saveAbstimmungsthema(testAbstimmungCreate());
//        }));


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

            VerticalLayout details = new VerticalLayout();
            details.setSpacing(false);
            details.setPadding(false);
            details.add(new Div(new Text(abstimmungsthema.getErsteller())));
            details
                    .add(new Div(new Text(abstimmungsthema.getErstelldatum().toString())));
            infoLayout
                    .add(new Details("Details", details));
            infoLayout.setWidth("800%");

            Dialog dialog = new Dialog();
            dialog.setDraggable(true);
            dialog.setHeaderTitle("Titel: " + abstimmungsthema.getTitel());
            dialog.add(new Paragraph("Beschreibung: "), new Paragraph(abstimmungsthema.getBeschreibung()));
            HorizontalLayout yes_no_buttons = new HorizontalLayout();
            Button yesButton = new Button("YES", (e) -> {
                handleInput(Entscheidung.JA, abstimmungsthema);
                dialog.close();
            });
            yesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                    ButtonVariant.LUMO_SUCCESS);
            Button noButton = new Button("NO", (e) -> {
                handleInput(Entscheidung.NEIN, abstimmungsthema);
                dialog.close();
            });
            noButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                    ButtonVariant.LUMO_ERROR);

            yes_no_buttons.add(yesButton, noButton);

            Button cancelButton = new Button("Abbrechen", (e) -> {
                dialog.close();
            });
            dialog.add(yes_no_buttons);
            cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog.getFooter().add(cancelButton);
            Button clickButton = new Button("Abstimmen", event -> {
                dialog.open();
            });
            clickButton.setEnabled(ableToVote(securityService.getAuthenticatedUser(), abstimmungsthema));
            clickButton.addClassNames(LumoUtility.Padding.MEDIUM);
            VerticalLayout votingLayout = new VerticalLayout();
            // Icon after a text
            Span confirmed2 = new Span("Zugestimmt: " + getYesCounts(abstimmungsthema));
            confirmed2.getElement().getThemeList().add("badge success");
            confirmed2.setMaxWidth("300");
            Span denied2 = new Span("Abgelehnt: " + getNoCounts(abstimmungsthema));
            denied2.getElement().getThemeList().add("badge error");
            denied2.setWidth("300");
            votingLayout.add(confirmed2, denied2);
            votingLayout.setPadding(false);
            votingLayout.setMargin(false);

            setPadding(false);
            setSizeUndefined();

            cardLayout.add(new Icon(VaadinIcon.User), infoLayout, clickButton, dialog, votingLayout);
            list.add(cardLayout);

        }
        add(list);
    }

    private Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-m");
        return icon;
    }

    private void handleInput(
            Entscheidung entscheidung,
            Abstimmungsthema abstimmungsthema
    ) {
        LocalDate localDate = LocalDate.now();
        if (entscheidung.equals(Entscheidung.JA)) {
            try {
                votingService.handleVote(entscheidung, localDate, securityService.getAuthenticatedUser(), abstimmungsthema);
            } catch (Exception e) {
                logger.error("Fehler beim Ja-Abstimmen: " + e.getMessage());
            }
        } else {
            try {
                votingService.handleVote(entscheidung, localDate, securityService.getAuthenticatedUser(), abstimmungsthema);
            } catch (Exception e) {
                logger.error("Fehler beim Nein-Abstimmen: " + e.getMessage());
            }
        }
    }


    public int getYesCounts(Abstimmungsthema abstimmungsthema) {
        int yesVoteCount = 0;
        for (Stimmzettel stimmzettel : abstimmungsthema.getStimmzettelList()) {
            if (stimmzettel.getEntscheidung() == Entscheidung.JA) {
                yesVoteCount++;
            }
        }
        return yesVoteCount;
    }

    public int getNoCounts(Abstimmungsthema abstimmungsthema) {
        int yesVoteCount = 0;
        for (Stimmzettel stimmzettel : abstimmungsthema.getStimmzettelList()) {
            if (stimmzettel.getEntscheidung() == Entscheidung.NEIN) {
                yesVoteCount++;
            }
        }
        return yesVoteCount;
    }

    public boolean ableToVote(BreakDeciderUser user, Abstimmungsthema abstimmungsthema) {
//        if (user.getAbstimmungsthema() != null) {
//            if (user.getAbstimmungsthema().getStimmzettelList() != null) {
//                for (Stimmzettel stimmzettel : user.getAbstimmungsthema().getStimmzettelList()) {
//                    if (stimmzettel.getAbstimmungsthema().equals(abstimmungsthema)) {
//                        return false;
//                    }
//                }
//            } else {
//                throw new NullPointerException("User hat kein Stimmzettel");
//            }
//        } else {
//            throw new NullPointerException("User hat kein Abstimmungsthema");
//        }


        // Wenn der Benutzer noch nicht abgestimmt hat, kann er/sie abstimmen
        // Füge hier den Code hinzu, um den Stimmzettel für den Benutzer und das Abstimmungsthema zu erstellen

        return true;
    }
}
