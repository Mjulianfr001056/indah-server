package com.indah.sandboxingserver.repository;

import com.indah.sandboxingserver.model.Perizinan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "izin", collectionResourceRel = "perizinan")
public interface PerizinanRepository extends JpaRepository<Perizinan, String>{
}
