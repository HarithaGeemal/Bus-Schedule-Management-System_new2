package com.company.bus_mgmt.web.controller.users;

import com.company.bus_mgmt.service.user.UserService;
import com.company.bus_mgmt.web.dto.user.UserResponse;
import com.company.bus_mgmt.web.dto.user.UserUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @GetMapping
    public Page<UserResponse> list(@RequestParam(required=false) String q,
                                   @RequestParam(defaultValue="1") int page,
                                   @RequestParam(defaultValue="20") int size,
                                   @RequestParam(required=false) String sort) {
        PageRequest pr = PageRequest.of(Math.max(0, page-1), size,
                sort == null ? Sort.unsorted() : Sort.by(sort.contains(",") ?
                        Sort.Order.by(sort.split(",")[0]).with("desc".equalsIgnoreCase(sort.split(",")[1]) ? Sort.Direction.DESC : Sort.Direction.ASC)
                        : Sort.Order.by(sort)));
        return users.list(q, pr);
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id){ return users.get(id); }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest req){
        return users.update(id, req);
    }

    @PatchMapping("/{id}:deactivate")
    public void deactivate(@PathVariable Long id){ users.deactivate(id); }
}
