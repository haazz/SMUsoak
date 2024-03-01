//package com.smusoak.restapi.models;
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.DynamicInsert;
//
//import java.time.LocalDateTime;
//
//@Entity
//@DynamicInsert
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Message {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String message;
//    private LocalDateTime sendAt;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_mail")
//    private User sender;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_mail")
//    private User receiver;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "room_id")
//    private ChatRoom chatRoom;
//
//}
