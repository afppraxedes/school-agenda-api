package com.schoolagenda;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // <--- Importante!

import java.util.TimeZone;

@SpringBootApplication
//@EnableJpaAuditing // 1. Habilita o Auditing Globalmente
public class SchoolAgendaBackendApplication {

	@PostConstruct
	public void init() {
		// Define o fuso horário padrão da aplicação como UTC
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(SchoolAgendaBackendApplication.class, args);
	}

}
