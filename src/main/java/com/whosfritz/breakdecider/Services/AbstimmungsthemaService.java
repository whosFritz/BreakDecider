package com.whosfritz.breakdecider.Services;

import com.whosfritz.breakdecider.Entities.Abstimmungsthema;
import com.whosfritz.breakdecider.Repositories.AbstimmungsthemaRepository;
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

    public Abstimmungsthema getAbstimmungsthemaById(Long id) {
        return abstimmungsthemaRepository.findById(id).orElse(null);
    }

    public Abstimmungsthema saveAbstimmungsthema(Abstimmungsthema abstimmungsthema) {
        return abstimmungsthemaRepository.save(abstimmungsthema);
    }

    public void deleteAbstimmungsthema(Long id) {
        abstimmungsthemaRepository.deleteById(id);
    }

    public void saveAllAbstimmungsthemen(List<Abstimmungsthema> abstimmungsthemaList) {
        abstimmungsthemaRepository.saveAll(abstimmungsthemaList);
    }

    // Weitere benutzerdefinierte Methoden für die Geschäftslogik
}
