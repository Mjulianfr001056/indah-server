package com.indah.sandboxingserver.service;

import com.indah.sandboxingserver.db.DBManager;
import com.indah.sandboxingserver.entity.ANOVAUtil;
import com.indah.sandboxingserver.entity.TTestStat;
import com.indah.sandboxingserver.mapper.DatasetMapper;
import com.indah.sandboxingserver.request.TTestRequest;
import com.indah.sandboxingserver.response.MannWhitneyTestResponse;
import com.indah.sandboxingserver.response.WilcoxonTestResponse;
import lombok.var;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.TiesStrategy;
import org.apache.commons.math3.util.FastMath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.indah.sandboxingserver.mapper.DatasetMapper.mapToDoubleArray;

@Service
public class InferenceServiceImpl implements InferenceService {

    @Autowired
    private DBManager dbManager;

    @Override
    public ANOVAUtil.ANOVAStat getANOVA(String tableId, List<String> columnName) {
        var tableName = dbManager.getInDBTableNameFromId(tableId);
        var table = dbManager.getTable(tableName, columnName);

        ANOVAUtil anovaUtil = new ANOVAUtil();

        for (String column : table.columns()) {
            double[] columnData = mapToDoubleArray(table, column);
            anovaUtil.addData(columnData);
        }

        return anovaUtil.calculate();
    }

    @Override
    public TTestStat tTest(TTestRequest request) {
        var tableName = dbManager.getInDBTableNameFromId(request.getTableId());
        var table = dbManager.getTable(tableName, Collections.singletonList(request.getColumnNames()));

        double[] columnData = mapToDoubleArray(table, request.getColumnNames());
        double mu = request.getMu();
        double df = columnData.length - 1;
        TTestRequest.TTestAlternative alternative = request.getAlternative();

        TDistribution tDistribution = new TDistribution(df);

        SummaryStatistics summaryStatistics = new SummaryStatistics();
        Arrays.stream(columnData).forEach(summaryStatistics::addValue);

        double tStat = TestUtils.t(mu, columnData);
        double pValue = 0.0f;
        double sd = summaryStatistics.getStandardDeviation();
        double sampleMean = summaryStatistics.getMean();
        double ci95Low = 0.0f;
        double ci95High = 0.0f;
        double marginOfError;

        switch (alternative) {
            case LESS:
                pValue = tDistribution.cumulativeProbability(tStat);
                marginOfError = tDistribution.inverseCumulativeProbability(0.95) * (sd / Math.sqrt(columnData.length));
                ci95Low = Double.NEGATIVE_INFINITY;
                ci95High = sampleMean + marginOfError;
                break;
            case GREATER:
                pValue = tDistribution.cumulativeProbability(-tStat);
                marginOfError = tDistribution.inverseCumulativeProbability(0.95) * (sd / Math.sqrt(columnData.length));
                ci95Low = sampleMean - marginOfError;
                ci95High = Double.POSITIVE_INFINITY;
                break;
            case TWO_SIDED:
                pValue = TestUtils.tTest(mu, columnData);
                marginOfError = tDistribution.inverseCumulativeProbability(0.975) * (sd / Math.sqrt(columnData.length));
                ci95Low = sampleMean - marginOfError;
                ci95High = sampleMean + marginOfError;
                break;
        }

        return TTestStat.builder()
                .t(tStat)
                .p(pValue)
                .sd(sd)
                .df(df)
                .mean(sampleMean)
                .ci95Low(ci95Low)
                .ci95High(ci95High)
                .build();
    }

    @Override
    public TTestStat pairedTTest(String tableId, String columnName1, String columnName2) {
        var tableName = dbManager.getInDBTableNameFromId(tableId);
        var table = dbManager.getTable(tableName, Arrays.asList(columnName1, columnName2));

        double[] columnData1 = mapToDoubleArray(table, columnName1);
        double[] columnData2 = mapToDoubleArray(table, columnName2);

        double tStat = TestUtils.pairedT(columnData1, columnData2);
        double pValue = TestUtils.pairedTTest(columnData1, columnData2);
        int df = columnData1.length - 1;

        SummaryStatistics summaryStatistics = new SummaryStatistics();
        TDistribution tDistribution = new TDistribution(df);

        for(int i = 0; i < columnData1.length; i++) {
            summaryStatistics.addValue(columnData1[i] - columnData2[i]);
        }

        double mean = summaryStatistics.getMean();
        double sd = summaryStatistics.getStandardDeviation();

        double marginOfError = tDistribution.inverseCumulativeProbability(0.975) * (sd / Math.sqrt(columnData1.length));
        double ci95Low = mean - marginOfError;
        double ci95High = mean + marginOfError;

        return TTestStat.builder()
                .t(tStat)
                .p(pValue)
                .sd(sd)
                .df(df)
                .mean(mean)
                .ci95Low(ci95Low)
                .ci95High(ci95High)
                .build();
    }

    @Override
    public WilcoxonTestResponse wilcoxonTest(String tableId, String columnName1, String columnName2) {
        var tableName = dbManager.getInDBTableNameFromId(tableId);
        var table = dbManager.getTable(tableName, Arrays.asList(columnName1, columnName2));

        double[] before = DatasetMapper.mapToDoubleArray(table, columnName1);
        double[] after = DatasetMapper.mapToDoubleArray(table, columnName2);
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

        return WilcoxonTestResponse.builder()
                .V(Wstat)
                .pValue(pValue)
                .build();
    }

    @Override
    public MannWhitneyTestResponse mannWhitneyTest(String tableId, String columnName1, String columnName2) {
        var tableName = dbManager.getInDBTableNameFromId(tableId);
        var table = dbManager.getTable(tableName, Arrays.asList(columnName1, columnName2));

        double[] col1 = DatasetMapper.mapToDoubleArray(table, columnName1);
        double[] col2 = DatasetMapper.mapToDoubleArray(table, columnName2);

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

        return MannWhitneyTestResponse.builder()
                .W(Wstat)
                .pValue(pValue)
                .build();
    }
}
