package com.whosfritz.breakdecider.Registration;

import com.whosfritz.breakdecider.Entities.AppUserRole;
import com.whosfritz.breakdecider.Entities.BreakDeciderUser;
import com.whosfritz.breakdecider.Repositories.BreakDeciderUserRepository;
import com.whosfritz.breakdecider.Services.BreakDeciderUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BreakDeciderUserRepository breakDeciderUserRepository;
    private final BreakDeciderUserService breakDeciderUserService;

    public void register(RegistrationRequest request) {
        if (breakDeciderUserService.userExists(request.getUsername())) {
            throw new IllegalStateException("Username already exists");
        }
        BreakDeciderUser breakDeciderUser = new BreakDeciderUser(
                request.getUsername(),
                bCryptPasswordEncoder.encode(request.getPassword()),
                AppUserRole.USER,
                false,
                true);
        breakDeciderUserRepository.save(breakDeciderUser);
    }
}
