package ru.iate.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iate.diplom.enteties.Record;
import ru.iate.diplom.enteties.Station;

import javax.transaction.Transactional;
import java.util.List;


public interface StationRepository extends JpaRepository<Station, Long> {
    Station findById(long id);
    @Transactional
    Station findStationByCoordinates(String coordinates);
    Station findStationByRecordsIn(List<Record> records);
    List<StationDTO> findStationsByRecordsIn(List<Record> records);


//    @Query(value = "select * from stations inner join records r on stations.id = r.station_id and r.type='2' inner join fields f on r.id = f.record_id and f.name='2ПОГОМСНВ' where stations.id = 3",nativeQuery = true)
//@Query("select new ru.iate.diplom.enteties.StationDTO(s.name,s.coordinates,r) from Station s inner join Record r on r.station = s where s.id in :stids")
//List<String> custom(@Param("stids")ArrayList<Long> ids);
}