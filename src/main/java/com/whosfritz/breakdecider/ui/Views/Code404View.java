package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.whosfritz.breakdecider.Security.SecurityService;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Code404View
 * <p>
 * View for 404 error
 * <p>
 * This view is used to display a 404 error.
 * It is accessed when a user tries to access a page that does not exist.
 * </p>
 */
@ParentLayout(MainView.class)
public class Code404View
        extends RouteNotFoundError {
    private final SecurityService securityService;

    public Code404View(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
                                 ErrorParameter<NotFoundException> parameter) {

        Div errorDiv = new Div();
        errorDiv.getStyle().set("display", "flex");
        errorDiv.getStyle().set("justify-content", "center");
        errorDiv.getStyle().set("align-items", "center");


        StreamResource logoStream = new StreamResource("404.gif", () -> getClass().getResourceAsStream("/static/gifs/404.gif"));
        Image errorImage = new Image(logoStream, "404.gif");
        errorDiv.add(errorImage);

        getElement().appendChild(errorDiv.getElement());

        return HttpServletResponse.SC_NOT_FOUND;
    }
}
