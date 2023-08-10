package com.whosfritz.breakdecider.Repositories;

import com.whosfritz.breakdecider.Entities.Stimmzettel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StimmzettelRepository extends JpaRepository<Stimmzettel, Long> {
    // Sie können hier benutzerdefinierte Abfrage-Methoden hinzufügen, falls erforderlich
}
