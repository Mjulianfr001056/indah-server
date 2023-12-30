package com.indah.sandboxingserver;

import com.indah.sandboxingserver.db.DBManager;
import com.indah.sandboxingserver.mapper.DatasetMapper;
import com.indah.sandboxingserver.model.*;
import com.indah.sandboxingserver.repository.PerizinanRepository;
import com.indah.sandboxingserver.repository.UserRepository;
import lombok.var;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.TiesStrategy;
import org.apache.commons.math3.util.FastMath;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.stat.MultivariateStatisticalSummary;
import org.apache.spark.mllib.stat.Statistics;
import org.apache.spark.sql.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.apache.spark.sql.functions.col;

@SpringBootTest
class SandboxingServerApplicationTests {

    @Autowired
    private SparkSession sparkSession;

    @Autowired
    private DBManager dbManager;

    @Autowired
    private PerizinanRepository perizinanRepository;

    @Autowired
    private UserRepository userRepository;

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
        dbSummary.show();
        dbSummary.collectAsList();
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
        dbTest = dbTest.select("bbsebelum", "bbsesudah");

        double[] bbSebelum = DatasetMapper.mapToDoubleArray(dbTest, "bbsebelum");
        double[] bbSesudah = DatasetMapper.mapToDoubleArray(dbTest, "bbsesudah");

        double tStat = TestUtils.pairedT(bbSebelum, bbSesudah);
        double pValue = TestUtils.pairedTTest(bbSebelum, bbSesudah);

