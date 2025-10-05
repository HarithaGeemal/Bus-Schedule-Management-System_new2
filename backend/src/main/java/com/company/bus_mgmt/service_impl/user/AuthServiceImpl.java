package com.company.bus_mgmt.service_impl.user;

import com.company.bus_mgmt.domain.user.Role;
import com.company.bus_mgmt.domain.user.User;
import com.company.bus_mgmt.exception.NotFoundException;
import com.company.bus_mgmt.repository.user.RoleRepository;
import com.company.bus_mgmt.repository.user.UserRepository;
import com.company.bus_mgmt.security.JwtTokenProvider;
import com.company.bus_mgmt.service.user.AuthService;
import com.company.bus_mgmt.web.dto.auth.LoginRequest;
import com.company.bus_mgmt.web.dto.auth.LoginResponse;
import com.company.bus_mgmt.web.dto.user.UserCreateRequest;
import com.company.bus_mgmt.web.dto.user.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider tokens;

    public AuthServiceImpl(UserRepository users, RoleRepository roles, PasswordEncoder encoder, JwtTokenProvider tokens) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.tokens = tokens;
    }

    @Override
    public UserResponse register(UserCreateRequest req) {
        User u = new User();
        u.setEmail(req.email().toLowerCase());
        u.setFullName(req.fullName());
        u.setPhone(req.phone());
        u.setPasswordHash(encoder.encode(req.password()));

        Set<Role> rset = new HashSet<>();
        for (String r : req.roles()) {
            Role role = roles.findByName(r).orElseThrow(() -> new NotFoundException("Role not found: " + r));
            rset.add(role);
        }
        u.getRoles().addAll(rset);
        users.save(u);
        return UserResponse.from(u);
    }

    @Override
    public LoginResponse login(LoginRequest req) {
        User u = users.findByEmail(req.email().toLowerCase())
                .orElseThrow(() -> new NotFoundException("Invalid credentials"));
        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            throw new NotFoundException("Invalid credentials");
        }
        List<String> roleNames = u.getRoles().stream().map(Role::getName).toList();
        String access = tokens.createAccessToken(u.getEmail(), Map.of("uid", u.getId(), "roles", roleNames));
        String refresh = tokens.createRefreshToken(u.getEmail());
        return LoginResponse.of(access, refresh, UserResponse.from(u));
    }

    @Override
    public LoginResponse refresh(String refreshToken) {
        var jws = tokens.parse(refreshToken);
        String email = jws.getBody().getSubject();
        User u = users.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        List<String> roleNames = u.getRoles().stream().map(Role::getName).toList();
        String access = tokens.createAccessToken(u.getEmail(), Map.of("uid", u.getId(), "roles", roleNames));
        return LoginResponse.of(access, refreshToken, UserResponse.from(u));
    }
}
