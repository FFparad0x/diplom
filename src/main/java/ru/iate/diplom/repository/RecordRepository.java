package ru.iate.diplom.repository;

import org.springframework.data.repository.CrudRepository;
import ru.iate.diplom.enteties.Field;
import ru.iate.diplom.enteties.Record;
import ru.iate.diplom.enteties.Station;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecordRepository extends CrudRepository<Record, Long> {
    @Override
    Optional<Record> findById(Long aLong);
    @Transactional
    Record findRecordByStationAndTypeAndDate(Station station, String type, LocalDate date);
    List<Record> findRecordsByStation(Station station);



}