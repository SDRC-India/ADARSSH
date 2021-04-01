package org.sdrc.rmncha.repositories;

import java.util.List;

import org.sdrc.usermgmt.mongodb.domain.Account;
import org.sdrc.usermgmt.mongodb.repository.AccountRepository;
import org.springframework.stereotype.Repository;

@Repository("customAccountRepository")
public interface CustomAccountRepository extends AccountRepository {
	
	List<Account> findByIdIn(List<String> ids);
	
	List<Account> findByExpiredFalse();

	Account findByEmailAndExpiredFalse(String email);

}
