package com.whosfritz.breakdecider.Data.Services;

import com.whosfritz.breakdecider.Data.Entities.*;
import com.whosfritz.breakdecider.Exception.SameVoteAgainException;
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
        Stimmzettel neuerStimmzettel = checkIfAlreadyVoted(authenticatedUser, abstimmungsthema);
        if (neuerStimmzettel == null) {
            neuerStimmzettel = new Stimmzettel();
            neuerStimmzettel.setBreakDeciderUser(authenticatedUser);
            neuerStimmzettel.setAbstimmungsthema(abstimmungsthema);
            abstimmungsthema.getStimmzettelSet().add(neuerStimmzettel);
        }
        if (neuerStimmzettel.getEntscheidung() != entscheidung) {
            neuerStimmzettel.setEntscheidung(entscheidung);
            neuerStimmzettel.setStimmabgabedatum(localDateTime);
            abstimmungsthemaService.saveAbstimmungsthema(abstimmungsthema);
        } else {
            throw new SameVoteAgainException("Benutzer " + authenticatedUser.getUsername() + " hat bereits für " + entscheidung + " abgestimmt.");
        }
    }

    private Stimmzettel checkIfAlreadyVoted(BreakDeciderUser authenticatedUser, Abstimmungsthema abstimmungsthema) {
        for (Stimmzettel stimmzettel : abstimmungsthema.getStimmzettelSet()) {
            if (stimmzettel.getBreakDeciderUser().getUsername().equals(authenticatedUser.getUsername())) {
                return stimmzettel;
            }
        }
        return null;
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
