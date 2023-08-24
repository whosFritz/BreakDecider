package com.whosfritz.breakdecider.Data.Services;

import com.whosfritz.breakdecider.Data.Entities.Abstimmungsthema;
import com.whosfritz.breakdecider.Data.Entities.Status;
import com.whosfritz.breakdecider.Data.Repositories.AbstimmungsthemaRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
public class AbstimmungsthemaService {
    private final AbstimmungsthemaRepository abstimmungsthemaRepository;
    private final StimmzettelService stimmzettelservice;

    public AbstimmungsthemaService(AbstimmungsthemaRepository abstimmungsthemaRepository, StimmzettelService stimmzettelservice) {
        this.abstimmungsthemaRepository = abstimmungsthemaRepository;
        this.stimmzettelservice = stimmzettelservice;
    }


    public List<Abstimmungsthema> getAllAbstimmungsthemen() {
        return abstimmungsthemaRepository.findAll();
    }


    public Abstimmungsthema saveAbstimmungsthema(Abstimmungsthema abstimmungsthema) {
        return abstimmungsthemaRepository.save(abstimmungsthema);
    }

    public void openAbstimmungsthema(Abstimmungsthema abstimmungsthema) {
        abstimmungsthema.setStatus(Status.OPEN);
        saveAbstimmungsthema(abstimmungsthema);
    }

    public void closeAbstimmungsthema(Abstimmungsthema abstimmungsthema) {
        abstimmungsthema.setStatus(Status.CLOSED);
        saveAbstimmungsthema(abstimmungsthema);
    }

    public void deleteAbstimmungsthema(Abstimmungsthema abstimmungsthema) {
        abstimmungsthema.getStimmzettelSet().forEach(stimmzettel -> {
            stimmzettel.setAbstimmungsthema(null);
            stimmzettelservice.deleteStimmzettelById(stimmzettel.getId());
        });
        abstimmungsthemaRepository.delete(abstimmungsthema);
    }


    // Weitere benutzerdefinierte Methoden für die Geschäftslogik
}
