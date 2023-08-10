package com.whosfritz.breakdecider.Views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.whosfritz.breakdecider.Components.ComponentAbstimmungsListe;
import com.whosfritz.breakdecider.Entities.Abstimmungsthema;
import com.whosfritz.breakdecider.Services.AbstimmungsthemaService;
import jakarta.annotation.security.PermitAll;

import java.util.ArrayList;

@PermitAll
@Route(value = "", layout = MainView.class)
@PageTitle("Abstimmungen")
public class AbstimmungenView extends VerticalLayout {

    public AbstimmungenView(AbstimmungsthemaService abstimmungsthemaService) {
        ArrayList<Abstimmungsthema> abstimmungsthemaArrayList = new ArrayList<>();
        abstimmungsthemaArrayList.add(new Abstimmungsthema("John Doe", "2023-08-07", "Open", "Test Topic 1", "This is a test description for topic 1.", 32, 21));
        abstimmungsthemaArrayList.add(new Abstimmungsthema("Jane Smith", "2023-08-23", "Closed", "Test Topic 2", "This is a test description for topic 2.", 32, 323));
        abstimmungsthemaArrayList.add(new Abstimmungsthema("Alice Johnson", "2023-08-12", "Open", "Test Topic 3", "This is a test description for topic 3.", 33, 11));
        abstimmungsthemaArrayList.add(new Abstimmungsthema("Bob Brown", "2023-08-01", "Closed", "Test Topic 4", "This is a test description for topic 4.", 32, 21));
        Button testButton = new Button("Test Button", event -> {
            abstimmungsthemaService.saveAllAbstimmungsthemen(abstimmungsthemaArrayList);
        });
        add(testButton);

//       add(grid);

        // Create an instance of the VirtualListBasic component
        ComponentAbstimmungsListe componentAbstimmungsListe = new ComponentAbstimmungsListe(abstimmungsthemaService.getAbstimmungsthemaRepository());

        // Add the VirtualListBasic component to the layout
        add(componentAbstimmungsListe);
    }


}
