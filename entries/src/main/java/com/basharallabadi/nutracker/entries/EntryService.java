package com.basharallabadi.nutracker.entries;

import com.basharallabadi.nutracker.entries.FoodServiceAdapter.FoodServiceAdapter;
import com.basharallabadi.nutracker.entries.errors.NotFound;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class EntryService {

    EntryRepository entryRepo;
    CustomEntryRepo customRepo;
    private FoodServiceAdapter foodService;

    @Autowired
    public EntryService(EntryRepository entryRepo, CustomEntryRepo customRepo, FoodServiceAdapter foodService) {
        this.entryRepo = entryRepo;
        this.customRepo = customRepo;
        this.foodService = foodService;
    }

    public Mono<Entry> create(Mono<Entry> entry) {
        Mono<Food> err = Mono.error(new NotFound());
        Mono<Entry> invalidEntry = Mono.error(new IllegalArgumentException());
        return entry
                .filter(this::isValideEntry)
                .switchIfEmpty(invalidEntry)
                .flatMap(e -> {
                    String foodId = e.getFood().getId();
                    return foodService.byId(foodId)
                            .switchIfEmpty(err)
                            .then(entryRepo.save(e));
                });
    }

    private boolean isValideEntry(Entry e) {
        return e.getAmount() > 0 && e.getFood() != null && e.getFood().getId() != null;
    }

    /**
     * gets the entries in a period
     * @param owner the creator
     * @param start (inclusive)
     * @param end end date (exclusive)
     * @return flux of entries
     */
    public Flux<Entry> getUserEntriesInPeriod(String owner, LocalDate start, LocalDate end) {
        return customRepo.getByPeriod(owner, start, end);
    }

    public Mono<UpdateResult> updateUserEntry(Entry entry) {
        return customRepo.updateUserEntry(entry);
    }

    public void deleteUserEntry(String id) {
        this.customRepo.deleteUserEntry(id);
    }
}
