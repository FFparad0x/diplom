package ru.iate.diplom.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;
import ru.iate.diplom.enteties.Field;
import ru.iate.diplom.enteties.Record;
import ru.iate.diplom.enteties.Station;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hibernate.annotations.QueryHints.READ_ONLY;
import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;
import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

public interface FieldRepository extends CrudRepository<Field, Long> {

    @Override
    <S extends Field> Iterable<S> saveAll(Iterable<S> entities);


    @Query("select f from Field f where f.record.station.name = ?1 and f.record.type = ?2")
    List<Fieldd> findByRecord_Station_NameAndRecord_Type(String record_station_name, String record_type);


    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "50"),
            @QueryHint(name = HINT_CACHEABLE, value = "false"),
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Query("select f from Field f where f.record.type = ?1")
    Stream<Fieldd> find(String record_type);
}