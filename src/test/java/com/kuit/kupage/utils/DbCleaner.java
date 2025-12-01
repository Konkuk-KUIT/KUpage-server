package com.kuit.kupage.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class DbCleaner {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void truncateAll() {
        em.flush();
        em.clear();

        @SuppressWarnings("unchecked")
        var tableNames = (java.util.List<String>) em.createNativeQuery("""
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = DATABASE()
                """)
                .getResultList();

        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        for (String tableName : tableNames) {
            if ("flyway_schema_history".equalsIgnoreCase(tableName)
                    || "schema_version".equalsIgnoreCase(tableName)) {
                continue;
            }
            em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }

        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}
