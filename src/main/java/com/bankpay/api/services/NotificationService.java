package com.bankpay.api.services;

import com.bankpay.api.domain.user.User;
import com.bankpay.api.dtos.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {
    @Autowired
    private RestTemplate restTemplate;

    public void sendNotification(User user, String message) {
        try {
            String email = user.getEmail();
            NotificationDto notificationRequest = new NotificationDto(email, message);

            ResponseEntity<String> notificationResponse = restTemplate.postForEntity("https://util.devi.tools/api/v1/notify", notificationRequest, String.class);

            if (notificationResponse.getStatusCode() != HttpStatus.OK) {
                System.out.println("Serviço de notificação retornou status diferente de OK");
            }
        } catch (Exception e) {
            System.out.println("Serviço de notificação indisponível: " + e.getMessage());
        }
    }
}