package org.sdrc.rmncha.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.sdrc.rmncha.repositories.CustomAccountRepository;
import org.sdrc.usermgmt.core.util.IUserManagementHandler;
import org.sdrc.usermgmt.exception.EmailMisMatchException;
import org.sdrc.usermgmt.exception.InvalidPropertyException;
import org.sdrc.usermgmt.exception.PasswordMismatchException;
import org.sdrc.usermgmt.exception.UserAlreadyExistException;
import org.sdrc.usermgmt.model.AuthorityControlType;
import org.sdrc.usermgmt.model.ForgotPasswordModel;
import org.sdrc.usermgmt.mongodb.domain.Account;
import org.sdrc.usermgmt.mongodb.domain.AssignedDesignations;
import org.sdrc.usermgmt.mongodb.domain.Designation;
import org.sdrc.usermgmt.mongodb.repository.AuthorityRepository;
import org.sdrc.usermgmt.mongodb.repository.DesignationRepository;
import org.sdrc.usermgmt.service.MongoUserManagementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;


@Primary
@Component
@Slf4j
public class CreateUser  extends MongoUserManagementServiceImpl{
	
	@Autowired
	@Qualifier("customAccountRepository")
	private CustomAccountRepository customAccountRepository;

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Autowired(required = false)
	@Qualifier("mongoDesignationRepository")
	private DesignationRepository designationRepository;

	@Autowired(required = false)
	private IUserManagementHandler iuserManagementHandler;

	@Autowired(required = false)
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired(required = false)
	@Qualifier("mongoAuthorityRepository")
	private AuthorityRepository authorityRepository;
	
