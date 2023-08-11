package com.whosfritz.breakdecider.Data.Services;

import com.whosfritz.breakdecider.Data.Entities.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class VotingService {
    private final AbstimmungsthemaService abstimmungsthemaService;


    @Transactional
    public void handleVote(Entscheidung entscheidung,
                           LocalDate localDate, BreakDeciderUser authenticatedUser, Abstimmungsthema abstimmungsthema) {
        Stimmzettel neuerStimmzettel = new Stimmzettel(entscheidung, localDate, authenticatedUser, abstimmungsthema);
        abstimmungsthema.getStimmzettelList().add(neuerStimmzettel);
        abstimmungsthemaService.saveAbstimmungsthema(abstimmungsthema);
    }


    @Transactional
    public void handleCreateAbstimmung(BreakDeciderUser ersteller, LocalDate localDate, Status status, String titel, String beschreibung) {
        Abstimmungsthema abstimmungsthema = new Abstimmungsthema();
        abstimmungsthema.setErsteller(ersteller.getUsername());
        abstimmungsthema.setErstelldatum(localDate);
        abstimmungsthema.setStatus(status);
        abstimmungsthema.setTitel(titel);
        abstimmungsthema.setBeschreibung(beschreibung);
        abstimmungsthemaService.saveAbstimmungsthema(abstimmungsthema);
    }
}
