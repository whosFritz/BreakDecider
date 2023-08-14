package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.whosfritz.breakdecider.Data.Entities.*;
import com.whosfritz.breakdecider.Data.Services.AbstimmungsthemaService;
import com.whosfritz.breakdecider.Data.Services.VotingService;
import com.whosfritz.breakdecider.Security.SecurityService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.whosfritz.breakdecider.ui.utils.showVoteNotification;

@PermitAll
@Route(value = "", layout = MainView.class)
@PageTitle("Abstimmungen")
public class AbstimmungenView extends VerticalLayout {
    private final SecurityService securityService;
    private final VotingService votingService;
    private final Logger logger = LoggerFactory.getLogger(AbstimmungenView.class);
    private Grid<Abstimmungsthema> list = new Grid<>(Abstimmungsthema.class);

    public AbstimmungenView(AbstimmungsthemaService abstimmungsthemaService, SecurityService securityService, VotingService votingService) {
        this.securityService = securityService;
        this.votingService = votingService;


        List<Abstimmungsthema> abstimmungsthemenList = abstimmungsthemaService.getAllAbstimmungsthemen(); // Get the list of Abstimmungsthemen from the service
        list.setColumns("status", "titel", "beschreibung", "ersteller", "erstelldatum");

        // Set the column headers
        list.getColumnByKey("ersteller").setHeader("Ersteller").setSortable(true);
        list.getColumnByKey("erstelldatum").setHeader("Erstelldatum").setSortable(true);
        list.getColumnByKey("status").setHeader("Status").setSortable(true);
        list.getColumnByKey("titel").setHeader("Titel").setSortable(true);
        list.getColumnByKey("beschreibung").setHeader("Beschreibung").setSortable(true);
        // Add a custom column for the "Yes" count
        list.addColumn(abstimmungsthema -> getYesCount(abstimmungsthema))
                .setHeader("Ja");
        // Add a custom column for the "No" count
        list.addColumn(abstimmungsthema -> getNoCount(abstimmungsthema))
                .setHeader("Nein");
        // Add a custom column for the "Yes" button
        list.addComponentColumn(abstimmungsthema -> {
            Button yesButton = new Button("Ja", event -> {
                handleInput(Entscheidung.JA, abstimmungsthema);
            });
            yesButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            yesButton.setEnabled(enableButtons(abstimmungsthema));
            return yesButton;
        }).setHeader("Ja");

        // Add a custom column for the "No" button
        list.addComponentColumn(abstimmungsthema -> {
            Button noButton = new Button("Nein", event -> {
                handleInput(Entscheidung.NEIN, abstimmungsthema);
            });
            noButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            noButton.setEnabled(enableButtons(abstimmungsthema));
            return noButton;
        }).setHeader("Nein");


        list.getColumns().forEach(abstimmungsthemaColumn -> abstimmungsthemaColumn.setAutoWidth(true));


        // Set the items (data) for the grid
        list.setItems(abstimmungsthemenList);

        // Set the selection mode to single (or multi, if you want to allow multiple selection)
        list.setSelectionMode(Grid.SelectionMode.SINGLE);

        // Add a selection listener to the grid's selection model
        list.getSelectionModel().addSelectionListener(event -> {
            // Get the selected item (if any)
            Optional<Abstimmungsthema> selectedItem = event.getFirstSelectedItem();
            if (selectedItem.isPresent()) {
                Abstimmungsthema abstimmungsthema = selectedItem.get();
                // If the selected item has Status.CLOSED, deselect it
                if (abstimmungsthema.getStatus() == Status.CLOSED) {
                    list.deselect(abstimmungsthema);
                }
            }
        });
        add(list);
    }


    private Dialog createAbstimmungDialog(Abstimmungsthema thema) {
        Dialog dialog = new Dialog();
        dialog.setDraggable(true);
        dialog.setHeaderTitle("Titel: " + thema.getTitel());
        dialog.add(new Paragraph("Beschreibung: "), new Paragraph(thema.getBeschreibung()));
        HorizontalLayout yes_no_buttons = new HorizontalLayout();
        Button buttonYes = new Button("YES", (e) -> {
            handleInput(Entscheidung.JA, thema);
            dialog.close();
        });
        buttonYes.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_SUCCESS);
        Button buttonNo = new Button("NO", (e) -> {
            handleInput(Entscheidung.NEIN, thema);
            dialog.close();
        });
        buttonNo.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);

        yes_no_buttons.add(buttonYes, buttonNo);

        Button cancelButton = new Button("Abbrechen", (e) -> {
            dialog.close();
        });
        dialog.add(yes_no_buttons);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        return dialog;
    }

    private void handleInput(
            Entscheidung entscheidung,
            Abstimmungsthema abstimmungsthema
    ) {
        LocalDate localDate = LocalDate.now();
        if (entscheidung.equals(Entscheidung.JA)) {
            try {
                votingService.handleVote(entscheidung, localDate, securityService.getAuthenticatedUser(), abstimmungsthema);
                showVoteNotification(Notification.Position.TOP_END, "Abstimmung erfolgreich abgegeben", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception e) {
                logger.error("Fehler beim Ja-Abstimmen: " + e.getMessage());
            }
        } else {
            try {
                votingService.handleVote(entscheidung, localDate, securityService.getAuthenticatedUser(), abstimmungsthema);
                showVoteNotification(Notification.Position.TOP_END, "Abstimmung erfolgreich abgegeben", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception e) {
                logger.error("Fehler beim Nein-Abstimmen: " + e.getMessage());
            }
        }
        list.getDataProvider().refreshItem(abstimmungsthema);
    }

    public int getYesCount(Abstimmungsthema abstimmungsthema) {
        int yesVoteCount = 0;
        for (Stimmzettel stimmzettel : abstimmungsthema.getStimmzettelList()) {
            if (stimmzettel.getEntscheidung() == Entscheidung.JA) {
                yesVoteCount++;
            }
        }
        return yesVoteCount;
    }

    public int getNoCount(Abstimmungsthema abstimmungsthema) {
        int yesVoteCount = 0;
        for (Stimmzettel stimmzettel : abstimmungsthema.getStimmzettelList()) {
            if (stimmzettel.getEntscheidung() == Entscheidung.NEIN) {
                yesVoteCount++;
            }
        }
        return yesVoteCount;
    }

    public boolean ableToVote(BreakDeciderUser user, Abstimmungsthema abstimmungsthema) {
        if (user.getAbstimmungsthema() != null) {
            if (user.getAbstimmungsthema().getStimmzettelList() != null) {
                for (Stimmzettel stimmzettel : user.getAbstimmungsthema().getStimmzettelList()) {
                    if (stimmzettel.getAbstimmungsthema().equals(abstimmungsthema)) {
                        return false;
                    }
                }
            } else {
                throw new NullPointerException("User hat kein Stimmzettel");
            }
        } else {
            throw new NullPointerException("User hat kein Abstimmungsthema");
        }
        return true;
    }

    private boolean enableButtons(Abstimmungsthema abstimmungsthema) {
        if (abstimmungsthema.getStatus() == Status.CLOSED) {
            return false;
        }
        return true;
    }
}
