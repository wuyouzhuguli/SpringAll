package com.example.webflux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author MrBird
 */
public class FluxTest {

    public static void main(String[] args) throws InterruptedException {
        Flux.just("Hello", "World").subscribe(System.out::println);
        Flux.fromArray(new Integer[]{1, 2, 3}).subscribe(System.out::println);
        Flux.empty().subscribe(System.out::println);
        Flux.range(1, 4).subscribe(System.out::println);
        // Flux.interval(Duration.of(1, ChronoUnit.SECONDS)).subscribe(System.out::println);

        Flux.generate(sink -> {
            sink.next("Hello");
            sink.complete();
        }).subscribe(System.out::println);


        final Random random = new Random();
        Flux.generate(ArrayList::new, (list, sink) -> {
            int value = random.nextInt(100);
            list.add(value);
            sink.next(value);
            if (list.size() == 10) {
                sink.complete();
            }
            return list;
        }).subscribe(System.out::println);

        Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }
            sink.complete();
        }).subscribe(System.out::println);

        Flux.range(1, 10).filter(i -> i % 2 == 0).subscribe(System.out::println);

        Flux.range(1, 20).take(10).subscribe(System.out::println);
        Flux.range(1, 20).takeLast(10).subscribe(System.out::println);
        Flux.range(1, 20).takeWhile(i -> i < 10).subscribe(System.out::println);
        Flux.range(1, 20).takeUntil(i -> i == 10).subscribe(System.out::println);

        Flux.range(1, 10).reduce((x, y) -> x + y).subscribe(System.out::println);
        Flux.range(1, 10).reduceWith(() -> 10, (x, y) -> x + y).subscribe(System.out::println);

        Flux.merge(
                Flux.interval(Duration.of(500, ChronoUnit.MILLIS)).take(2),
                Flux.interval(Duration.of(500, ChronoUnit.MILLIS)).take(2)
        ).toStream().forEach(System.out::println);

        Flux.range(1, 100).buffer(20).subscribe(System.out::println);

        Flux.range(1, 10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);
        Flux.range(1, 10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);

        Flux.just("a", "b", "c", "d")
                .zipWith(Flux.just("e", "f", "g", "h", "i"))
                .subscribe(System.out::println);

        Flux.just("a", "b", "c", "d")
                .zipWith(Flux.just("e", "f", "g", "h", "i"), (s1, s2) -> String.format("%s-%s", s1, s2))
                .subscribe(System.out::println);


        Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalStateException()))
                .subscribe(System.out::println, System.err::println);

        Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalStateException()))
                .onErrorReturn(0)
                .subscribe(System.out::println);


        Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalArgumentException()))
                .onErrorResume(e -> {
                    if (e instanceof IllegalStateException) {
                        return Mono.just(0);
                    } else if (e instanceof IllegalArgumentException) {
                        return Mono.just(-1);
                    }
                    return Mono.empty();
                }).subscribe(System.out::println);

        Thread.currentThread().join(20000);
    }
}
