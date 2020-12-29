package com.example.reactive.app;

import com.example.reactive.app.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoReactiveApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DemoReactiveApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoReactiveApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        Flux<String> names = Flux.just("Johana", "Mike", "Mariana", "Gary", "Rose")
//                .doOnNext(name -> System.out.println(name));
//        names.subscribe();

//        Flux<String> names = Flux.just("Johana", "Mike", "Mariana", "Gary", "Rose")
//                .doOnNext(System.out::println);
//        names.subscribe();

//        Flux<String> passwords = Flux.just("JoH4n4", "Mik3", "M4ri4n4", "G4", "Ros3")
//                .doOnNext(pass -> {
//                    if (pass.length() < 4) {
//                        throw new RuntimeException("Password length is invalid!");
//                    }
//                    System.out.println(pass);
//                });
//        passwords.subscribe(null, e -> LOG.error(e.getMessage()));

//        Flux<User> users = Flux.just("JoH4n4", "Mik3", "M4ri4n4", "G4", "Ros3")
//                .map(pass -> new User(null, null, pass))
//                .filter(user -> user.getPass().length() > 5)
//                .doOnNext(user -> {
//                    if (user.getPass().length() < 4) {
//                        throw new RuntimeException("Password length is invalid!");
//                    }
//                });
//        users.subscribe(user -> LOG.info("User pass: " + user.getPass()),
//                e -> LOG.error(e.getMessage()));

        List<String> passList = new ArrayList<>();
        passList.add("JoH4n4");
        passList.add("Mik3");
        passList.add("M4ri4n4");
        passList.add("G4");
        passList.add("Ros3");
        passList.add("T3r3s4");
        Flux<User> users = Flux.fromIterable(passList)
                .map(pass -> new User(null, null, pass))
                .filter(user -> user.getPass().length() > 5)
                .doOnNext(user -> {
                    if (user.getPass().length() < 4) {
                        throw new RuntimeException("Password length is invalid!");
                    }
                });
        users.subscribe(user -> LOG.info("User pass: " + user.getPass()),
                e -> LOG.error(e.getMessage()));
    }
}
