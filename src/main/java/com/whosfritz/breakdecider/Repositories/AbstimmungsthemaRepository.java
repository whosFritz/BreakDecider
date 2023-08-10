package com.whosfritz.breakdecider.Repositories;

import com.whosfritz.breakdecider.Entities.Abstimmungsthema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbstimmungsthemaRepository extends JpaRepository<Abstimmungsthema, Long> {
    // Sie können hier benutzerdefinierte Abfrage-Methoden hinzufügen, falls erforderlich
}
