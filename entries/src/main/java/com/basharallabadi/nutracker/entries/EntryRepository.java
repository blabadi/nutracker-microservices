package com.basharallabadi.nutracker.entries;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepository extends ReactiveCrudRepository<Entry, String> {
}
