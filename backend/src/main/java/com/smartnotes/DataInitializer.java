package com.smartnotes;

import com.smartnotes.model.Note;
import com.smartnotes.model.User;
import com.smartnotes.repository.NoteRepository;
import com.smartnotes.repository.UserRepository;
import com.smartnotes.security.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            NoteRepository noteRepository,
            UserRepository userRepository) {

        return args -> {

            // Only create demo data when the database is completely empty.
            if (userRepository.count() == 0 && noteRepository.count() == 0) {

                User owner = new User();
                owner.setUsername("demo");
                owner.setEmail("demo@smartnotes.local");
                owner.setPassword(
                        PasswordUtil.hashPassword("demo12345")
                );

                owner = userRepository.save(owner);

                Note welcome = new Note(
                        "Welcome to Smart Notes",
                        "This is your personal note-taking app. Create, edit, and organize your notes easily.",
                        "General"
                );
                welcome.setUser(owner);
                noteRepository.save(welcome);

                Note springBoot = new Note(
                        "Spring Boot Basics",
                        "Spring Boot makes it easy to create stand-alone, production-grade Spring applications.",
                        "Study"
                );
                springBoot.setUser(owner);
                noteRepository.save(springBoot);

                Note shoppingList = new Note(
                        "Shopping List",
                        "Milk, Eggs, Bread, Butter, Coffee, Sugar",
                        "Personal"
                );
                shoppingList.setUser(owner);
                noteRepository.save(shoppingList);

                Note projectIdeas = new Note(
                        "Project Ideas",
                        "1. Smart Notes App\n2. Task Manager\n3. Budget Tracker\n4. Weather App",
                        "Work"
                );
                projectIdeas.setUser(owner);
                noteRepository.save(projectIdeas);
            }
        };
    }
}