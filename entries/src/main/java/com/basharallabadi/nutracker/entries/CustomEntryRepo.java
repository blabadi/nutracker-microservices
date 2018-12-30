package com.basharallabadi.nutracker.entries;

import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public class CustomEntryRepo {

    @Autowired
    ReactiveMongoOperations operations;

    public Flux<Entry> getByPeriod(String owner, LocalDate start, LocalDate end) {
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("createdAt").gte(start).lt(end).and("owner").is(owner));
        return operations.find(findQuery, Entry.class);
    }

    public Mono<UpdateResult> updateUserEntry(Entry entry){
        Query findQuery = new Query();
        findQuery.addCriteria(Criteria.where("_id").is(entry.getId()).and("owner").is(entry.getOwner()));
        Update update = new Update();
        update.set("amount", entry.getAmount());
        return operations.updateFirst(findQuery, update, Entry.class);
    }

    public void deleteUserEntry(String id) {
        operations.remove(new Query().addCriteria(
                Criteria.where("_id").is(id)
            ),
            Entry.class
        );
    }

}
