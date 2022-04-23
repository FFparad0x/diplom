package ru.iate.diplom.enteties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.iate.diplom.repository.FieldRepository;
import ru.iate.diplom.repository.Fieldd;

import java.util.stream.Stream;

@Component
public class StreamRequest {
    @Autowired
    private FieldRepository repository;

    @Transactional(readOnly = true)
    public void GetAll(){
        try (Stream<Fieldd> fielddStream = repository.find("2")) {
            fielddStream.forEach(fieldd -> fieldd.getName());
            System.out.println("done");
        }
    }
}
