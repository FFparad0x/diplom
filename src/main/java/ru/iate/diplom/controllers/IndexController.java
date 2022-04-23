package ru.iate.diplom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.iate.diplom.enteties.Station;
import ru.iate.diplom.repository.RecordRepository;
import ru.iate.diplom.repository.StationRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    StationRepository stationRepository;
    @Autowired
    RecordRepository recordRepository;

    @RequestMapping("/first")
    public String Index( @RequestParam(required = false, name = "data") List<String> data ,
                         @RequestParam(required = false, name = "station")List<Long> stations,
                         @RequestParam(required = false, name = "record") List<String> records,
                         @RequestParam(required = false, name = "field") List<String> fields,
                         @RequestParam(required = false, name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate sdate,
                         @RequestParam(required = false, name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate edate,
                         Model model){
        List<Station> stationList = stationRepository.findAll();
        if(data == null){
            data = new ArrayList<>();
        }
        if(stations == null){
            stations = new ArrayList<>();
        }
        if(records == null){
            records = new ArrayList<>();
        }
        if(fields == null){
            fields = new ArrayList<>();
        }
        if(sdate == null || edate == null){
            sdate = LocalDate.now();
            edate = LocalDate.now();
        }
        sdate = LocalDate.now().withDayOfMonth(1).withYear(2012).withMonth(12);
        System.out.println(sdate);
//        List<RecordDTO> recordsResult = recordRepository.findRecordsByAndMonthBetweenAndYearBetweenAndTypeIn(
//                sdate.getMonthValue(),
//                edate.getDayOfMonth(),
//                sdate.getYear(),
//                edate.getYear(),
//                records
////                );
//        if(recordsResult != null)
//            System.out.println(recordsResult.size());
        model.addAttribute("stationsList", stationList);
        model.addAttribute("data", data);
        model.addAttribute("fields", fields);
        model.addAttribute("records", records);
        model.addAttribute("stations", stations);
        model.addAttribute("sdate",sdate);
        model.addAttribute("edate",edate);
        return "index";
    }
}
