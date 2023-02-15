package com.learnkafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.entity.LibraryEvent;
import com.learnkafka.jpa.LibraryEventsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class LibraryEventsService {

    private final LibraryEventsRepository repository;
    private final ObjectMapper mapper;


    public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        LibraryEvent libraryEvent = mapper.readValue(consumerRecord.value(), LibraryEvent.class);
        log.info("libraryEvent: {}", libraryEvent);

        if (libraryEvent != null && libraryEvent.getLibraryEventId() == 999) {
            throw new RecoverableDataAccessException("Temporary Network Issue");
        }

        switch (libraryEvent.getType()) {
            case NEW -> save(libraryEvent);
            case UPDATE -> update(libraryEvent);
        }
    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        repository.save(libraryEvent);
        log.info("Successfully Persisted the Library Event {}", libraryEvent);
    }

    private void update(LibraryEvent libraryEvent) {
        if (libraryEvent.getLibraryEventId()==null) {
            throw new IllegalArgumentException("Library Event Id is missing");
        }

        Optional<LibraryEvent> event = repository.findById(libraryEvent.getLibraryEventId());
        if (event.isEmpty()) {
            throw new IllegalArgumentException("Not a valid library event");
        }
        log.info("Validation is successful for library Event : {}", event.get());
        save(libraryEvent);
    }
}
