package com.whosfritz.breakdecider.Registration;

import com.whosfritz.breakdecider.Data.Entities.AppUserRole;
import com.whosfritz.breakdecider.Exception.InvalidTokenException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import static com.whosfritz.breakdecider.Registration.SecretRegistrationToken.REGISTRATION_TOKEN;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class RegistrationControllerTest {

    private RegistrationController registrationController;
    private RegistrationService registrationService;

    @AfterAll
    public static void allTestsPassed() {        // highlight "Test passed" in green
        // highlight "Test passed" in green
        System.out.println("\u001B[32mAll tests passed\u001B[0m");
    }

    @BeforeEach
    public void setup() {
        registrationService = mock(RegistrationService.class);
        registrationController = new RegistrationController(registrationService);
    }

    @Test
    public void testSuccessfulRegistration() {
        RegistrationRequest request = new RegistrationRequest("username", "password", AppUserRole.ROLE_USER, REGISTRATION_TOKEN);

        ResponseEntity<String> response = registrationController.register(request);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("Benutzer username wurde erfolgreich registriert", response.getBody());
        System.out.println("\u001B[32mTest testSuccessfulRegistration passed\u001B[0m");

        // Assuming registration was successful
        // Add appropriate assertions here based on your requirements
    }

    @Test
    public void testInvalidTokenRegistration() {
        RegistrationRequest request = new RegistrationRequest("username", "password", AppUserRole.ROLE_USER, "invalid_token");
        doThrow(InvalidTokenException.class).when(registrationService).register(request);

        ResponseEntity<String> response = registrationController.register(request);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // highlight "Test passed" in green
        System.out.println("\u001B[32mTest testInvalidTokenRegistration passed\u001B[0m");

        // Assert that the response status is HttpStatus.BAD_REQUEST
        // Add other relevant assertions based on your requirements
    }

    @Test
    public void testConflictRegistration() {
        RegistrationRequest request = new RegistrationRequest("existing_username", "password", AppUserRole.ROLE_USER, REGISTRATION_TOKEN);
        doThrow(IllegalStateException.class).when(registrationService).register(request);

        ResponseEntity<String> response = registrationController.register(request);
        Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        // highlight "Test passed" in green
        System.out.println("\u001B[32mTest testConflictRegistration passed\u001B[0m");

        // Assert that the response status is HttpStatus.CONFLICT
        // Add other relevant assertions based on your requirements
    }

    @Test
    public void testInternalServerErrorRegistration() {
        RegistrationRequest request = new RegistrationRequest("username", "password", AppUserRole.ROLE_USER, REGISTRATION_TOKEN);
        // Assuming registrationService throws HttpServerErrorException.InternalServerError
        doThrow(HttpServerErrorException.InternalServerError.class).when(registrationService).register(request);

        ResponseEntity<String> response = registrationController.register(request);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        // highlight "Test passed" in green
        System.out.println("\u001B[32mTest testInternalServerErrorRegistration passed\u001B[0m");

        // Assert that the response status is HttpStatus.INTERNAL_SERVER_ERROR
        // Add other relevant assertions based on your requirements
    }
}
