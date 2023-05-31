package com.honda.olympus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.honda.olympus.vo.MessageVO;
import com.honda.olympus.vo.ResponseVO;

@Service
public class NotificationService {

	@Value("${notification.service.url}")
	private String notificationURI;

	public void generatesNotification(MessageVO message) {

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			RestTemplate restTemplate = new RestTemplate();

			HttpEntity<MessageVO> requestEntity = new HttpEntity<>(message, headers);

			ResponseEntity<ResponseVO> responseEntity = restTemplate.postForEntity(notificationURI, requestEntity,
					ResponseVO.class);

			System.out.println("Notification sent with Status Code: " + responseEntity.getStatusCode());
			System.out.println("Message: " + responseEntity.getBody().getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error calling Notification service");
		}

	}

}
