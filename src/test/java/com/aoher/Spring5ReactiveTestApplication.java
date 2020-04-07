//package com.aoher;
//
//import com.aoher.model.Foo;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@SpringBootApplication
//public class Spring5ReactiveTestApplication {
//
//    private static final Logger log = LoggerFactory.getLogger(Spring5ReactiveTestApplication.class);
//
//    private static final String LOCALHOST_URI = "http://localhost:8080";
//
//    @Bean
//    public WebClient client() {
//        return WebClient.create(LOCALHOST_URI);
//    }
//
//    @Bean
//    CommandLineRunner cmd(WebClient client) {
//        return args -> {
//            client.get()
//                    .uri("/foos2")
//                    .retrieve()
//                    .bodyToFlux(Foo.class).log()
//                    .subscribe(s -> log.info(s.toString()));
//        };
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(Spring5ReactiveTestApplication.class, args);
//    }
//}
