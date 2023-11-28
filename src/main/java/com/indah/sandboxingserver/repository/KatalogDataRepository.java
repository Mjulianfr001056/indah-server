package com.indah.sandboxingserver.repository;

import com.indah.sandboxingserver.model.KatalogData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "data", collectionResourceRel = "katalog_data")
public interface KatalogDataRepository extends JpaRepository<KatalogData, String> {
}
