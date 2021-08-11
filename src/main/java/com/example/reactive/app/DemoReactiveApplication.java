package com.example.reactive.app;

import com.example.reactive.app.model.AuthenticatedUser;
import com.example.reactive.app.model.Permission;
import com.example.reactive.app.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class DemoReactiveApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DemoReactiveApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoReactiveApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        exampleIterable1();
//        exampleIterable2();
//        exampleIterable3();
//        exampleIterable4();
//        exampleIterable5();
        exampleIterableAndFlapMap();
//        exampleIterableAndCollectList();
//        exampleCombineMonoTypeWithFlatMap();
//        exampleCombineMonoTypeWithZipWith();
    }

    private List<String> getList() {
        List<String> passList = new ArrayList<>();
        passList.add("JoH4n4");
        passList.add("Mik3");
        passList.add("M4ri4n4");
        passList.add("G4");
        passList.add("Ros3");
        passList.add("T3r3s4");
        return passList;
    }

    private void exampleCombineMonoTypeWithZipWith() {
        Mono<User> user1 = Mono.fromCallable(() -> new User("Miguel", "Flor", "2304"));
        Mono<Permission> permission1 = Mono.fromCallable(() -> new Permission(Arrays.asList("ROLE_USER", "ROLE_CLIENT")));

        user1.zipWith(permission1, (user, permission) -> new AuthenticatedUser(user, permission))
                .subscribe(au -> LOG.info(au.getUser().getName()
                        .concat(", ")
                        .concat(au.getUser().getLastname())
                        .concat("/")
                        .concat(au.getPermission().getRoles().toString())));

        user1.zipWith(permission1)
                .map(tuple -> new AuthenticatedUser(tuple.getT1(), tuple.getT2()))
                .subscribe(au -> LOG.info(au.getUser().getName()
                        .concat(" ")
                        .concat(au.getUser().getLastname())
                        .concat("/")
                        .concat(au.getPermission().getRoles().toString())));

        user1.zipWith(permission1)
                .flatMap(tuple -> Mono.just(new AuthenticatedUser(tuple.getT1(), tuple.getT2())))
                .subscribe(au -> LOG.info("Mono: " + au.getUser().getName()
                        .concat(" ")
                        .concat(au.getUser().getLastname())
                        .concat("/")
                        .concat(au.getPermission().getRoles().toString())));
    }

    private void exampleCombineMonoTypeWithFlatMap() {
        Mono<User> user1 = Mono.fromCallable(() -> new User("Miguel", "Flor", "2304"));
        Mono<Permission> permission1 = Mono.fromCallable(() -> new Permission(Arrays.asList("ROLE_USER", "ROLE_CLIENT")));

        user1.flatMap(u -> permission1.map(p -> new AuthenticatedUser(u, p)))
                .subscribe(au -> LOG.info(au.getUser().getName()
                        .concat("/")
                        .concat(au.getPermission().getRoles().toString())));
    }

    private void exampleIterableAndCollectList() {
        /* Con este ejemplo lo que conseguimos es que en lugar de que se vaya emitiendo elemento por elemento
        lo que sucede es que se le emitirá al subscribe una sola vez con el listado completo y ya allí se
        recorre la lista */
        Flux.fromIterable(getList())
                .collectList()
                .subscribe(list -> {
                    list.forEach(it -> LOG.info(it));
                });
    }

    private void exampleIterableAndFlapMap() {
        Flux.fromIterable(getList())
                .map(pass -> new User(null, null, pass))
                .filter(user -> user.getPass().length() > 5)
                .flatMap(user -> {
                    if (user.getPass().length() > 5) {
                        return Mono.just(user);
                    } else {
                        return Mono.empty();
                    }
                })
                .map(user -> {
                    String maskedPass = user.getPass().substring(0, 3).concat("****");
                    user.setPass(maskedPass);
                    return user;
                })
                .subscribe(user -> LOG.info("User pass: " + user.getPass()),
                        e -> LOG.error(e.getMessage()),
                        () -> LOG.info("El proceso ha finalizado"));
    }

    private void exampleIterable1() {
        Flux<String> names = Flux.just("Johana", "Mike", "Mariana", "Gary", "Rose")
                .doOnNext(name -> System.out.println(name));
        names.subscribe();
    }

    private void exampleIterable2() {
        Flux<String> names = Flux.just("Johana", "Mike", "Mariana", "Gary", "Rose")
                .doOnNext(System.out::println);
        names.subscribe();
    }

    private void exampleIterable3() {
        Flux<String> passwords = Flux.just("JoH4n4", "Mik3", "M4ri4n4", "G4", "Ros3")
                .doOnNext(pass -> {
                    if (pass.length() < 4) {
                        throw new RuntimeException("Password length is invalid!");
                    }
                    System.out.println(pass);
                });
        passwords.subscribe(null, e -> LOG.error(e.getMessage()));
    }

    private void exampleIterable4() {
        Flux<User> users = Flux.just("JoH4n4", "Mik3", "M4ri4n4", "G4", "Ros3")
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

    private void exampleIterable5() {
        Flux<User> users = Flux.fromIterable(getList())
                .map(pass -> new User(null, null, pass))
                .filter(user -> user.getPass().length() > 5)
                .doOnNext(user -> {
                    if (user.getPass().length() < 4) {
                        throw new RuntimeException("Password length is invalid!");
                    }
                });
        users.subscribe(user -> LOG.info("User pass: " + user.getPass()),
                e -> LOG.error(e.getMessage()), () -> LOG.info("El proceso ha finalizado"));
    }
}
