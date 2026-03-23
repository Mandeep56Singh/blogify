package com.mandeep.blogify.integrationTest.base;

import com.mandeep.blogify.integrationTest.config.TestContainersConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Base class for Integration Tests.
 * Uses a Non-Transactional strategy to ensure tests run against a real database state
 * without the "false positives" often caused by Spring's managed test transactions.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(TestContainersConfig.class)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    protected EntityManager entityManager;

    private TransactionTemplate transactionTemplate;

    /**
     * Initializes TransactionTemplate using the existing PlatformTransactionManager.
     * This avoids the need to manually define a TransactionTemplate bean in a configuration class.
     */
    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Wipes the database before every test.
     * Using direct JDBC truncation instead of @Transactional ensures that the
     * database is physically clean and prevents primary key sequence collisions.
     */
    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE posts, categories, users RESTART IDENTITY CASCADE");
    }

    /**
     * Persists an entity into the database within a short-lived transaction.
     * It flushes to disk and clears the Persistence Context (L1 Cache) immediately,
     * ensuring that subsequent service calls actually hit the database.
     * * @param entity The JPA entity to store.
     */
    protected <T> void persist(T entity) {
        transactionTemplate.executeWithoutResult(status -> {
            entityManager.persist(entity);
            entityManager.flush();
        });
        entityManager.clear();
    }
}