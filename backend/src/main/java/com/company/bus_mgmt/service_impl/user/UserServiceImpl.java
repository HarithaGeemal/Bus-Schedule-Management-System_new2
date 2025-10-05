package com.company.bus_mgmt.service_impl.user;

import com.company.bus_mgmt.domain.user.User;
import com.company.bus_mgmt.exception.NotFoundException;
import com.company.bus_mgmt.repository.user.UserRepository;
import com.company.bus_mgmt.service.user.UserService;
import com.company.bus_mgmt.web.dto.user.UserResponse;
import com.company.bus_mgmt.web.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository users;

    public UserServiceImpl(UserRepository users) {
        this.users = users;
    }

    @Override
    public Page<UserResponse> list(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return users.findAll(pageable).map(UserResponse::from);
        }
        return users.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, pageable)
                .map(UserResponse::from);
    }

    @Override
    public UserResponse get(Long id) {
        User u = users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return UserResponse.from(u);
    }

    @Override
    public UserResponse update(Long id, UserUpdateRequest req) {
        User u = users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (req.fullName() != null) u.setFullName(req.fullName());
        if (req.phone() != null) u.setPhone(req.phone());
        return UserResponse.from(users.save(u));
    }

    @Override
    public void deactivate(Long id) {
        User u = users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        u.setStatus("INACTIVE");
        users.save(u);
    }
}
