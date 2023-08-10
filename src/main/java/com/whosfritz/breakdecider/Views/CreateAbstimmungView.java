package com.whosfritz.breakdecider.Views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;


@PermitAll
@Route(value = "abstimmungErstellen", layout = MainView.class)
@PageTitle("Abstimmung erstellen")
public class CreateAbstimmungView extends VerticalLayout {
    public CreateAbstimmungView() {
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
