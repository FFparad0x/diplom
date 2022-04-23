package ru.iate.diplom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.iate.diplom.analyzer.Analyzer;
import ru.iate.diplom.enteties.Station;
import ru.iate.diplom.repository.RecordRepository;
import ru.iate.diplom.repository.StationRepository;

import java.net.URISyntaxException;

@RestController
public class MainController {

    @Autowired
    Analyzer analyzer;
    @Autowired
    final  StationRepository stationRepository;
    @Autowired
    final RecordRepository recordRepository;
    public MainController(StationRepository repository, RecordRepository recordRepository) {
        this.stationRepository = repository;
        this.recordRepository = recordRepository;
    }

    @GetMapping("/wait")
    public String test() throws InterruptedException, URISyntaxException {
        try {
            analyzer.Start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "done";
    }

    @GetMapping("/test")
    public String ask(){
        Station station = stationRepository.findStationByRecordsIn(recordRepository.findRecordsByStation(stationRepository.findById(3)));
        System.out.println(station);
        return "ez";
    }
}