        System.out.println("T Statistics: " + tStat);
        System.out.println("P-Value: " + pValue);
    }

    @Test
    @DisplayName("Test uji T 1 sampel")
    void testTTest(){
        Dataset<Row> dbTest = dbManager.getTable("data_sampel4");
        dbTest = dbTest.select("ip1");

        double[] ip1 = DatasetMapper.mapToDoubleArray(dbTest, "ip1");
        int degreeOfFreedom = ip1.length - 1;

        TDistribution tDistribution = new TDistribution(degreeOfFreedom);


        double tStat = TestUtils.t(3.35, ip1);
        // Two-sided p-value
        double twoSidedPValue = 2 * tDistribution.cumulativeProbability(-Math.abs(tStat));

        // One-sided p-value for greater alternative
        double greaterPValue = tDistribution.cumulativeProbability(-tStat);

        // One-sided p-value for less alternative
        double lessPValue = tDistribution.cumulativeProbability(tStat);

        System.out.println("Two-Sided P-Value: " + twoSidedPValue);
        System.out.println("One-Sided P-Value (Greater): " + greaterPValue);
        System.out.println("One-Sided P-Value (Less): " + lessPValue);

        System.out.println("T Statistics: " + tStat);
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

    @Test
    @DisplayName("Test patch")
    void testPatch(){
        var requestId = "102";
        var newStatus = "PENDING";
        var tableName = "perizinan";

        perizinanRepository.findById(requestId)
                .ifPresent(perizinan -> {
                    perizinan.setStatus(StatusPerizinan.valueOf(newStatus));
                    perizinanRepository.save(perizinan);
                });
    }

    @Test
    @DisplayName("Test get metadata")
    public void getKeterangan() {
        var tableId = "SAMPEL2";
        var tableName = dbManager.getInDBTableNameFromId(tableId);

        tableName = tableName + "_metadata";
        var table = dbManager.getMetadataTable(tableName);

        var header = table.columns();
        var keterangan = table.select("keterangan");
        keterangan.show();

        Map<String, Object> response = new HashMap<>();

        response.put("header", header);
        response.put("keterangan", keterangan.toJSON().collectAsList());

        System.out.println(response.get("keterangan"));
    }

    @Test
    @DisplayName("Test One Way ANOVA dengan library ANOVA Test")
    public void anovastat() {
        // Your existing code
        Dataset<Row> dbTest = dbManager.getTable("data_sampel2");
        dbTest = dbTest.select("A", "B", "C");

        double[] prodiA = DatasetMapper.mapToDoubleArray(dbTest, "A");
        double[] prodiB = DatasetMapper.mapToDoubleArray(dbTest, "B");
        double[] prodiC = DatasetMapper.mapToDoubleArray(dbTest, "C");

        Collection<double[]> data = new ArrayList<>();
        data.add(prodiA);
        data.add(prodiB);
        data.add(prodiC);

        // Calculate additional statistics manually
        int numGroups = data.size();
        int numObservations = prodiA.length + prodiB.length + prodiC.length;

        // Degrees of Freedom Between (dfB)
        int dfBetween = numGroups - 1;

        // Degrees of Freedom Within (dfW)
        int dfWithin = numObservations - numGroups;

        // Degrees of Freedom Total (dfTotal)
        int dfTotal = numObservations - 1;

        // Grand Mean
        double grandMean = (sum(prodiA) + sum(prodiB) + sum(prodiC)) / numObservations;

        // Sum of Squares Between (MSB)
        double ssBetween = sumSquaredMeans(data, grandMean);

        // Sum of Squares Within (SSW)
        double ssWithin = sumSquaredDeviations(data);

        double msBetween = ssBetween / dfBetween;
        double msWithin = ssWithin / dfWithin;

        // Sum of Squares Total (SST)
        double ssTotal = ssBetween + ssWithin;

        // F-Value
        double fValue = msBetween / msWithin;

        // Print or use the calculated statistics as needed
        System.out.println("One-Way ANOVA Results:");
        System.out.printf("F-Value: %.4f%n", fValue);
        System.out.printf("Degrees of Freedom Between (dfB): %d%n", dfBetween);
        System.out.printf("Degrees of Freedom Within (dfW): %d%n", dfWithin);
        System.out.printf("Degrees of Freedom Total (dfTotal): %d%n", dfTotal);
        System.out.printf("Sum of Squares Between (SSB): %.4f%n", ssBetween);
        System.out.printf("Sum of Squares Within (SSW): %.4f%n", ssWithin);
        System.out.printf("Sum of Squares Total (SST): %.4f%n", ssTotal);
        System.out.printf("Mean Squares Between (MSB): %.4f%n", msBetween);
        System.out.printf("Mean Squares Within (MSW): %.4f%n", msWithin);

        // Rest of your code for hypothesis testing based on p-value
    }

    private static double sum(double[] array) {
        double sum = 0.0;
        for (double value : array) {
            sum += value;
        }
        return sum;
    }

    private static double sumSquaredMeans(Collection<double[]> data, double grandMean) {
        double sum = 0.0;
        for (double[] group : data) {
            double groupMean = sum(group) / group.length;
            sum += group.length * Math.pow(groupMean - grandMean, 2);
        }
        return sum;
    }

    private static double sumSquaredDeviations(Collection<double[]> data) {
        double sum = 0.0;
        for (double[] group : data) {
            double groupMean = sum(group) / group.length;
            for (double value : group) {
                sum += Math.pow(value - groupMean, 2);
            }
        }
        return sum;
    }

    @Test
    @DisplayName("Test mengambil baris")
    void testGetRow() {
        // Assuming dbManager is an instance of SparkSession or SparkContext
        Dataset<Row> dbTest = dbManager.getTable("data_sampel1");

        dbTest.show();

        // Collect all rows as a list
        List<Row> rows = dbTest.collectAsList();

        // Iterate through the list and print each row
        for (Row row : rows) {
            System.out.println(row);
        }
    }

    @Test
    @DisplayName("Test Wilcoxon Signed Rank Test")
    void testWilcoxon() {
        Dataset<Row> dbTest = dbManager.getTable("data_sampel6");
        dbTest = dbTest.select("Xa", "Xb");

        double[] before = DatasetMapper.mapToDoubleArray(dbTest, "Xa");
        double[] after = DatasetMapper.mapToDoubleArray(dbTest, "Xb");
        double[] diff = new double[before.length];
        double[] zAbs = new double[before.length];

        for(int i = 0; i < before.length; ++i) {
            diff[i] = after[i] - before[i];
            zAbs[i] = FastMath.abs(diff[i]);
        }

        NaturalRanking naturalRanking = new NaturalRanking(NaNStrategy.FIXED, TiesStrategy.AVERAGE);
        double[] ranks = naturalRanking.rank(zAbs);

        double Wplus = 0.0;

        int i;
        for(i = 0; i < diff.length; ++i) {
            if (diff[i] > 0.0) {
                Wplus += ranks[i];
            }
        }

        i = before.length;
        double Wminus = (double)(i * (i + 1)) / 2.0 - Wplus;
        double Wstat = FastMath.min(Wplus, Wminus);

        WilcoxonSignedRankTest wilcoxonSignedRankTest = new WilcoxonSignedRankTest();
        double pValue = wilcoxonSignedRankTest.wilcoxonSignedRankTest(before, after, false);

        System.out.println("W Statistics: " + Wstat);
        System.out.println("Asymp. Sig.: " + pValue);
    }

    @Test
    @DisplayName("Test Mann Whitney U Test")
    void testMannWhitney() {
        Dataset<Row> dbTest = dbManager.getTable("data_sampel7");
        dbTest = dbTest.select("skorY", "skorT");

        double[] col1 = DatasetMapper.mapToDoubleArray(dbTest, "skorY");
        double[] col2 = DatasetMapper.mapToDoubleArray(dbTest, "skorT");

        double[] z = new double[col1.length + col2.length];
        System.arraycopy(col1, 0, z, 0, col1.length);
        System.arraycopy(col2, 0, z, col1.length, col2.length);

        NaturalRanking naturalRanking = new NaturalRanking(NaNStrategy.FIXED, TiesStrategy.AVERAGE);
        double[] ranks = naturalRanking.rank(z);

        double sumRankX = 0.0;

        for(int i = 0; i < col1.length; ++i) {
            sumRankX += ranks[i];
        }

        double U1 = sumRankX - (double)((long)col1.length * (long)(col1.length + 1) / 2L);
        double U2 = (double)((long)col1.length * (long) col2.length) - U1;

        double Wstat = FastMath.min(U1, U2);

        MannWhitneyUTest mannWhitneyUTest = new MannWhitneyUTest();
        double pValue = mannWhitneyUTest.mannWhitneyUTest(col1, col2);
        System.out.println("W Statistics: " + Wstat);
        System.out.println("Asymp. Sig.: " + pValue);
    }

    @Test
    @DisplayName("Test get data based on izin")
    void testGetDataBasedOnIzin() {
        var tableName = "katalog_data";
        var columnNames = Arrays.asList("id", "judul");
        var userId = "102";
        Dataset<Row> katalog = dbManager.getTable(tableName, columnNames);

        Dataset<Row> izin = dbManager.getTable("perizinan", Arrays.asList("status", "id_data", "id_user"));
        izin = izin.filter(izin.col("id_user").equalTo(userId));

        Dataset<Row> joined = katalog.join(izin, katalog.col("id").equalTo(izin.col("id_data")), "left_outer");
        joined = joined.withColumn("status",
                functions.when(joined.col("status").equalTo("DISETUJUI"), "GRANTED").otherwise("PROHIBITED"))
                .drop("id_data", "id_user");

        joined.show();
    }

    @Test
    @DisplayName("Save perizinan")
    void testPerizinan(){
        var userId = "102";
        var tableId = "SAMPEL1";
        Optional<User> userOptional = userRepository.findById(userId);

        User user = userOptional.orElse(null);

        if (user == null) {
            return;
        }

        var katalog = dbManager.getTable("katalog_data");


        var filteredRows = katalog.filter(col("id").equalTo(tableId));

        Row matchingRow = filteredRows.first();

        KatalogData kd = DatasetMapper.mapToKatalogData(matchingRow);

        Optional<Perizinan> existingPerizinanOptional = perizinanRepository.findByUserAndData(user, kd);

        Perizinan existingPerizinan = existingPerizinanOptional.orElse(new Perizinan(user, kd));

        perizinanRepository.save(existingPerizinan);
    }
}
