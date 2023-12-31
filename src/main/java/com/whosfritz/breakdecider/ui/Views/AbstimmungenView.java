package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.whosfritz.breakdecider.ui.utils.showNotification;

@PermitAll
@Route(value = "", layout = MainView.class)
@PageTitle("Abstimmungen")
public class AbstimmungenView extends VerticalLayout {
    private final SecurityService securityService;
    private final VotingService votingService;
    private final AbstimmungsthemaService abstimmungsthemaService;
    private final Logger logger = LoggerFactory.getLogger(AbstimmungenView.class);
    private final Grid<Abstimmungsthema> abstimmungsthemaGrid = new Grid<>();

    public AbstimmungenView(SecurityService securityService, VotingService votingService, AbstimmungsthemaService abstimmungsthemaService) {
        this.securityService = securityService;
        this.votingService = votingService;
        this.abstimmungsthemaService = abstimmungsthemaService;

        ListDataProvider<Abstimmungsthema> listDataProvider = DataProvider.ofCollection(abstimmungsthemaService.getAllAbstimmungsthemen());
        abstimmungsthemaGrid.setDataProvider(listDataProvider);


        abstimmungsthemaGrid.addColumn(Abstimmungsthema::getStatus).setHeader("Status");
        abstimmungsthemaGrid.addColumn(Abstimmungsthema::getTitel).setHeader("Titel");
        abstimmungsthemaGrid.addColumn(Abstimmungsthema::getErsteller).setHeader("Ersteller");
        abstimmungsthemaGrid.addColumn(Abstimmungsthema::getBeschreibung).setHeader("Beschreibung");

        abstimmungsthemaGrid.addColumn(new LocalDateTimeRenderer<>(Abstimmungsthema::getErstelldatum, () -> DateTimeFormatter.ofPattern("EEEE H:mm 'Uhr', dd. MMMM yyyy", Locale.GERMANY))).setHeader("Erstellungsdatum");
        abstimmungsthemaGrid.addColumn(abstimmungsthema -> countVotes(abstimmungsthema, Entscheidung.JA)).setHeader("Ja").setTextAlign(ColumnTextAlign.CENTER);
        abstimmungsthemaGrid.addColumn(abstimmungsthema -> countVotes(abstimmungsthema, Entscheidung.NEIN)).setHeader("Nein").setTextAlign(ColumnTextAlign.CENTER);
        abstimmungsthemaGrid.addComponentColumn(abstimmungsthema -> {
            HorizontalLayout layout = new HorizontalLayout();
            Button yesButton = new Button("Ja", event -> handleInput(Entscheidung.JA, abstimmungsthema));
            yesButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            yesButton.setEnabled(enableButtons(abstimmungsthema));
            layout.add(yesButton);

            Button noButton = new Button("Nein", event -> handleInput(Entscheidung.NEIN, abstimmungsthema));
            noButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            noButton.setEnabled(enableButtons(abstimmungsthema));
            layout.add(noButton);

            return layout;
        }).setHeader("Abstimmen").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);
        abstimmungsthemaGrid.addComponentColumn(this::hasVoted).setHeader("Abgestimmt").setTextAlign(ColumnTextAlign.CENTER);

        abstimmungsthemaGrid.getColumns().forEach(abstimmungsthemaColumn -> abstimmungsthemaColumn.setResizable(true).setAutoWidth(true).setSortable(true));
        add(abstimmungsthemaGrid);
    }

    private Icon hasVoted(Abstimmungsthema abstimmungsthema) {
        for (Stimmzettel stimmzettel : abstimmungsthema.getStimmzettelSet()) {
            if (stimmzettel.getBreakDeciderUser().getId().equals(securityService.getAuthenticatedUser().getId())) {
                Icon icon = new Icon(VaadinIcon.CHECK);
                icon.setColor("green");
                return icon;
            }
        }
        Icon icon = new Icon(VaadinIcon.CLOSE);
        icon.setColor("red");
        return icon;
    }

    private void handleInput(
            Entscheidung entscheidung,
            Abstimmungsthema abstimmungsthema
    ) {
        try {
            String result = votingService.handleVote(entscheidung, LocalDateTime.now(), securityService.getAuthenticatedUser(), abstimmungsthema);
            showNotification(Notification.Position.BOTTOM_END, result, NotificationVariant.LUMO_SUCCESS);
            if (result.equals("Stimme zurückgezogen")) {
                logger.info("Benutzer " + securityService.getAuthenticatedUser().getUsername() + " hat für das Thema " + abstimmungsthema.getTitel() + " mit " + entscheidung + " abgestimmt.");
            } else {
                logger.info("Benutzer " + securityService.getAuthenticatedUser().getUsername() + " hat seine Abstimmung für das Thema " + abstimmungsthema.getTitel() + " zurückgezogen.");
            }
            abstimmungsthemaGrid.setItems(abstimmungsthemaService.getAllAbstimmungsthemen());
        } catch (Exception e) {
            showNotification(Notification.Position.BOTTOM_END, "Fehler beim " + entscheidung + "-Abstimmen: ", NotificationVariant.LUMO_ERROR);
            logger.error("Fehler beim " + entscheidung + "-Abstimmen: " + e.getMessage());
        }
    }

    public int countVotes(Abstimmungsthema abstimmungsthema, Entscheidung entscheidung) {
        int voteCount = 0;
        for (Stimmzettel stimmzettel : abstimmungsthema.getStimmzettelSet()) {
            if (stimmzettel.getEntscheidung() == entscheidung) {
                voteCount++;
            }
        }
        return voteCount;
    }

    private boolean enableButtons(Abstimmungsthema abstimmungsthema) {
        return abstimmungsthema.getStatus() != Status.CLOSED;
    }
}
