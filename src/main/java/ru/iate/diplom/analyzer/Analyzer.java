package ru.iate.diplom.analyzer;


import com.ibm.icu.text.Transliterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.iate.diplom.enteties.Field;
import ru.iate.diplom.enteties.Station;
import ru.iate.diplom.enteties.Record;
import ru.iate.diplom.repository.FieldRepository;
import ru.iate.diplom.repository.RecordRepository;
import ru.iate.diplom.repository.StationRepository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class Analyzer {
    private List<String> desc;
    int currentPos;
    private Reader reader;
    private BufferedWriter writer;
    private int rbodyType;
    private int depth = 0;
//    private List<String> stack;
    private String grName;
    Logger logger;
    private int grLen;
    private Dictionary<String, Integer> counters;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private FieldRepository fieldRepository;
    private String descriptionFilePath = "/description/tms.ddl";
    private String dataFilePath = "/si11812d.mci1790019";
    private String Output = "exit.txt";
    private Station station;
    private Record record;
    private List<Field> fields;
    private List<Record> records;
    private List<Station> stations;
    private boolean isStationExists = false;
    private boolean isRecordExists = false;
    private List<List<Field>> superlist;
    private List<Field> fieldsBuff;



    public void setDescriptionFilePath(String descriptionFilePath) {
        this.descriptionFilePath = descriptionFilePath;
    }

    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    public void setOutput(String output) {
        Output = output;
    }

    public Analyzer() {
        logger = LoggerFactory.getLogger(Analyzer.class);
        currentPos = 0;
        rbodyType = 0;
        counters = new Hashtable<>();
        fields = new ArrayList<>();
        records = new ArrayList<>();
        stations = new ArrayList<>();
        superlist = new ArrayList<>();
        fieldsBuff = new ArrayList<>();
    }

    public void Start() throws IOException, URISyntaxException {
        desc = Files.readAllLines(Paths.get(getClass().getResource(descriptionFilePath).toURI()), Charset.forName("Windows-1251"));
        reader = new Reader(Paths.get(getClass().getResource(dataFilePath).toURI()));
        writer = Files.newBufferedWriter(Paths.get(Output), StandardCharsets.UTF_8);
        Process();


    }
    public void Process() throws IOException {

        GetDefinitions();
        while (!reader.EOF() && rbodyType != -1) {
            ParseHeader();
            ParseRecord(rbodyType);
            Record temp = null;
            if(isStationExists == true)
                temp = recordRepository.findRecordByStationAndTypeAndDate( record.getStation(), record.getType(), record.getDate());
            if(temp == null) {
                records.add(record);
                isRecordExists = false;
                fields.addAll(fieldsBuff);
                fieldsBuff.clear();
            }
            else{
                fieldsBuff.clear();
                isRecordExists = true;
            }
        }
        logger.info("Writing database:");
        logger.info("writing stations");
        stationRepository.saveAll(stations);
        logger.info("writing records");
        recordRepository.saveAll(records);
        logger.info("writing fields");
        ArrayList<Field> temp = new ArrayList<>();
        int batchsize = 2500;
        logger.warn("количество полей" + fields.size());
        for (int i = 0; i < fields.size(); i++) {
            temp.add(fields.get(i));
            if(i % batchsize == 0){
                superlist.add(temp);
                temp = new ArrayList<>();
            }
        }
        superlist.add(temp);
        saveAllFields();

        logger.info("done");
        records.clear();
        fields.clear();
        stations.clear();
        Clear();
    }
    private void Clear(){
        currentPos = 0;
        rbodyType = 0;
        while (counters.keys().hasMoreElements()){
            counters.remove(counters.keys().nextElement());
        }
        fields .clear();
        records.clear();
        stations.clear();
        superlist.clear();
        fieldsBuff.clear();
        isStationExists = false;
        isRecordExists = false;
    }
    int poolsize = 10;

    public void saveAllJdbcBatch(List<Field> fields){
//        System.out.println("insert using jdbc batch");
        String sql = String.format(
                "INSERT INTO fields (comment, depth, name, type, value, record_id, seq_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)"
//                ,Field.class.getAnnotation(Table.class).name()
        );
        String url = "jdbc:postgresql://localhost:5432/Measurments";
        String user ="postgres";
        String password = "12345678";
        try (
                Connection connection = DriverManager.getConnection(url,user,password);
                PreparedStatement statement = connection.prepareStatement(sql)
        ){
            int batchsize = 2500;
            int counter = 0;
            for (Field field : fields) {
                statement.clearParameters();
                statement.setString(1, field.getComment());
                statement.setInt(2, field.getDepth());
                statement.setString(3, field.getName());
                statement.setString(4, field.getType());
                statement.setString(5, field.getValue());
                statement.setLong(6, field.getRecord().getId());
                statement.setInt(7, field.getOrder());
                statement.addBatch();
                if ((counter + 1) % batchsize == 0 || (counter + 1) == fields.size()) {
                    statement.executeBatch();
                    statement.clearBatch();
                }
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void saveAllFields(){
        System.out.println("insert using jdbc batch, threading");
        ExecutorService executorService = Executors.newFixedThreadPool(poolsize);
        List<List<Field>> listOfBookSub = superlist;
        List<Callable<Void>> callables = listOfBookSub.stream().map(sublist ->
                (Callable<Void>) () -> {
                    saveAllJdbcBatch(sublist);
//                    fieldRepository.saveAll(sublist);
                    return null;
                }).collect(Collectors.toList());
        try {
            executorService.invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void GetDefinitions() {
        for (int i = desc.size() - 1; i > 0; i--) {
            String[] temp = desc.get(i).split("\\s+;?|;");
            if (temp[0].contains("END"))
                break;
            if (temp[0].equals("LNAT")) {
                ArrayList<String> values = new ArrayList<>();
                for (String j : temp[2].split(",")) {
                    values.add(j);
                }
//                definitions.put(temp[1], values);
            }
        }
    }


    private boolean ParseLine(String grpName) throws IOException {
//        if(grLen + grStart <= reader.current){
//            depth = 0;
//            return false;
//        }

        String fieldType = "";
        String fieldName = "";
        String fieldValue = "";
        String fieldComment = "";

        String[] line = desc.get(currentPos).trim().split("\\s+;?|;");
        currentPos++;
        writer.flush();
        if (line[0].contains("GRP")) {
            writer.write("Новая группа: " + line[1]);
            fieldType = "GRP";
            fieldName = rbodyType+line[1];
            fieldComment = "GRP" + line[1];
            fieldValue = "";
            writer.newLine();
            depth++;
//            stack.add(line[1].replace("(", "").replace(")", "").trim());
            ParseGroup(0,line[1].replace("(", "").replace(")", "").trim());
        }

        if (line[0].contains("GRV") || line[0].contains("GRK")) {
            int len = 0;
            String name = line[1].replace("(", "").replace(")", "").trim();

            if(counters.get(name) == null) {
                name = line[0].split("\\(")[1].replace("(", "").replace(")", "").trim();
            }
            len = counters.get(name);
//            stack.add(name);

            String searchname = "";
            for(int i = 0; i<line.length;i++){
                if(line[i].contains(")")){
                    searchname = line[i + 1];
                    break;
                }
            }
            writer.write("Новая группа GRV: " + searchname);
            writer.newLine();
            int grstart = currentPos;
            fieldType = rbodyType+"GRV";
            fieldName = searchname;
            fieldComment = "GRV" + searchname;
            fieldValue = "";

            if(len != 0) {
                    currentPos = grstart;
                    depth++;
                    ParseGroup(len,searchname);

            }
            else{
                SeekToGRVEnd(searchname);
            }
        }

        if (line[0].equals("MIT")) {
            Object data = null;
            writer.write(line[1] + ":");
            if (line[2].contains("A")) {
                int len = Integer.valueOf(line[2].substring(line[2].indexOf("(") + 1, line[2].indexOf(")")));
                data = reader.ReadSymbols(len);
//                data = "reader.ReadSymbols(len)";
                writer.write(String.valueOf(data));
                if(rbodyType == 1){
                    if(line[1].equals("НАИМЕНСТ")){
                        station.setName(((String) data).strip());
                        if(isStationExists == false)
                            stations.add(station);
                    }
                }
            }

            if (line[2].contains("B")) {
                int len = Integer.valueOf(line[2].substring(line[2].indexOf("(") + 1, line[2].indexOf(")")));
                int acc = -1;
                for (String i : line) {
                    if (i.contains("D")) {
                        acc = Integer.valueOf(i.substring(i.indexOf("(") + 1, i.indexOf(")")));
                    }
                }
                if (acc == -1)
                    data = reader.ReadInt(len);
                else
                    data = reader.ReadDouble(len, acc);
                writer.write(String.valueOf(data));
            }
            fieldType = "MIT";
            fieldName = rbodyType+line[1];
            fieldComment = PrintComment(line);;
            fieldValue = String.valueOf(data);

            writer.newLine();
            writer.flush();
        }

        if (line[0].contains("CHA")) {
            int len = 0;
            int end = !line[0].contains(")") ? line[0].length() : line[0].indexOf(")");

            fieldName = rbodyType+line[0].substring(line[0].indexOf("(") + 1,end).trim();
            writer.write(fieldName);
            for (int i = 1; i < line.length; i++) {
                if (line[i].length() > 0) {
                    if (line[i].charAt(0) == 'B') {
                        len = Integer.valueOf(line[i].substring(line[i].indexOf("(") + 1, line[i].indexOf(")")));
                    }
                    if (line[i].charAt(0) == 'Q') {
                        writer.write(line[i] + ": ");
                    }
                }
            }
            fieldType = "CHA";
            fieldComment = PrintComment(line);
            fieldValue = String.valueOf(reader.ReadInt(len));
            writer.write(fieldValue);
            writer.newLine();
            writer.flush();
        }

        if (line[0].contains("KEY")) {

            int len = 0;
            writer.write(line[0].replace("(", " ").replace(")", "").trim() + ":");
            len = getLen(line, len);
            fieldName = rbodyType+line[1];
            fieldType = "KEY";
            fieldValue = String.valueOf(reader.ReadInt(len));
            writer.write(fieldValue + " " + line[1]);
            fieldComment = PrintComment(line);
            writer.newLine();
            writer.flush();
            if(line[1].equals("ДЕНЬ")){
                record.setDate(record.getDate().withDayOfMonth(Integer.parseInt(fieldValue)));
                record.setDaily(true);
            }
        }

        if (line[0].contains("CNT")) {
            int len = 0;
            len = getLen(line, len);
            int temp = reader.ReadInt(len);
            counters.put(line[1], temp);
            writer.write(line[0] + " " + line[1] + ": " + temp);
            fieldName = rbodyType+line[1];
            fieldComment = PrintComment(line);
            fieldValue = String.valueOf(temp);
            fieldType = "CNT";
            writer.newLine();
            writer.flush();
        }

        if (line[0].contains("END")) {
            if (line[1].equals(grName))
                depth--;
            else{
                if(grpName != null){
                    return !grpName.equals(line[1]);
                }
            }
        }
        if(fieldType.length() > 1) {
            Field field = new Field(record, fieldType, fieldValue, fieldComment, fieldName, depth, reader.current);
            fieldsBuff.add(field);
        }
        return true;
    }

    private int getLen(String[] line, int len) {
        for (int i = 1; i < line.length; i++) {
            if (line[i].length() > 0) {
                if (line[i].charAt(0) == 'B') {
                    len = Integer.valueOf(line[i].substring(line[i].indexOf("(") + 1, line[i].indexOf(")")));
                }
            }
        }
        return len;
    }

    private void SeekToGRVEnd(String name) throws IOException {
        for (int i = currentPos + 1; i < desc.size(); i++) {
            String[] temp = desc.get(i).trim().split("\\s+;?|;");
            if(temp[0].equals("END")){
                if(temp[1].equals(name)) {
                    currentPos = i + 1;
                    break;
                }
            }
        }
        writer.write("Группа имела длину 0, поэтому пропушена\n");

    }

    private String PrintComment(String[] line) throws IOException {
        boolean flag = false;
        String buff = "";
        for (int i = 0; i < line.length; i++) {
            if (line[i].contains("//")) {
                flag = true;
            }
            if (flag) {

                writer.write(" " + line[i]);
                buff += " " + line[i].replace("/","");
            }
        }
        return buff;
    }

    private void ParseGroup(int length, String grpName) throws IOException {

        String[] line = desc.get(currentPos).trim().split("\\s+;?|;");
        int len = length;
        if(len == 0)
        len = Integer.valueOf(line[0].substring(line[0].indexOf("(") + 1, line[0].indexOf(")")));
        String name = "";
        for(int i = 0; i< line.length; i++){
            if(line[i].length() > 0)
            if(line[i].charAt(0) == 'C'){
                name = line[i].replace("C","").replace("(","").replace(")","");
            }
        }
        //currentPos++;
        int start = currentPos;
        int count = 0;


        for (int i = 0; i < len; i++) {
            count = 0;
            while (ParseLine(grpName)) {
                count++;
            }
            if(i + 1 < len)
                currentPos = start;
        }
        //currentPos += count;
        depth--;
        writer.write("Конец группы: " + grpName);
        writer.newLine();
        writer.newLine();
        writer.flush();
    }

    private void ParseRecord(int type) throws IOException {

        SeekToDefinition(type);
        while (counters.keys().hasMoreElements()) {
            counters.remove(counters.keys().nextElement());
        }
        while (depth > 0 ) {
            ParseLine(null);
        }
        depth = 0;
//        recordRepository.save(record);
    }

    private void SeekToDefinition(int type) {
        for (int j = 0; j < desc.size(); j++) {
            String i = desc.get(j);
            int temp = i.indexOf("RBODY(" + type);
            if (temp != -1) {
                grName = i.trim().split("\\s+;?|;")[1];
                currentPos = j + 1;
                depth += 1;
                break;
            }
        }
    }

    public void ParseHeader() throws IOException {
        record = new Record();
        grLen = reader.ReadInt(2);
        reader.Skip(2);

        writer.write("\n\nДЛЗАП: " + grLen + " НАЧАЛО ЗАГОЛОВКА");
        writer.newLine();
        int year = reader.ReadInt(2);
        writer.write("ГОД: " + year);
        writer.newLine();
        int month = reader.ReadInt(1);
        writer.write("МЕСЯЦ: " + month);
        writer.newLine();
        Integer coordinates = reader.ReadInt(4);
        writer.write("СТАНЦИЯ: " + coordinates);
        writer.newLine();
        rbodyType = reader.ReadInt(1);
        writer.write("ТИПЗАП: " + rbodyType);
        writer.newLine();
        writer.newLine();

        grLen -=12;
        writer.flush();
        if(rbodyType == 0) {
            rbodyType = -1;
        }

        if(rbodyType == 1){
            station = stationRepository.findStationByCoordinates(String.valueOf(coordinates));
            isStationExists = true;
            if(station == null){
                station = new Station();
                station.setCoordinates(String.valueOf(coordinates));
                isStationExists = false;
            }
        }
//        stationRepository.save(station);
        record.setType(String.valueOf(rbodyType));
        LocalDate date = LocalDate.now().withMonth(month).withYear(year).withDayOfMonth(1);
        record.setDate(date);
        record.setDaily(false);
        record.setLength(grLen);

        record.setStation(station);
    }


}