package com.whosfritz.breakdecider.Data.Services;

import com.whosfritz.breakdecider.Data.Entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class VotingService {
    private final AbstimmungsthemaService abstimmungsthemaService;
    private final Logger logger = LoggerFactory.getLogger(VotingService.class);

    public VotingService(AbstimmungsthemaService abstimmungsthemaService) {
        this.abstimmungsthemaService = abstimmungsthemaService;
    }

    @Transactional
    public void handleVote(Entscheidung entscheidung,
                           LocalDate localDate,
                           BreakDeciderUser authenticatedUser,
                           Abstimmungsthema abstimmungsthema
    ) {
        try {
            Stimmzettel neuerStimmzettel = new Stimmzettel(entscheidung, localDate, authenticatedUser, abstimmungsthema);
            abstimmungsthema.getStimmzettelSet().add(neuerStimmzettel);
            abstimmungsthemaService.saveAbstimmungsthema(abstimmungsthema);
        } catch (Exception e) {
            throw e;
        }
    }


    @Transactional
    public void handleCreateAbstimmung(BreakDeciderUser ersteller, LocalDate localDate, Status status, String titel, String beschreibung) {
        try {
            Abstimmungsthema abstimmungsthema = new Abstimmungsthema();
            abstimmungsthema.setErsteller(ersteller.getUsername());
            abstimmungsthema.setErstelldatum(localDate);
            abstimmungsthema.setStatus(status);
            abstimmungsthema.setTitel(titel);
            abstimmungsthema.setBeschreibung(beschreibung);
            abstimmungsthemaService.saveAbstimmungsthema(abstimmungsthema);
        } catch (Exception e) {
            logger.error("Fehler beim Erstellen der Abstimmung: " + e.getMessage());
            throw e;
        }
    }
}
