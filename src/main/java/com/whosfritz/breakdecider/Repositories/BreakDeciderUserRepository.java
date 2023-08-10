package com.whosfritz.breakdecider.Repositories;

import com.whosfritz.breakdecider.Entities.BreakDeciderUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BreakDeciderUserRepository extends JpaRepository<BreakDeciderUser, Long> {
    // Sie können hier benutzerdefinierte Abfrage-Methoden hinzufügen, falls erforderlich
    Optional<BreakDeciderUser> findByUsername(String username);

    boolean existsByUsername(String username);

}
