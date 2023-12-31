package com.whosfritz.breakdecider.ui.Views;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Route("login")
@PageTitle("Login | BreakDecider")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterListener {
    private final LoginForm loginForm = new LoginForm();
    private final Logger logger = LoggerFactory.getLogger(LoginView.class);


    public LoginView(@Value("${info.app.developer.name}") String adminName) {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);


        LoginI18n i18n = LoginI18n.createDefault();

        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle("Melde dich an");
        i18nForm.setUsername("Dein Benutzername");
        i18nForm.setPassword("Dein Passwort");
        i18nForm.setSubmit("Anmelden");
        i18nForm.setForgotPassword("Passwort vergessen?");
        i18n.setForm(i18nForm);
        loginForm.setEnabled(false);
        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle("Fehler");
        i18nErrorMessage.setMessage(
                "Fehler text");
        i18n.setErrorMessage(i18nErrorMessage);

        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(true);
        loginForm.addForgotPasswordListener(event -> Notification.show("Wenden Sie sich dafür bitte an " + adminName + ".❤️"));
        loginForm.addLoginListener(event -> logger.info("Benutzer versuchte sich einzuloggen mit Benutzername: " + event.getUsername()));
        loginForm.setI18n(i18n);
        Image image = new Image("images/img-login.png", "LOGO");

        Checkbox checkbox = new Checkbox();
        // link inside the label
        checkbox.setLabel("Ich habe die Datenschutzerklärung gelesen und akzeptiere diese.");
        checkbox.addValueChangeListener(event -> {
            if (event.getValue()) {
                loginForm.setEnabled(true);
            } else {
                loginForm.setEnabled(false);
            }
        });
        Anchor datenschutz = new Anchor("https://github.com/whosFritz/BreakDecider/blob/main/datenschutzerklaerung.md", "Datenschutzerklärung");
        datenschutz.setTarget("_blank");
        Anchor impressum = new Anchor("https://github.com/whosFritz/BreakDecider/blob/main/impressum.md", "Impressum");
        impressum.setTarget("_blank");

        add(
                image,
                loginForm,
                checkbox,
                datenschutz,
                impressum
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent
                .getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
