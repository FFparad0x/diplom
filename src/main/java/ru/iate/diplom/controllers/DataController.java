package ru.iate.diplom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.iate.diplom.enteties.StreamRequest;
import ru.iate.diplom.repository.FieldRepository;
import ru.iate.diplom.repository.Fieldd;
import ru.iate.diplom.repository.RecordRepository;
import ru.iate.diplom.repository.StationRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@EnableTransactionManagement
public class DataController {

    private final StationRepository stationRepository;
    private final RecordRepository recordRepository;
    private final FieldRepository fieldRepository;
    private final StreamRequest request;
    DataController(@Autowired StationRepository repository, @Autowired RecordRepository recordRepository,
                   @Autowired FieldRepository fieldRepository, @Autowired StreamRequest request) {
        this.stationRepository = repository;
        this.recordRepository = recordRepository;
        this.fieldRepository = fieldRepository;
        this.request = request;
    }
    @CrossOrigin()
    @GetMapping("/stations")
    List<String> allStations(){
        return stationRepository.findAll().stream()
                .filter(station -> station.getName() != null)
                .map(station -> station.getName().strip())
                .collect(Collectors.toList());
    }

    @CrossOrigin()
    @GetMapping("/id")
    String[] test(@RequestParam(required = false) String[] data){
        return data;
    }

    @GetMapping("/search")
    void get(){
        request.GetAll();
    }

}
