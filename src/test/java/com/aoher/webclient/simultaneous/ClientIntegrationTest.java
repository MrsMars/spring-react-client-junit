package com.aoher.webclient.simultaneous;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientIntegrationTest {

    private WireMockServer wireMockServer;

    @Before
    public void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().port(8089));
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
    }

    @After
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void givenClient_whenFetchingUsers_thenExecutionTimeIsLessThanDouble() {
        int requestsNumber = 5;
        int singleRequestTime = 1000;

        for (int i = 0; i <= requestsNumber; i++) {
            stubFor(get(urlEqualTo("/user/" + i)).willReturn(aResponse().withFixedDelay(singleRequestTime)
            .withStatus(200)
            .withHeader("Content-Type", "appplication/json")
            .withBody(String.format("{ \"id\": %d }", i))));
        }

        List<Integer> userIds = IntStream.rangeClosed(1, requestsNumber)
                .boxed()
                .collect(Collectors.toList());

        Client client = new Client("http://localhost:8080");

        long start = System.currentTimeMillis();
        List<User> users = client.fetchUsers(userIds)
                .collectList().block();
        long end = System.currentTimeMillis();

        long totalExecutionTime = end - start;

        assertEquals("Unexpected number of users", requestsNumber, Objects.requireNonNull(users).size());
        assertTrue("Execution time is too big", 2 * singleRequestTime > totalExecutionTime);
    }
}
