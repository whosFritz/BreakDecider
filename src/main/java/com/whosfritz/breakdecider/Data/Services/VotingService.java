package com.whosfritz.breakdecider.Data.Services;

import com.whosfritz.breakdecider.Data.Entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VotingService {
    private final AbstimmungsthemaService abstimmungsthemaService;
    private final Logger logger = LoggerFactory.getLogger(VotingService.class);

    public VotingService(AbstimmungsthemaService abstimmungsthemaService) {
        this.abstimmungsthemaService = abstimmungsthemaService;
    }


    public void handleVote(Entscheidung entscheidung,
                           LocalDateTime localDateTime,
                           BreakDeciderUser authenticatedUser,
                           Abstimmungsthema abstimmungsthema
    ) {
        checkIfAlreadyVoted(authenticatedUser, abstimmungsthema);
        Stimmzettel neuerStimmzettel = new Stimmzettel();
        neuerStimmzettel.setEntscheidung(entscheidung);
        neuerStimmzettel.setStimmabgabedatum(localDateTime);
        neuerStimmzettel.setBreakDeciderUser(authenticatedUser);
        neuerStimmzettel.setAbstimmungsthema(abstimmungsthema);

        abstimmungsthema.getStimmzettelSet().add(neuerStimmzettel);
        abstimmungsthemaService.saveAbstimmungsthema(abstimmungsthema);
    }


    public void checkIfAlreadyVoted(BreakDeciderUser authenticatedUser, Abstimmungsthema abstimmungsthema) {
        for (Stimmzettel stimmzettel : abstimmungsthema.getStimmzettelSet()) {
            if (stimmzettel.getBreakDeciderUser().getUsername().equals(authenticatedUser.getUsername())) {
                throw new IllegalStateException("Benutzer " + authenticatedUser.getUsername() + " hat bereits abgestimmt");
            }
        }
    }


    public void handleCreateAbstimmung(BreakDeciderUser ersteller, LocalDateTime localDateTime, Status status, String titel, String beschreibung) {
        Abstimmungsthema abstimmungsthema = new Abstimmungsthema();
        abstimmungsthema.setErsteller(ersteller.getUsername());
        abstimmungsthema.setErstelldatum(localDateTime);
        abstimmungsthema.setStatus(status);
        abstimmungsthema.setTitel(titel);
        abstimmungsthema.setBeschreibung(beschreibung);
        abstimmungsthemaService.saveAbstimmungsthema(abstimmungsthema);
    }
}
