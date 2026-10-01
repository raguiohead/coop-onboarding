package com.coop.onboarding.application.user.service;

import com.coop.onboarding.domain.model.UserRole;
import com.coop.onboarding.infrastructure.iam.KeycloakAdminService;
import com.coop.onboarding.infrastructure.persistence.entity.DepartmentEntity;
import com.coop.onboarding.infrastructure.persistence.entity.UserEntity;
import com.coop.onboarding.infrastructure.persistence.entity.UserProfileEntity;
import com.coop.onboarding.infrastructure.persistence.repository.DepartmentRepository;
import com.coop.onboarding.infrastructure.persistence.repository.EnrollmentRepository;
import com.coop.onboarding.infrastructure.persistence.repository.UserProfileRepository;
import com.coop.onboarding.infrastructure.persistence.repository.UserRepository;
import com.coop.onboarding.infrastructure.web.dto.AdminUserSummaryResponse;
import com.coop.onboarding.infrastructure.web.dto.CreateUserRequest;
import com.coop.onboarding.infrastructure.web.dto.UpdateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AdminUserService {

    private static final Logger log = LoggerFactory.getLogger(AdminUserService.class);

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final KeycloakAdminService keycloakAdminService;

    public AdminUserService(
            UserRepository userRepository,
            UserProfileRepository userProfileRepository,
            DepartmentRepository departmentRepository,
            EnrollmentRepository enrollmentRepository,
            KeycloakAdminService keycloakAdminService
    ) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.departmentRepository = departmentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.keycloakAdminService = keycloakAdminService;
    }

    @Transactional(readOnly = true)
    public List<AdminUserSummaryResponse> listAllMembers() {
        return userRepository.findAll().stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminUserSummaryResponse getMemberById(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com id: " + userId));
        return toSummaryResponse(user);
    }

    public AdminUserSummaryResponse createMember(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com o e-mail: " + request.email());
        }

        String username = request.email().split("@")[0].toLowerCase().trim();
        String password = (request.password() != null && !request.password().isBlank())
                ? request.password()
                : "Coop@" + (100 + (int)(Math.random() * 900));

        // 1. Provisiona no Keycloak IAM
        String keycloakId = keycloakAdminService.createKeycloakUser(
                username,
                request.email(),
                request.name(),
                password,
                request.role()
        );

        // 2. Persiste na tabela public.users
        UserEntity user = UserEntity.builder()
                .keycloakId(keycloakId)
                .name(request.name())
                .email(request.email())
                .role(request.role())
                .department(request.department() != null && !request.department().isBlank()
                        ? request.department()
                        : "Atendimento & Cooperados")
                .build();
        user = userRepository.save(user);

        // 3. Resolve departamento e persiste em identity.user_profiles
        UUID departmentId = resolveDepartmentId(user.getDepartment());
        String jobTitle = (request.jobTitle() != null && !request.jobTitle().isBlank())
                ? request.jobTitle()
                : resolveDefaultJobTitle(request.role());

        UserProfileEntity profile = UserProfileEntity.builder()
                .userId(user.getId())
                .departmentId(departmentId)
                .jobTitle(jobTitle)
                .phone(request.phone())
                .bio(request.bio() != null ? request.bio() : "Colaborador da cooperativa cadastrado pelo Administrador.")
                .onboardingStatus(request.role() == UserRole.COLABORADOR ? "IN_PROGRESS" : "COMPLETED")
                .build();
        userProfileRepository.save(profile);

        log.info("Usuário criado e sincronizado com sucesso. ID: {}, KeycloakId: {}, Email: {}", user.getId(), keycloakId, user.getEmail());
        return toSummaryResponse(user);
    }

    public AdminUserSummaryResponse updateMember(UUID userId, UpdateUserRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com id: " + userId));

        // 1. Atualiza no Keycloak IAM
        keycloakAdminService.updateKeycloakUser(
                user.getKeycloakId(),
                request.email(),
                request.name(),
                request.role(),
                request.password(),
                request.enabled()
        );

        // 2. Atualiza em public.users
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());
        if (request.department() != null && !request.department().isBlank()) {
            user.setDepartment(request.department());
        }
        user = userRepository.save(user);

        // 3. Atualiza em identity.user_profiles
        Optional<UserProfileEntity> profileOpt = userProfileRepository.findByUserId(user.getId());
        UserProfileEntity profile = profileOpt.orElseGet(() -> UserProfileEntity.builder().userId(userId).build());

        if (request.jobTitle() != null && !request.jobTitle().isBlank()) {
            profile.setJobTitle(request.jobTitle());
        }
        if (request.phone() != null) {
            profile.setPhone(request.phone());
        }
        if (request.bio() != null) {
            profile.setBio(request.bio());
        }
        if (user.getDepartment() != null) {
            profile.setDepartmentId(resolveDepartmentId(user.getDepartment()));
        }
        if (profile.getOnboardingStatus() == null) {
            profile.setOnboardingStatus(user.getRole() == UserRole.COLABORADOR ? "IN_PROGRESS" : "COMPLETED");
        }
        userProfileRepository.save(profile);

        log.info("Usuário atualizado com sucesso. ID: {}, Email: {}", user.getId(), user.getEmail());
        return toSummaryResponse(user);
    }

    public void deleteMember(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com id: " + userId));

        String keycloakId = user.getKeycloakId();

        // 1. Exclui do Keycloak IAM
        if (keycloakId != null && !keycloakId.isBlank()) {
            keycloakAdminService.deleteKeycloakUser(keycloakId);
        }

        // 2. Exclui do PostgreSQL
        userProfileRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);

        log.info("Usuário {} expurgado com sucesso do Keycloak e PostgreSQL.", userId);
    }

    private AdminUserSummaryResponse toSummaryResponse(UserEntity user) {
        Optional<UserProfileEntity> profileOpt = userProfileRepository.findByUserId(user.getId());

        String jobTitle = profileOpt.map(UserProfileEntity::getJobTitle).orElseGet(() -> resolveDefaultJobTitle(user.getRole()));
        String phone = profileOpt.map(UserProfileEntity::getPhone).orElse(null);
        String bio = profileOpt.map(UserProfileEntity::getBio).orElse(null);
        String onboardingStatus = profileOpt.map(UserProfileEntity::getOnboardingStatus)
                .orElse(user.getRole() == UserRole.COLABORADOR ? "IN_PROGRESS" : "COMPLETED");

        int completed = user.getRole() == UserRole.COLABORADOR ? 0 : 6;
        int total = 6;
        int progressPercent = user.getRole() == UserRole.COLABORADOR ? 0 : 100;

        return new AdminUserSummaryResponse(
                user.getId(),
                user.getKeycloakId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getDepartment(),
                jobTitle,
                phone,
                bio,
                onboardingStatus,
                completed,
                total,
                progressPercent,
                true,
                user.getCreatedAt()
        );
    }

    private UUID resolveDepartmentId(String departmentName) {
        if (departmentName == null || departmentName.isBlank()) {
            return null;
        }
        return departmentRepository.findByNameContainingIgnoreCase(departmentName)
                .map(DepartmentEntity::getId)
                .orElse(null);
    }

    private String resolveDefaultJobTitle(UserRole role) {
        if (role == UserRole.ADMIN) return "Administrador de TI & Governança";
        if (role == UserRole.GESTOR) return "Gestor de Desenvolvimento Humano";
        return "Assistente de Atendimento Cooperativo";
    }
}
