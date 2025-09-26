package gng.learning.accountapi.services;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientServiceTest {

    private final HttpClientService httpClientService = new HttpClientService();

    @Test
    void sendPaymentResult() {

        UUID id = UUID.randomUUID();

        String testUrl =  "http://localhost:5160/payment/bank-notification" + "/" + id.toString();
        Boolean testSuccess = true;

        // Since the method does not return anything and we cannot easily mock RestTemplate here,
        // we will just call the method to ensure it runs without exceptions.
        assertDoesNotThrow(() -> httpClientService.sendPaymentResult(testUrl, testSuccess));
    }
}