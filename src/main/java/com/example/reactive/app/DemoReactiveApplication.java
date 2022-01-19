package com.example.reactive.app;

import com.example.reactive.app.model.AuthenticatedUser;
import com.example.reactive.app.model.Permission;
import com.example.reactive.app.model.User;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;

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
//        exampleIterableAndFlapMap();
//        exampleIterableAndCollectList();
//        exampleCombineMonoTypeWithFlatMap();
//        exampleCombineFluxTypeWithZipWith();
//        exampleCombineFluxTypeWithZipWithAndRange();
//        exampleCombineMonoTypeWithZipWith();
//        exampleWithDelay();
//        exampleWithCountDownLatchAndRetry();
//        exampleWithEmitter();
        exampleBackPressure();
    }

    private void exampleBackPressure() {
        //TODO
        Flux.range(0, 12)
                .log()
                .limitRate(3)
                .subscribe(/*new Subscriber<Integer>() {
                    private Subscription s;
                    private Integer limit = 3;
                    private Integer receivedElements = 0;
                    @Override
                    public void onSubscribe(Subscription s) {
                        this.s = s;
                        s.request(limit);
                    }

                    @Override
                    public void onNext(Integer i) {
                        LOG.info(i.toString());
                        receivedElements++;
                        if (receivedElements == limit) {
                            receivedElements = 0;
                            this.s.request(limit);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }*/);

    }

    private void exampleWithInterval() {
        Flux range = Flux.range(10, 10);
        Flux delay = Flux.interval(Duration.ofSeconds(1));
        LOG.info("Starting process");
        range.zipWith(delay, (r, d) -> r)
            .doOnNext(value -> LOG.info(value.toString()))
            .blockLast(); // No se recomienda porque va contra el concepto de programación reactiva que es netamente asíncrono.
//            .subscribe();
        LOG.info("Finished process");
    }

    private void exampleWithDelay() throws InterruptedException {
        Flux<Integer> range = Flux.range(15, 5)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(value -> LOG.info(value.toString()));
        range.subscribe();
        Thread.sleep(7000);
        LOG.info("Finished process");
    }

    private void exampleWithCountDownLatchAndRetry() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Flux.interval(Duration.ofSeconds(1))
            .doOnTerminate(latch::countDown)
            .flatMap(i -> {
                if (i > 5) {
                    return Flux.error(new InterruptedException("Until 5 elements!"));
                }
                return Flux.just(i);
            })
            .map(i -> "Element: " + i)
            .retry(1) // Como ha habido un error vuelve a ejecutar el flujo
            .subscribe(msg -> LOG.info(msg), err -> LOG.info(err.getMessage()));

        latch.await();
    }

    private void exampleWithEmitter() {
        //TODO la diferencia principal es que en el primero es un for rápido y en el segundo caso es son hilos que se lanzan cada segundo
        Flux.create(emitter -> {
//            for (int i=0;i<10;i++) {
//                emitter.next(i+1);
//                if (i==7) {
//                    emitter.complete();
//                }
//                if (i==5) {
//                    emitter.error(new InterruptedException("Flux was interrupted!"));
//                }
//            }
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                private Integer counter = 0;
                @Override
                public void run() {
                    emitter.next(++counter);
                    if (counter == 7) {
                        timer.cancel();
                        emitter.complete();
                    }
                    if (counter == 5) {
                        timer.cancel();
                        emitter.error(new InterruptedException("Flux was interrupted!"));
                    }
                }
            }, 1000, 1000);
        }).subscribe(next -> LOG.info("Task: " + next.toString()),
                err -> LOG.error(err.getMessage()),
                () -> LOG.info("Proceso finalizado"));
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

    private void exampleCombineFluxTypeWithZipWithAndRange() {
        Flux<Integer> range1 = Flux.range(10, 10);
        Flux.just(3, 7, 11, 13, 17, 19)
                .zipWith(range1, (val1, val2) -> String.format("Value1: %d, Value2: %d", val1, val2))
                .subscribe(text -> LOG.info(text));
    }

    private void exampleCombineFluxTypeWithZipWith() {
        List<User> users = new ArrayList<>();
        users.add(new User("Miguel", "Flor", "2304"));
        users.add(new User("Johana", "Caceres", "0204"));
        List<Permission> permissions = new ArrayList<>();
        permissions.add(new Permission(Arrays.asList("ROLE_USER", "ROLE_CLIENT")));
        permissions.add(new Permission(Arrays.asList("ROLE_ADMIN", "ROLE_TEST")));

        Flux<User> flux1 = Flux.fromIterable(users);
        Flux<Permission> flux2 = Flux.fromIterable(permissions);

        flux1.zipWith(flux2)
                .map(tuple -> new AuthenticatedUser(tuple.getT1(), tuple.getT2()))
                .subscribe(au -> LOG.info(au.getUser().getName()
                        .concat(" ")
                        .concat(au.getUser().getLastname())
                        .concat("/")
                        .concat(au.getPermission().getRoles().toString())));
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
        lo que sucede es que se le emitirá al subscribe(...) una sola vez un objeto Mono<> con el listado completo y
        ya allí se recorre la lista */
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
