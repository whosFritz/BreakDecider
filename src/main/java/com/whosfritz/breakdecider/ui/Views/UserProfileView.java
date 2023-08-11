package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Route(value = "profile", layout = MainView.class)
@PageTitle("Abstimmung erstellen")
public class UserProfileView extends VerticalLayout {

    //Constructor vaadin
    public UserProfileView() {
        addClassName("user-profile-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

}
