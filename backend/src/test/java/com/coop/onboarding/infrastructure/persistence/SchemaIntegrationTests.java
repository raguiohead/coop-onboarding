package com.coop.onboarding.infrastructure.persistence;

import com.coop.onboarding.domain.model.UserRole;
import com.coop.onboarding.infrastructure.persistence.entity.UserEntity;
import com.coop.onboarding.infrastructure.persistence.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class SchemaIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("pgvector/pgvector:pg16"));

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve verificar que a migração Flyway V1 criou as tabelas e índices vetoriais HNSW")
    void shouldVerifyFlywayMigrationAndVectorStoreTable() {
        // Verifica existência da tabela vector_store
        Integer vectorStoreTableCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM information_schema.tables WHERE table_name = 'vector_store'",
                Integer.class
        );
        assertThat(vectorStoreTableCount).isEqualTo(1);

        // Verifica índice HNSW
        Integer hnswIndexCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM pg_indexes WHERE indexname = 'vector_store_hnsw_idx'",
                Integer.class
        );
        assertThat(hnswIndexCount).isEqualTo(1);

        // Salva e recupera um usuário para testar integridade JPA + DDL validate
        UserEntity user = UserEntity.builder()
                .keycloakId(UUID.randomUUID().toString())
                .name("Colaborador Teste")
                .email("teste@coop.com.br")
                .role(UserRole.COLABORADOR)
                .department("TI")
                .build();

        UserEntity saved = userRepository.save(user);
        assertThat(saved.getId()).isNotNull();

        Optional<UserEntity> found = userRepository.findByEmail("teste@coop.com.br");
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(UserRole.COLABORADOR);
    }
}
