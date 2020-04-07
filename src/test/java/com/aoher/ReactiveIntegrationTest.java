package com.aoher;

import com.aoher.model.Foo;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ReactiveIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ReactiveIntegrationTest.class);

    private static final String LOCALHOST_URI = "http://localhost:8080";


    private WebClient client;

    @BeforeEach
    void init() {
        client = WebClient.create(LOCALHOST_URI);
    }

    @Test
    void whenMonoReactiveEndpointIsConsumed_thenCorrectOutput() {
        final Mono<ClientResponse> fooMono = client.get()
                .uri("/foos/123").exchange().log();
        log.info(fooMono.subscribe().toString());
    }

    @Test
    void whenFluxReactiveEndpointIsConsumed_thenCorrectOutput() throws InterruptedException {
        client.get()
                .uri("/foos")
                .retrieve()
                .bodyToFlux(Foo.class).log()
                .subscribe(s -> log.info(s.toString()));
    }
}
