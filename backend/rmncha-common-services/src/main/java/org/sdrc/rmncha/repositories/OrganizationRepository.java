package org.sdrc.rmncha.repositories;

import org.sdrc.rmncha.domain.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends MongoRepository<Organization, String> {

	Organization findByOrganizationId(Integer organizationId);
}
