package com.whosfritz.breakdecider.Security;

import com.whosfritz.breakdecider.Data.Entities.BreakDeciderUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;


@Component()
public class AuthSuccessHandler implements ApplicationListener<AuthenticationSuccessEvent> {

    private final Logger logger = LoggerFactory.getLogger(AuthSuccessHandler.class);

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        BreakDeciderUser logged_in_user = (BreakDeciderUser) event.getAuthentication().getPrincipal();
        logger.info("Benutzer wurde eingeloggt: " + logged_in_user.getUsername());
    }
}