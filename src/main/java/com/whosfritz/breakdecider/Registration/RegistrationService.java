package com.whosfritz.breakdecider.Registration;

import com.whosfritz.breakdecider.Data.Entities.BreakDeciderUser;
import com.whosfritz.breakdecider.Data.Services.BreakDeciderUserService;
import com.whosfritz.breakdecider.Exception.InvalidTokenException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.whosfritz.breakdecider.Registration.SecretRegistrationToken.REGISTRATION_TOKEN;

@Service
public class RegistrationService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BreakDeciderUserService breakDeciderUserService;

    public RegistrationService(BCryptPasswordEncoder bCryptPasswordEncoder, BreakDeciderUserService breakDeciderUserService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.breakDeciderUserService = breakDeciderUserService;
    }

    public BreakDeciderUser register(RegistrationRequest request) {
        if (request.getSecret_token() == null)
            throw new InvalidTokenException("Token ist ungültig");
        if (!request.getSecret_token().equals(REGISTRATION_TOKEN)) {
            throw new InvalidTokenException("Token ist ungültig");
        }
        if (breakDeciderUserService.userExists(request.getUsername())) {
            throw new IllegalStateException("Benutzername schon vergeben");
        }
        BreakDeciderUser breakDeciderUser = new BreakDeciderUser();
        breakDeciderUser.setUsername(request.getUsername());
        breakDeciderUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        breakDeciderUser.setAppUserRole(request.getAppUserRole());
        breakDeciderUser.setLocked(false);
        breakDeciderUser.setEnabled(true);
        return breakDeciderUserService.save(breakDeciderUser);
    }
}
