package ru.iate.diplom.repository;

import ru.iate.diplom.enteties.Station;

public interface Fieldd  {
    int getDepth();

    String getType();

    String getValue();

    String getComment();

    int getOrder();

    String getName();

    RecordInfo getRecord();

    interface RecordInfo {
        Station getStation();

        int getYear();

        int getMonth();

        int getDay();

        interface StationInfo{

            String getName();
        }
    }
}
