package com.coop.onboarding.infrastructure.web.controller;

import com.coop.onboarding.application.user.service.AdminUserService;
import com.coop.onboarding.infrastructure.web.dto.AdminUserSummaryResponse;
import com.coop.onboarding.infrastructure.web.dto.CreateUserRequest;
import com.coop.onboarding.infrastructure.web.dto.UpdateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GESTOR', 'ADMIN', 'GESTOR')")
    public ResponseEntity<List<AdminUserSummaryResponse>> listMembers() {
        return ResponseEntity.ok(adminUserService.listAllMembers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GESTOR', 'ADMIN', 'GESTOR')")
    public ResponseEntity<AdminUserSummaryResponse> getMember(@PathVariable UUID id) {
        return ResponseEntity.ok(adminUserService.getMemberById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ADMIN')")
    public ResponseEntity<AdminUserSummaryResponse> createMember(@Valid @RequestBody CreateUserRequest request) {
        AdminUserSummaryResponse created = adminUserService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ADMIN')")
    public ResponseEntity<AdminUserSummaryResponse> updateMember(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(adminUserService.updateMember(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        adminUserService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
