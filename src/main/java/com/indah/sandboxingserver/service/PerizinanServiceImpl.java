package com.indah.sandboxingserver.service;

import com.indah.sandboxingserver.db.DBManager;
import com.indah.sandboxingserver.mapper.DatasetMapper;
import com.indah.sandboxingserver.model.KatalogData;
import com.indah.sandboxingserver.model.Perizinan;
import com.indah.sandboxingserver.model.User;
import com.indah.sandboxingserver.repository.PerizinanRepository;
import com.indah.sandboxingserver.repository.UserRepository;
import lombok.var;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.apache.spark.sql.functions.col;

@Service
public class PerizinanServiceImpl implements PerizinanService{
    @Autowired
    UserRepository userRepository;

    @Autowired
    DBManager dbManager;

    @Autowired
    PerizinanRepository perizinanRepository;

    @Override
    public void savePerizinan(String userId, String tableId) {
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
