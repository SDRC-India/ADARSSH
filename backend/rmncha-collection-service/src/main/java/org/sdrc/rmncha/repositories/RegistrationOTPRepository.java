package org.sdrc.rmncha.repositories;

import org.sdrc.rmncha.domain.RegistrationOTP;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RegistrationOTPRepository extends MongoRepository<RegistrationOTP, String> {

	RegistrationOTP findByEmailIdAndIsActiveTrue(String email);

	RegistrationOTP findByEmailIdAndVarificationCodeAndIsActiveTrue(String email, Integer varificationCode);

}
