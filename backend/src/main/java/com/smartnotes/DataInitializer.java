package com.smartnotes;

import com.smartnotes.model.Note;
import com.smartnotes.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(NoteRepository repo) {
        return args -> {
            repo.save(new Note("Welcome to Smart Notes",
                    "This is your personal note-taking app. Create, edit, and organize your notes easily.",
                    "General"));
            repo.save(new Note("Spring Boot Basics",
                    "Spring Boot makes it easy to create stand-alone, production-grade Spring applications.",
                    "Study"));
            repo.save(new Note("Shopping List",
                    "Milk, Eggs, Bread, Butter, Coffee, Sugar",
                    "Personal"));
            repo.save(new Note("Project Ideas",
                    "1. Smart Notes App\n2. Task Manager\n3. Budget Tracker\n4. Weather App",
                    "Work"));
        };
    }
}
