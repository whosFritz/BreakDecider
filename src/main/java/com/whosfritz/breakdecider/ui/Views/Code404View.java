package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * Code404View
 * <p>
 * View for 404 error
 * <p>
 * This view is used to display a 404 error.
 * It is accessed when a user tries to access a page that does not exist.
 * </p>
 */
@PageTitle("404 | BreakDecider")
@ParentLayout(MainView.class)
@Component
public class Code404View
        extends RouteNotFoundError {

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
                                 ErrorParameter<NotFoundException> parameter) {

        Div errorDiv = new Div();
        errorDiv.getStyle().set("display", "flex");
        errorDiv.getStyle().set("justify-content", "center");
        errorDiv.getStyle().set("align-items", "center");
        errorDiv.getStyle().set("height", "100%");

        StreamResource logoStream = new StreamResource("404.png", () -> getClass().getResourceAsStream("/static/img/404-page-not-found.512x249.png"));
        Image errorImage = new Image(logoStream, "404.png");
        errorDiv.add(errorImage);

        getElement().appendChild(errorDiv.getElement());

        return HttpServletResponse.SC_NOT_FOUND;
    }

}
