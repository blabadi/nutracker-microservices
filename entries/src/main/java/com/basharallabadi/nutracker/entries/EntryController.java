package com.basharallabadi.nutracker.entries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/entry")
public class EntryController {

    @Autowired
    private EntryService entrySvc;

    @GetMapping
    public Flux<Entry> getUserEntriesInPeriod(@RequestParam("start") String start,
                                              @RequestParam("end") String end,
                                              @RequestParam("username") String username) {

        DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate startDate, endDate = null;
        startDate = LocalDate.parse(start, dateTimeFormatter);
        endDate = LocalDate.parse(end, dateTimeFormatter);
        endDate = endDate.plusDays(1);
        return entrySvc.getUserEntriesInPeriod(username, startDate, endDate);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Entry> create(@RequestBody Mono<Entry> entry){
        System.out.println("create "+ entry);
        return entrySvc.create(entry);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void update(@RequestBody Entry entry){
        System.out.println("update" + entry);
        entrySvc.updateUserEntry(entry);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id){
        System.out.println("delete id:" + id);
        entrySvc.deleteUserEntry(id);
    }

    Flux<String> capitalizeMany(Flux<String> flux) {
        return flux.map(u -> u.toUpperCase());
    }

}