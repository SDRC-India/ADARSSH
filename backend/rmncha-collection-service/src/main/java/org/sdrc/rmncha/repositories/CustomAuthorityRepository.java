package org.sdrc.rmncha.repositories;

import java.util.List;

import org.sdrc.usermgmt.mongodb.domain.Authority;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomAuthorityRepository extends MongoRepository<Authority, String> {

	List<Authority> findByAuthorityIn(List<String> authorities);

	Authority findByAuthority(String string);
}
