package com.whosfritz.breakdecider.Data.Repositories;

import com.whosfritz.breakdecider.Data.Entities.BreakDeciderUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface BreakDeciderUserRepository extends JpaRepository<BreakDeciderUser, Long> {
    // Sie können hier benutzerdefinierte Abfrage-Methoden hinzufügen, falls erforderlich

    @Transactional
    Optional<BreakDeciderUser> findByUsername(String username);


    @Transactional
    boolean existsByUsername(String username);

}
