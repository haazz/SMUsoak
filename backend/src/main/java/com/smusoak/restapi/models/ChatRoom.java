//package com.smusoak.restapi.models;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Table(name = "chat_room")
//public class ChatRoom {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_mail")
//    private List<User> users;
//
//    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
//    private List<Message> messageList = new ArrayList<>();
//}
