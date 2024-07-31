package com.smusoak.restapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_room")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "userList"})
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_chatroom",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "chatRooms"})
    private Set<User> users;

    @OneToOne
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private OpenGroupChat OpenGroupChat;

    @Getter
    private List<User> userList;
//    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
//    private List<Message> messageList = new ArrayList<>();
}
