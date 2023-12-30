package com.indah.sandboxingserver.repository;

import com.indah.sandboxingserver.model.KatalogData;
import com.indah.sandboxingserver.model.Perizinan;
import com.indah.sandboxingserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "izin", collectionResourceRel = "perizinan")
public interface PerizinanRepository extends JpaRepository<Perizinan, String>{

    Optional<Perizinan> findByUserAndData(User user, KatalogData data);
}
