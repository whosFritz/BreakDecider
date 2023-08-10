package com.whosfritz.breakdecider.Services;

import com.whosfritz.breakdecider.Entities.Stimmzettel;
import com.whosfritz.breakdecider.Repositories.StimmzettelRepository;
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

    public void deleteStimmzettel(Long id) {
        stimmzettelRepository.deleteById(id);
    }

    // Weitere benutzerdefinierte Methoden für die Geschäftslogik
}
