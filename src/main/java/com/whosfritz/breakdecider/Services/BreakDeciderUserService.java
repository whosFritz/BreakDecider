package com.whosfritz.breakdecider.Services;

import com.whosfritz.breakdecider.Repositories.BreakDeciderUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BreakDeciderUserService implements UserDetailsService {

    private final BreakDeciderUserRepository breakDeciderUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return breakDeciderUserRepository.findByUsername(username).orElseThrow(()
                -> new UsernameNotFoundException("User " + username + " not found"));
    }

    public boolean userExists(String username) {
        return breakDeciderUserRepository.existsByUsername(username);
    }
}
