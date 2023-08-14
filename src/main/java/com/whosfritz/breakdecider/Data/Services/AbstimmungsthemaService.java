package com.whosfritz.breakdecider.Data.Services;

import com.whosfritz.breakdecider.Data.Entities.Abstimmungsthema;
import com.whosfritz.breakdecider.Data.Repositories.AbstimmungsthemaRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Getter
public class AbstimmungsthemaService {
    private final AbstimmungsthemaRepository abstimmungsthemaRepository;

    public AbstimmungsthemaService(AbstimmungsthemaRepository abstimmungsthemaRepository) {
        this.abstimmungsthemaRepository = abstimmungsthemaRepository;
    }

    @Transactional
    public List<Abstimmungsthema> getAllAbstimmungsthemen() {
        return abstimmungsthemaRepository.findAll();
    }


    @Transactional
    public Abstimmungsthema saveAbstimmungsthema(Abstimmungsthema abstimmungsthema) {
        return abstimmungsthemaRepository.save(abstimmungsthema);
    }
    // Weitere benutzerdefinierte Methoden für die Geschäftslogik
}
