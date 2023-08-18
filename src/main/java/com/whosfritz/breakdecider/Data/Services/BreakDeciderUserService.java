package com.whosfritz.breakdecider.Data.Services;

import com.whosfritz.breakdecider.Data.Entities.Abstimmungsthema;
import com.whosfritz.breakdecider.Data.Entities.BreakDeciderUser;
import com.whosfritz.breakdecider.Data.Entities.Stimmzettel;
import com.whosfritz.breakdecider.Data.Repositories.BreakDeciderUserRepository;
import com.whosfritz.breakdecider.Exception.NewEqualsOldPasswordException;
import com.whosfritz.breakdecider.Exception.PasswordIncorrectException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class BreakDeciderUserService implements UserDetailsService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final BreakDeciderUserRepository breakDeciderUserRepository;
    private final StimmzettelService stimmzettelService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return breakDeciderUserRepository.findByUsername(username).orElseThrow(()
                -> new UsernameNotFoundException("Benutzer " + username + " not found"));
    }


    @Transactional
    public void deleteUserWithStimmzettel(Long userId) {
        try {
            List<Stimmzettel> stimmzettelList = stimmzettelService.getAllStimmzettelByUserId(userId);

            for (Stimmzettel stimmzettel : stimmzettelList) {
                // Remove from Abstimmungsthema
                Abstimmungsthema abstimmungsthema = stimmzettel.getAbstimmungsthema();
                if (abstimmungsthema != null) {
                    abstimmungsthema.getStimmzettelSet().remove(stimmzettel);
                } else {
                    throw new NullPointerException("Stimmzettel " + stimmzettel.getId() + " hat kein Abstimmungsthema");
                }

                // Delete Stimmzettel by ID
                stimmzettelService.deleteStimmzettelById(stimmzettel.getId());
            }

            // After deleting associated Stimmzettel, delete the user
            breakDeciderUserRepository.deleteById(userId);
        } catch (NullPointerException nullPointerException) {
            throw nullPointerException;
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }


    public boolean userExists(String username) {
        return breakDeciderUserRepository.existsByUsername(username);
    }


    public List<BreakDeciderUser> getAllUsers() {
        return breakDeciderUserRepository.findAll();
    }


    public void save(BreakDeciderUser breakDeciderUser) {
        breakDeciderUserRepository.save(breakDeciderUser);
    }


    // function to get user by username

    public BreakDeciderUser getUserByUsername(String username) {
        return breakDeciderUserRepository.findByUsername(username).orElseThrow(()
                -> new UsernameNotFoundException("Benutzer " + username + " not found"));
    }


    public void updateUser(BreakDeciderUser breakDeciderUser, String oldPasswordString, String newPasswordString) {
        if (!bCryptPasswordEncoder.matches(oldPasswordString, breakDeciderUser.getPassword())) {
            throw new PasswordIncorrectException("Das alte Passwort ist nicht korrekt.");
        }
        try {
            breakDeciderUser = getUserByUsername(breakDeciderUser.getUsername());
        } catch (UsernameNotFoundException usernameNotFoundException) {
            throw new UsernameNotFoundException("Benutzer konnte nicht ermittelt werden");
        }
        if (bCryptPasswordEncoder.matches(newPasswordString, breakDeciderUser.getPassword())) {
            throw new NewEqualsOldPasswordException("Das neue Passwort darf nicht dem alten Passwort entsprechen.");
        }
        breakDeciderUser.setPassword(bCryptPasswordEncoder.encode(newPasswordString));
        save(breakDeciderUser);
    }

}
