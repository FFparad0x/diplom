package ru.iate.diplom.enteties;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "records", indexes = {
        @Index(name = "idx_record_station_id", columnList = "station_id, type"),
        @Index(name = "idx_record_date", columnList = "date")
})
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "station_id")
    private Station station;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    private List<Field> fields;


    public Record(Station station, String type, boolean daily, LocalDate date, int length) {
        this.station = station;
        this.type = type;
        this.daily = daily;
        this.length = length;
        this.date = date;
    }

    public int getLength() {
        return length;
    }



    private int length;

    public Long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    public String getType() {
        return type;
    }

    public boolean isDaily() {
        return daily;
    }


    public void setStation(Station station) {
        this.station = station;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDaily(boolean daily) {
        this.daily = daily;
    }


    private String type;

    private boolean daily;


    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Record(Station station) {
        this.station = station;
    }

    public Record() {

    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", station=" + station +
                ", length=" + length +
                ", type='" + type + '\'' +
                ", daily=" + daily +
                ", date=" + date +
                '}';
    }
}
