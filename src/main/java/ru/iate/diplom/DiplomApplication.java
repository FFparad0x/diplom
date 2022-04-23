package ru.iate.diplom;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.iate.diplom.repository.StationRepository;
import ru.iate.diplom.enteties.Station;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class DiplomApplication {


	public static void main(String[] args) {
		SpringApplication.run(DiplomApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner demo(StationRepository repository){
//		return(args) ->
//		{
//			System.out.println(repository.findById(7));
//		};
//
//	}

}
