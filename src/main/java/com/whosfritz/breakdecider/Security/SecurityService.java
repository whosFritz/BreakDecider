package com.whosfritz.breakdecider.Security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import com.whosfritz.breakdecider.Data.Entities.BreakDeciderUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {

    private static final String LOGOUT_SUCCESS_URL = "/";
    private final Logger logger = LoggerFactory.getLogger(SecurityService.class);

    public BreakDeciderUser getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (BreakDeciderUser) context.getAuthentication().getPrincipal();
        }
        logger.error("Benutzer konnte nicht ermittelt werden");
        throw new RuntimeException("Benutzer konnte nicht ermittelt werden");
    }

    public void logout() {
        try {
            logger.info("Benutzer wurde ausgeloggt: " + getAuthenticatedUser().getUsername());
            UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(
                    VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                    null);
        } catch (Exception e) {
            logger.error("Benutzer konnte nicht ausgeloggt werden: " + getAuthenticatedUser().getUsername());
        }

    }

    public boolean isUserLoggedIn() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }
}
