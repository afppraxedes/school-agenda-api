package com.schoolagenda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // <--- Importante!

@SpringBootApplication
@EnableJpaAuditing // 1. Habilita o Auditing Globalmente
public class SchoolAgendaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolAgendaBackendApplication.class, args);
	}

}
