package com.whosfritz.breakdecider.Data.Services;

import com.whosfritz.breakdecider.Data.Entities.Stimmzettel;
import com.whosfritz.breakdecider.Data.Repositories.StimmzettelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StimmzettelService {

    private final StimmzettelRepository stimmzettelRepository;

    public StimmzettelService(StimmzettelRepository stimmzettelRepository) {
        this.stimmzettelRepository = stimmzettelRepository;
    }


    @Transactional
    public List<Stimmzettel> getAllStimmzettel() {
        return stimmzettelRepository.findAll();
    }


    @Transactional
    public Stimmzettel getStimmzettelById(Long id) {
        return stimmzettelRepository.findById(id).orElse(null);
    }


    @Transactional
    public Stimmzettel saveStimmzettel(Stimmzettel stimmzettel) {
        return stimmzettelRepository.save(stimmzettel);
    }


    @Transactional
    public void deleteStimmzettel(Stimmzettel stimmzettel) {
        stimmzettelRepository.delete(stimmzettel);
    }

    public List<Stimmzettel> getAllStimmzettelByUserId(Long userId) {
        return stimmzettelRepository.findAllByBreakDeciderUser_Id(userId);
    }

    // Weitere benutzerdefinierte Methoden für die Geschäftslogik
}
