package com.smusoak.restapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "userDetail"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String mail;
    private String password;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String fcmToken;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_detail_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserDetail userDetail;

    @ManyToMany(mappedBy = "users")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<ChatRoom> chatRooms;

    @OneToMany(mappedBy = "creator")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<OpenChat> openChats;

    @OneToMany(mappedBy = "creator")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<OpenGroupChat> openGroupChats;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<MatchingInfo> matchingInfos;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        // SSO가 필요해지면 중복 mail이 발생할 수 있으므로 PK(id)로 수정 필요
        return mail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
