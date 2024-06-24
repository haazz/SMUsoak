package com.smusoak.restapi.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@Service
public class FirebaseCloudMessageService {
    @Value("${firebase.key-path}")
    private String fcmKeyPath;
    @Value("${firebase.project-id}")
    private String projectId;


    // bean을 초기화 한 후 딱 한번만 실행
    @PostConstruct
    public void fcmInitialize() throws IOException {
        //Firebase 프로젝트 정보를 FireBaseOptions에 입력해준다.
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(fcmKeyPath).getInputStream()))
                .setProjectId(projectId)
                .build();

        //입력한 정보를 이용하여 initialze 해준다.
        FirebaseApp.initializeApp(options);
    }

    public void sendMessageByToken(String title, String body, Map<String, String> data, String token) throws FirebaseMessagingException {
        // FirebaseOptions에 초기화해둔 정보를 기반으로 메시지 전송
        FirebaseMessaging.getInstance().send(Message.builder()
                .putAllData(data)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(token)
                .build());

    }
}
