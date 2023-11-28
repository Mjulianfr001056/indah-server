package com.indah.sandboxingserver;

import com.indah.sandboxingserver.db.DBManager;
import com.indah.sandboxingserver.mapper.DatasetMapper;
import com.indah.sandboxingserver.model.Role;
import com.indah.sandboxingserver.model.User;
import lombok.var;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.stat.Statistics;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class SandboxingServerApplicationTests {

    @Autowired
    private SparkSession sparkSession;

    @Autowired
    private DBManager dbManager;

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

    @Test
    @DisplayName("Test mengambil kolom dari tabel")
    void testColumnSelection() {
        Dataset<Row> dbTest = dbManager.getTable("data_sampel1", Arrays.asList("c2021", "c2022"));

        dbTest.show();
    }

    @Test
    @DisplayName("Test menghitung rata-rata kolom")
    void testAverage() {
        Dataset<Row> dbTest = dbManager.getTable("data_sampel1");
        dbTest = dbTest.select(dbTest.columns()[0], "c2021");

        Dataset<Row> dbSummary = dbTest.summary();
        System.out.println(dbSummary.toJSON().collectAsList());
    }

    @Test
    @DisplayName("Test uji statistik normalitas")
    void testNormality() {
        Dataset<Row> dbTest = dbManager.getTable("data_sampel1");
        var dbTest1 = dbTest.select("c2021");
        var dbTest2 = dbTest.select("c2022");


        // Extract the values from the DataFrame column
        List<Double> dataList1 = dbTest1.as(Encoders.DOUBLE()).collectAsList();
        List<Double> dataList2 = dbTest2.as(Encoders.DOUBLE()).collectAsList();

        // Convert List<Double> to double[]
        double[] dataArray1 = dataList1.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        double[] dataArray2 = dataList2.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        NormalDistribution normalDistribution = new NormalDistribution();
        double pValue = TestUtils.kolmogorovSmirnovTest(normalDistribution, dataArray2);
        System.out.println("Kolmogorov-Smirnov test p-value: " + pValue);
    }

    @Test
    @DisplayName("Test korelasi")
    void testCorr() {
        Dataset<Row> dbTest = dbManager.getTable("data_sampel5");
        dbTest = dbTest.select("Alpukat (Ton)", "Belimbing (Ton)", "Jambu Air (Ton)");
        var vectorRDD = DatasetMapper.mapToNumericJavaRDDVector(dbTest);

        Matrix correlationMatrix = Statistics.corr(vectorRDD.rdd(), "spearman");

        System.out.println("Correlation Matrix:\n" + correlationMatrix);

    }

    @Test
    @DisplayName("Test One Way ANOVA")
    void testAnovaOneWay(){
        Dataset<Row> dbTest = dbManager.getTable("data_sampel2");
        dbTest = dbTest.select("Prodi A", "Prodi B", "Prodi C");

        double[] prodiA = DatasetMapper.mapToDoubleArray(dbTest, "Prodi A");
        double[] prodiB = DatasetMapper.mapToDoubleArray(dbTest, "Prodi B");
        double[] prodiC = DatasetMapper.mapToDoubleArray(dbTest, "Prodi C");

        Collection<double[]> data = new ArrayList<>();
        data.add(prodiA);
        data.add(prodiB);
        data.add(prodiC);

        double pValue = TestUtils.oneWayAnovaPValue(data);
        double fValue = TestUtils.oneWayAnovaFValue(data);

        System.out.println("One-Way ANOVA Results:");
        System.out.printf("F-Value: %.4f%n", fValue);
        System.out.printf("P-Value: %.4f%n", pValue);

        if (pValue < 0.05) {
            System.out.println("Reject the null hypothesis: There are significant differences between groups.");
        } else {
            System.out.println("Fail to reject the null hypothesis: There are no significant differences between groups.");
        }
    }

    @Test
    @DisplayName("Test uji T 2 sampel berpasangan")
    void testPairedTTest(){
        Dataset<Row> dbTest = dbManager.getTable("data_sampel3");
        dbTest = dbTest.select("BB Sebelum", "BB Sesudah");

        double[] bbSebelum = DatasetMapper.mapToDoubleArray(dbTest, "BB Sebelum");
        double[] bbSesudah = DatasetMapper.mapToDoubleArray(dbTest, "BB Sesudah");

        double tStat = TestUtils.pairedT(bbSebelum, bbSesudah);
        double pValue = TestUtils.pairedTTest(bbSebelum, bbSesudah);

        System.out.println("T Statistics: " + tStat);
        System.out.println("P-Value: " + pValue);
    }

    @Test
    @DisplayName("Test create User")
    void testCreateUser(){
        User user = new User("1", "Indah", "user@stis.ac.id", Role.USER);
        assert (user != null);
    }

    @Test
    @DisplayName("Test dashboard User")
    void testDashboardUser(){
        Map<String, Object> response = new HashMap<>();

        Dataset<Row> perizinanTable = dbManager.getTable("perizinan");
        Dataset<Row> usersTable =
                dbManager.getTable("users", Arrays.asList("id", "nama"))
                        .withColumnRenamed("id", "id_user");

        Dataset<Row> dataTable =
                dbManager.getTable("katalog_data", Arrays.asList("id", "judul"))
                        .withColumnRenamed("id", "id_data");


        perizinanTable = perizinanTable
                .join(usersTable, perizinanTable.col("id_user").equalTo(usersTable.col("id_user")))
                .join(dataTable, perizinanTable.col("id_data").equalTo(dataTable.col("id_data")))
                .drop("id_user", "id_data");

        perizinanTable.show();

        List<Row> aggregate = perizinanTable.groupBy("status")
                .count()
                .collectAsList();

        for (Row row : aggregate) {
            String status = row.getString(0);
            long count = row.getLong(1);

            response.put(status, count);
        }

        Dataset<String> result =
                perizinanTable.map((MapFunction<Row, String>) row -> row.prettyJson(), Encoders.STRING());

        List<String> resultList = result.collectAsList();

        response.put("raw", resultList);
        System.out.println(response);
    }
}
