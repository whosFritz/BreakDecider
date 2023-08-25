package com.whosfritz.breakdecider.Data.Services;

import com.whosfritz.breakdecider.Data.Entities.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VotingService {
    private final AbstimmungsthemaService abstimmungsthemaService;
    private final StimmzettelService stimmzettelService;

    public VotingService(AbstimmungsthemaService abstimmungsthemaService, StimmzettelService stimmzettelService) {
        this.abstimmungsthemaService = abstimmungsthemaService;
        this.stimmzettelService = stimmzettelService;
    }


    public String handleVote(Entscheidung entscheidung,
                             LocalDateTime localDateTime,
                             BreakDeciderUser authenticatedUser,
                             Abstimmungsthema abstimmungsthema
    ) {
        Stimmzettel neuerStimmzettel = getOldStimmzettel(authenticatedUser, abstimmungsthema);

        if (neuerStimmzettel != null && neuerStimmzettel.getEntscheidung().equals(entscheidung)) {
            abstimmungsthema.getStimmzettelSet().remove(getOldStimmzettel(authenticatedUser, abstimmungsthema));
            stimmzettelService.deleteStimmzettelById(neuerStimmzettel.getId());
            abstimmungsthemaService.saveAbstimmungsthema(abstimmungsthema);
            return "Abstimmung wurde zurückgezogen";
        }

        if (neuerStimmzettel == null) {
            neuerStimmzettel = new Stimmzettel();
            neuerStimmzettel.setBreakDeciderUser(authenticatedUser);
            neuerStimmzettel.setAbstimmungsthema(abstimmungsthema);
            abstimmungsthema.getStimmzettelSet().add(neuerStimmzettel);
        }
        neuerStimmzettel.setEntscheidung(entscheidung);
        neuerStimmzettel.setStimmabgabedatum(localDateTime);
        abstimmungsthemaService.saveAbstimmungsthema(abstimmungsthema);
        return "Abstimmung wurde abgegeben";
    }

    private Stimmzettel getOldStimmzettel(BreakDeciderUser authenticatedUser, Abstimmungsthema abstimmungsthema) {
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
