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

    public AbstimmungsthemaService(AbstimmungsthemaRepository abstimmungsthemaRepository) {
        this.abstimmungsthemaRepository = abstimmungsthemaRepository;
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


    // Weitere benutzerdefinierte Methoden für die Geschäftslogik
}