	@Override
	public ResponseEntity<String> createUser(Map<String, Object> map, Principal p) {

		Gson gson = new Gson();

		if (map.get("userName") == null || map.get("userName").toString().isEmpty())
			throw new RuntimeException("key : userName not found in map");

		if (map.get("designationIds") == null || map.get("designationIds").toString().isEmpty())
			throw new RuntimeException("key : designationIds not found in map");

		if (map.get("password") == null || map.get("password").toString().isEmpty())
			throw new RuntimeException("key : password not found in map");

		Account user = customAccountRepository.findByUserName(map.get("userName").toString());

		if (user != null) {

			throw new UserAlreadyExistException(configurableEnvironment.getProperty("username.duplicate.error"));

		}

		Account account = new Account();

		account.setUserName(map.get("userName").toString());
		account.setPassword(bCryptPasswordEncoder.encode(map.get("password").toString()));

		if (map.get("email") != null && !map.get("email").toString().isEmpty()) {
			Account acc = customAccountRepository.findByEmailAndExpiredFalse(map.get("email").toString());
			if (acc != null)
				throw new UserAlreadyExistException(configurableEnvironment.getProperty("email.duplicate.error"));
			account.setEmail(map.get("email").toString().toLowerCase());
		}

		List<String> designationIds = (List<String>) map.get("designationIds");

		List<Designation> designations = designationRepository.findByIdIn(designationIds);

		// check whether the user wanted to create admin user, if yes than does
		// user set the property 'allow.admin.creation' = true
		if ((!configurableEnvironment.containsProperty("allow.admin.creation"))
				|| configurableEnvironment.getProperty("allow.admin.creation").equals("false")) {
			designations.forEach(desgs -> {
				if (desgs.getName().equals("ADMIN")) {
					throw new RuntimeException("you do not have permission to create admin user!");
				}
			});
		}

		// setting multiple AssignedDesignations in account
		List<AssignedDesignations> assDesgList = new ArrayList<>();
		designations.forEach(d -> {

			AssignedDesignations assignedDesignations = new AssignedDesignations();
			assignedDesignations.setDesignationIds(d.getId());
			assDesgList.add(assignedDesignations);
		});

		account.setAssignedDesignations(assDesgList);

		/**
		 * if authorityIds present in the map than set its value in account
		 */
		if (map.get("authorityIds") != null && !map.get("authorityIds").toString().isEmpty()) {

			account.setAuthorityIds((List<String>) map.get("authorityIds"));

		}

		if (map.get("authority_control_type") != null && !map.get("authority_control_type").toString().isEmpty()) {

			if (!(map.get("authority_control_type").toString().equals("hybrid")
					|| map.get("authority_control_type").toString().equals("designation")
					|| map.get("authority_control_type").toString().equals("authority"))) {
				throw new java.lang.IllegalArgumentException(
						"invalid authority control type provided in api.allowed arguments are hybrid,authority,designation");
			}

			String controlType = map.get("authority_control_type").toString();
			account.setAuthorityControlType(controlType.equals("authority") ? AuthorityControlType.AUTHORITY
					: controlType.equals("designation") ? AuthorityControlType.DESIGNATION
							: AuthorityControlType.HYBRID);
			;

		} else {

			account.setAuthorityControlType(AuthorityControlType.DESIGNATION);
		}

		iuserManagementHandler.saveAccountDetails(map, account);

		return new ResponseEntity<String>(gson.toJson(configurableEnvironment.getProperty("user.create.success")),
				HttpStatus.OK);
	}
	
	
	@Override
	@Transactional
	public ResponseEntity<String> updateUser(Map<String, Object> updateUserMap, Principal principal) {

		/**
		 * checking if account-id exist
		 */
		if (updateUserMap.get("id") == null || updateUserMap.get("id").toString().isEmpty()) {
			throw new RuntimeException("key : id not found in updateUserMap");
		}

		Account account = customAccountRepository.findOne(updateUserMap.get("id").toString());
		if (account == null) {
			throw new EntityNotFoundException("Cannot find such user with id:" + updateUserMap.get("id").toString());
		}

		/**
		 * checking if email-id already exist
		 */
		if (updateUserMap.get("emailId") != null && !updateUserMap.get("emailId").toString().isEmpty()) {

			Account accountByEmail = customAccountRepository.findByEmailAndExpiredFalse(updateUserMap.get("emailId").toString());
			if (accountByEmail != null && !accountByEmail.getId().equals(account.getId()))
				throw new EmailMisMatchException(configurableEnvironment.getProperty("email.duplicate.error"));
		}

		Gson gson = new Gson();

		try {
			iuserManagementHandler.updateAccountDetails(updateUserMap, account, principal);
			log.info("Action : update-user :  user information updated successfully  for user : {}",
					account.getUserName());
			return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("user.update.success")),
					HttpStatus.OK);

		} catch (Exception e) {
			log.error("Action : update user : error : ", e);
			throw new RuntimeException(e);
		}

	}
	
	@Override
	@Transactional
	public ResponseEntity<String> forgotPassword(ForgotPasswordModel forgotPasswordModel) {
		/**
		 * using it to parse string as json
		 */
		Gson gson = new Gson();
		
		Account user = null;
		
		if(forgotPasswordModel.getValueType()==null || forgotPasswordModel.getValueType().equals("email")){
			user = customAccountRepository.findByEmailAndExpiredFalse(forgotPasswordModel.getEmailId());
		}else if(forgotPasswordModel.getValueType()==null || forgotPasswordModel.getValueType().equals("uName")){
			user = customAccountRepository.findByUserName(forgotPasswordModel.getEmailId());
		}else{
			throw new InvalidPropertyException("Invalid valueType property");
		}
		
		try {

			/**
			 * validating otp entered by user is correct or not
			 */
			ResponseEntity<String> validateOtpMess = validateOtp(forgotPasswordModel.getEmailId(),
					forgotPasswordModel.getOtp(),forgotPasswordModel.getValueType());

			if (validateOtpMess.getStatusCodeValue() == 200) {

				/**
				 * check if new password is equal to confirm password
				 */
				if (!forgotPasswordModel.getNewPassword().equals(forgotPasswordModel.getConfirmPassword())) {
					log.error("Action : change-password  : error while updating password! : message : ",
							configurableEnvironment.getProperty("password.not.matching"));
					throw new PasswordMismatchException(
							configurableEnvironment.getProperty("new.password.confirm.password.not.matching"));
				}

				user.setPassword(bCryptPasswordEncoder.encode(forgotPasswordModel.getNewPassword()));
				customAccountRepository.save(user);
				log.info("Action : forgot-password  message :: password updated successfull for user {}",
						user.getUserName());
				return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("password.forgot.success")),
						HttpStatus.OK);
			} else {
				return validateOtpMess;
			}
		} catch (Exception e) {
			log.error("Action : forgot-password By {}: error in forget password with payload {} ",
					user.getUserName(), forgotPasswordModel, e);
			return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("password.reset.failure")),
					HttpStatus.CONFLICT);

		}
	}


}
