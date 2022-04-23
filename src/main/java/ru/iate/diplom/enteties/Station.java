package ru.iate.diplom.enteties;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "stations", indexes = {
        @Index(name = "idx_station_coordinates", columnList = "coordinates")
})
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    public Station(Long id, String coordinates, String name, List<Record> records) {
        this.id = id;
        this.coordinates = coordinates;
        this.name = name;
        this.records = records;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    private String coordinates;

    private String name;

    @OneToMany(mappedBy = "station", cascade = CascadeType.REMOVE)
    private List<Record> records;

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public Station() {}

    public Station(String coordinates, String name, List<Record> records) {
        this.coordinates = coordinates;
        this.name = name;
        this.records = records;
    }

    public Long getId() {
        return id;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Station{" +
                "coordinates='" + coordinates + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
