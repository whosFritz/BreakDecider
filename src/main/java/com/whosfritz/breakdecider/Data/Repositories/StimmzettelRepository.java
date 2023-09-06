package com.whosfritz.breakdecider.Data.Repositories;

import com.whosfritz.breakdecider.Data.Entities.Stimmzettel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StimmzettelRepository extends JpaRepository<Stimmzettel, Long> {
    // @Transactional
    List<Stimmzettel> findAllByBreakDeciderUser_Id(Long userId);
    // Sie können hier benutzerdefinierte Abfrage-Methoden hinzufügen, falls erforderlich
}
