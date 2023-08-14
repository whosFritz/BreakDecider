package com.whosfritz.breakdecider.Data.Services;

import com.whosfritz.breakdecider.Data.Entities.BreakDeciderUser;
import com.whosfritz.breakdecider.Data.Repositories.BreakDeciderUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class BreakDeciderUserService implements UserDetailsService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final BreakDeciderUserRepository breakDeciderUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return breakDeciderUserRepository.findByUsername(username).orElseThrow(()
                -> new UsernameNotFoundException("User " + username + " not found"));
    }

    @Transactional
    public boolean userExists(String username) {
        return breakDeciderUserRepository.existsByUsername(username);
    }


    @Transactional
    public void save(BreakDeciderUser breakDeciderUser) {
        breakDeciderUserRepository.save(breakDeciderUser);
    }

    public void updateUser(BreakDeciderUser breakDeciderUser, String oldPassword, String newPassword) {
        if (!bCryptPasswordEncoder.matches(oldPassword, breakDeciderUser.getPassword())) {
            throw new IllegalArgumentException("Das alte Passwort ist nicht korrekt.");
        }
        if ()

            breakDeciderUser.
                    breakDeciderUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
        breakDeciderUserRepository.save(breakDeciderUser);
    }
}
