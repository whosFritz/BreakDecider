package com.whosfritz.breakdecider.Registration;

import com.whosfritz.breakdecider.Exception.InvalidTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;


@RestController
@RequestMapping(path = "/api/v1/registration")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {
        try {
            registrationService.register(request);
        } catch (IllegalStateException e) {
            logger.error("Benutzername bereits vergeben");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (HttpServerErrorException.InternalServerError e) {
            logger.error("Ein HttpServerErrorException.InternalServerError trat auf beim Registrieren eines Benutzers");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            logger.error("Ein DataIntegrityViolationException trat auf beim Registrieren eines Benutzers");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (InvalidTokenException e) {
            logger.error("Ein InvalidTokenException trat auf beim Registrieren eines Benutzers");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Ein Fehler trat auf beim Registrieren eines Benutzers");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        logger.info("Der folgende Benutzer: " + request.getUsername() + "wurde erfolgreich registriert");
        return ResponseEntity.status(HttpStatus.CREATED).body("Benutzer " + request.getUsername() + " wurde erfolgreich registriert");
    }

}
