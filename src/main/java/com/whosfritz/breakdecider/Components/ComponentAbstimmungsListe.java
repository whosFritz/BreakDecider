package com.whosfritz.breakdecider.Components;

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
import com.whosfritz.breakdecider.Entities.Abstimmungsthema;
import com.whosfritz.breakdecider.Repositories.AbstimmungsthemaRepository;
import com.whosfritz.breakdecider.Services.AbstimmungsthemaService;

import java.util.List;


public class ComponentAbstimmungsListe extends Div {


    public ComponentAbstimmungsListe(AbstimmungsthemaRepository abstimmungsthemaRepository) {
        AbstimmungsthemaService abstimmungsthemaService = new AbstimmungsthemaService(abstimmungsthemaRepository); // Create an instance of the service using the repository
        List<Abstimmungsthema> abstimmungsthemen = abstimmungsthemaService.getAllAbstimmungsthemen(); // Get the list of Abstimmungsthemen from the service

        add(buildListe(abstimmungsthemen, abstimmungsthemaService));
    }

    private VerticalLayout buildListe(List<Abstimmungsthema> abstimmungsthemen, AbstimmungsthemaService abstimmungsthemaService) {
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
                    .add(new Div(new Text(abstimmungsthema.getErstelldatum())));
            infoLayout
                    .add(new Details("Details", contactLayout));

            Dialog dialog = new Dialog();
            dialog.setDraggable(true);
            dialog.setHeaderTitle("Abstimmen über Thema: " + abstimmungsthema.getTitel());
            HorizontalLayout yes_no_buttons = new HorizontalLayout();
            Button yesButton = new Button("YES", (e) -> {
                applicateVote(true, abstimmungsthemaService);
                dialog.close();
            });
            yesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                    ButtonVariant.LUMO_SUCCESS);
            Button noButton = new Button("NO", (e) -> {
                applicateVote(true, abstimmungsthemaService);
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
        return list;
    }

    private void applicateVote(boolean entscheidung, AbstimmungsthemaService abstimmungsthemaService) {
        if (entscheidung) {
            System.out.println("yes");
        } else {
            System.out.println("no");
        }
    }
}
