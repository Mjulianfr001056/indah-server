package com.indah.sandboxingserver.controller;

import com.indah.sandboxingserver.config.ServerResponse;
import com.indah.sandboxingserver.db.DBManager;
import lombok.var;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    @Autowired
    private DBManager dbManager;

    @GetMapping("/dashboard")
    public ServerResponse getColumn() {
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

        var resultList = perizinanTable.toJSON().collectAsList();

        response.put("raw", resultList);

        return new ServerResponse(response);
    }

    @GetMapping("/katalog")
    public ServerResponse getKatalog() {
        var katalog = dbManager.getTable("katalog_data");
        return new ServerResponse(katalog.toJSON().collectAsList());
    }

    @GetMapping("/users")
    public ServerResponse getUsers() {
        var users = dbManager.getTable("users");
        return new ServerResponse(users.toJSON().collectAsList());
    }
}
