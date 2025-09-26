package gng.learning.accountapi.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.StyledEditorKit;

// servci to send payment result notification to ecommerce backend
@Service
public class HttpClientService {

    private final RestTemplate restTemplate = new RestTemplate();

    public HttpClientService() {

    }

    public void sendPaymentResult(String url , Boolean success) {

        HttpHeaders headers = new HttpHeaders();

        String finalUrl = url + "?success=" + success;

        // Combine headers and body
        HttpEntity<Void> requestEntity = new HttpEntity<>( headers);

        System.out.println("Sending POST request to URL: " + finalUrl);

        // Send POST request

        try {
            ResponseEntity<Void> response = restTemplate.exchange(finalUrl, HttpMethod.POST, requestEntity, Void.class);

        } catch (HttpClientErrorException e) {
            // Log the error and handle it gracefully
            System.out.println("Sending POST request to URL: " + e.getMessage());
            // Optionally, send to a DLQ or take other actions
        } catch (Exception e) {
            System.out.println("Sending POST request to URL: " + e.getMessage());
        }


        return;

    }

}
