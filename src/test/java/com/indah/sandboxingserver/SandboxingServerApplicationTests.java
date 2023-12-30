package com.indah.sandboxingserver;

import com.indah.sandboxingserver.db.DBManager;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SandboxingServerApplicationTests {

    @Autowired
    private SparkSession sparkSession;

    @Autowired
    private DBManager dbManager;

    @Test
    void contextLoads() {

    }

    @Test
    @DisplayName("Test koneksi ke SparkSession")
    void testSparkSessionConnection() {
        assert (sparkSession != null);
    }

    @Test
    @DisplayName("Test koneksi ke database")
    void testDBConnection() {
        assert (dbManager != null);
    }
}
