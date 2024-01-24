package com.smusoak.restapi.models;

import com.smusoak.restapi.models.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
@Setter
public class Users implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private Long id;
	
	@Column(unique = true)
	private String mail;
	
	private String password;

	private LocalDateTime createdAt;
	
	private boolean mailAuth;

	private Integer age;

	private String major;

	private Character gender;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		// 관리자 및 권한 추가시 수정 필요
		if (mail.equals("admin")) {
			authorities.add(new SimpleGrantedAuthority(role.name()));
		}
		else {
			authorities.add(new SimpleGrantedAuthority(role.name()));
		}
		return authorities;
	}

	@Override
	public String getUsername() {
		// SSO가 필요해지면 중복 mail이 발생할 수 있으므로 PK(id)로 수정 필요
		return mail;
	}

	@Override
	public String getPassword() {
		return password;
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
