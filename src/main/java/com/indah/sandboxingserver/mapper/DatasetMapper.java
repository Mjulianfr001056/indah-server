package com.indah.sandboxingserver.mapper;

import com.indah.sandboxingserver.model.KatalogData;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface DatasetMapper {
    static double[] mapToDoubleArray(Dataset<Row> dataset, String columnName) {
        Double[] result = dataset.select(columnName)
                .filter(functions.col(columnName).isNotNull())
                .as(Encoders.DOUBLE())
                .collectAsList()
                .toArray(new Double[0]);

        return Arrays.stream(result)
                .mapToDouble(Double::doubleValue)
                .toArray();
    }

    static JavaRDD<Vector> mapToNumericJavaRDDVector(Dataset<Row> dataset){
        String[] columns = dataset.columns();

        JavaRDD<Vector> vectorRDD = dataset.toJavaRDD().map(row -> {
            List<Number> values = new ArrayList<>();

            for (int i = 0; i < columns.length; i++) {
                Number value = row.isNullAt(i) ? 0 : row.getAs(i);
                values.add(value);
            }

            double[] valuesArray = new double[values.size()];
            for (int i = 0; i < values.size(); i++) {
                valuesArray[i] = values.get(i).doubleValue();
            }

            return Vectors.dense(valuesArray);
        });

        return vectorRDD;
    }

    static KatalogData mapToKatalogData(Row katalog){
        return KatalogData.builder()
                .id(katalog.getString(0))
                .judul(katalog.getString(1))
                .kategori(katalog.getString(2))
                .tahun(katalog.getInt(3))
                .build();
    }
}
