package com.whosfritz.breakdecider.Data.Services;

import com.whosfritz.breakdecider.Data.Entities.Stimmzettel;
import com.whosfritz.breakdecider.Data.Repositories.StimmzettelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StimmzettelService {

    private final StimmzettelRepository stimmzettelRepository;

    public StimmzettelService(StimmzettelRepository stimmzettelRepository) {
        this.stimmzettelRepository = stimmzettelRepository;
    }


    public List<Stimmzettel> getAllStimmzettel() {
        return stimmzettelRepository.findAll();
    }


    public Stimmzettel getStimmzettelById(Long id) {
        return stimmzettelRepository.findById(id).orElse(null);
    }


    public Stimmzettel saveStimmzettel(Stimmzettel stimmzettel) {
        return stimmzettelRepository.save(stimmzettel);
    }


    public List<Stimmzettel> getAllStimmzettelByUserId(Long userId) {
        return stimmzettelRepository.findAllByBreakDeciderUser_Id(userId);
    }

    public void deleteStimmzettelById(Long id) {
        stimmzettelRepository.deleteById(id);
    }

    // Weitere benutzerdefinierte Methoden für die Geschäftslogik
}
