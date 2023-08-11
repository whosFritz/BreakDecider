package com.whosfritz.breakdecider.Registration;

import com.whosfritz.breakdecider.Data.Entities.AppUserRole;
import com.whosfritz.breakdecider.Data.Entities.BreakDeciderUser;
import com.whosfritz.breakdecider.Data.Services.BreakDeciderUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BreakDeciderUserService breakDeciderUserService;
    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    public RegistrationService(BCryptPasswordEncoder bCryptPasswordEncoder, BreakDeciderUserService breakDeciderUserService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.breakDeciderUserService = breakDeciderUserService;
    }

    public void register(RegistrationRequest request) {
        if (breakDeciderUserService.userExists(request.getUsername())) {
            logger.error("Username already exists");
            throw new IllegalStateException("Username already exists");
        }
        BreakDeciderUser breakDeciderUser = new BreakDeciderUser(
                request.getUsername(),
                bCryptPasswordEncoder.encode(request.getPassword()),
                AppUserRole.USER,
                false,
                true);
        breakDeciderUserService.save(breakDeciderUser);
    }
}
