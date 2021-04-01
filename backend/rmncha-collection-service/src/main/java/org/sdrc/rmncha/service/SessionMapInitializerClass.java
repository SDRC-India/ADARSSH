package org.sdrc.rmncha.service;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmncha.domain.UserDetails;
import org.sdrc.rmncha.repositories.AreaLevelRepository;
import org.sdrc.rmncha.repositories.AreaRepository;
import org.sdrc.rmncha.repositories.CustomAuthorityRepository;
import org.sdrc.rmncha.repositories.CustomTypeDetailRepository;
import org.sdrc.rmncha.util.Gender;
import org.sdrc.usermgmt.core.util.IUserManagementHandler;
import org.sdrc.usermgmt.mongodb.domain.Account;
import org.sdrc.usermgmt.mongodb.domain.AssignedDesignations;
import org.sdrc.usermgmt.mongodb.domain.Authority;
import org.sdrc.usermgmt.mongodb.domain.Designation;
import org.sdrc.usermgmt.mongodb.repository.AccountRepository;
import org.sdrc.usermgmt.mongodb.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sarita Panigrahi Created Date: 31-12-2018
 *
 */
@Service
@Slf4j
public class SessionMapInitializerClass implements IUserManagementHandler {

	@Autowired
	private AreaRepository areaRepositoy;

	@Autowired
	@Qualifier("mongoAccountRepository")
	private AccountRepository accountRepository;

	@Autowired
	private CustomAuthorityRepository customAuthorityRepository;
	
	@Autowired
	private AreaLevelRepository mongoAreaLevelRepository;
	
	@Autowired
	private ConfigurableEnvironment configurableEnvironment;
	
	@Autowired(required = false)
	@Qualifier("mongoDesignationRepository")
	private DesignationRepository designationRepository;
	
	@Autowired
	private CustomTypeDetailRepository customtypeDetailRepository;

	//when user logsin, put extra information here (if reqd)
	@Override
	public Map<String, Object> sessionMap(Object account) {
		Account acc = (Account) account;
		/*
		 * setting extra parameters to be sent while user logged in.
		 */
		Map<String, Object> sessionMap = new HashMap<>();

		// get all the area associated with the user
		List<Area> areas = areaRepositoy.findByAreaIdIn(acc.getMappedAreaIds());
		
		Map<String, String> devPartnerIdNameMap = new HashMap<>();
		List<TypeDetail> devPartners = customtypeDetailRepository.findAll();

		devPartners.stream().forEach(devp -> {
			devPartnerIdNameMap.put(devp.getId(), devp.getName());
		});

		sessionMap.put("area", areas);
		UserDetails userDetails = (UserDetails) acc.getUserDetails();
		sessionMap.put("organization",userDetails.getOrganization());
		sessionMap.put("orgName",userDetails.getOrgName());
		sessionMap.put("firstName", userDetails.getFirstName());
		sessionMap.put("middleName", userDetails.getMiddleName());
		sessionMap.put("lastName", userDetails.getLastName());
		
		sessionMap.put("gender", userDetails.getGender());
		sessionMap.put("mobNo", userDetails.getMobNo());
		sessionMap.put("areaLevel", userDetails.getAreaLevel().getAreaLevelName());
		sessionMap.put("roleId", userDetails.getAreaLevel().getAreaLevelId());
		sessionMap.put("dob", userDetails.getDob());
		sessionMap.put("idProType", userDetails.getIdProType());
		sessionMap.put("devPartnerId", userDetails.getDevPartner() );
		sessionMap.put("devPartner", (userDetails.getDevPartner() == null ? ""
				: devPartnerIdNameMap.get(userDetails.getDevPartner())));
		sessionMap.put("othersDevPartner", userDetails.getOthersDevPartner());
		sessionMap.put("idProType", userDetails.getIdProType());
		sessionMap.put("idProTypeName", (userDetails.getIdProType() == null ? ""
				: devPartnerIdNameMap.get(userDetails.getIdProType())));
		sessionMap.put("idProofFile", userDetails.getIdProofFile());
		sessionMap.put("othersIdProof", userDetails.getOthersIdProof());
		sessionMap.put("authorityIds", acc.getAuthorityIds());
		
		
		return sessionMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sdrc.usermgmt.core.util.IUserManagementHandler#saveAccountDetails(
	 * java.util.Map, java.lang.Object) Send account in map json from front
	 * end-- /createUser
	 */
	@Override
	public boolean saveAccountDetails(Map<String, Object> map, Object account) {
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"); 
		Date date = Calendar.getInstance().getTime();  
		Account acc = (Account) account;
         acc.setUserName(map.get("userName").toString().toLowerCase());
         acc.setEmail(map.get("email") == null ? acc.getEmail() : map.get("email").toString().toLowerCase());
		
         if (!map.containsKey("organization"))
			throw new RuntimeException("key : mbl not found in map");

//		if (!map.containsKey("designation"))
//			throw new RuntimeException("key : name not found in map");

		if (!map.containsKey("areaId"))
			throw new RuntimeException("key : areaId not found in map");

		UserDetails userDetails = new UserDetails();
		userDetails.setOrganization(Integer.parseInt(map.get("organization").toString()));
//		userDetails.setDesignation(Integer.parseInt(map.get("designation").toString()));
		userDetails.setOrgName(map.get("orgName").toString());
//		userDetails.setDesgnName(map.get("desgnName").toString());
		userDetails.setAreaLevel(mongoAreaLevelRepository.findByAreaLevelId(Integer.parseInt(map.get("areaLevel").toString())));
		userDetails.setFirstName(map.get("firstName").toString());
		userDetails.setMiddleName(map.get("middleName") == null ? null : map.get("middleName").toString());
		userDetails.setLastName(map.get("lastName") == null ? null : map.get("lastName").toString());
		userDetails.setGender(map.get("gender") == null ? null : Gender.valueOf(map.get("gender").toString()));
		userDetails.setMobNo(map.get("mobNo") == null ? null : map.get("mobNo").toString());
		userDetails.setOthersDevPartner(
				map.get("othersDevPartner") == null ? null : map.get("othersDevPartner").toString());
		userDetails.setOthersIdProof(map.get("othersIdProof") == null ? null : map.get("othersIdProof").toString());
		userDetails.setDob(map.get("dob") == null ? null : map.get("dob").toString());
		userDetails.setDevPartner(map.get("devPartner")==null ? null : map.get("devPartner").toString());
		userDetails.setIdProType(map.get("idProType") == null ? null : map.get("idProType").toString());
		userDetails.setIdProofFile(map.get("idProofFile") == null ? null : map.get("idProofFile").toString());
        userDetails.setCreatedDate(dateFormat.format(date));
		// set userDetails to account
        List<String> authorityIds = acc.getAuthorityIds();
        List<Authority> changepassWordAuth = customAuthorityRepository.findByAuthorityIn(new ArrayList<String>(Arrays.asList(configurableEnvironment.getProperty("change.password.authority.name").split(","))));
        authorityIds.add(changepassWordAuth.get(0).getId());
        authorityIds.add(changepassWordAuth.get(1).getId());
        acc.setAuthorityIds(authorityIds);
		acc.setUserDetails(userDetails);
		
		//check whether admin
		 List<String> authorityNames =new ArrayList<>();
		 SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().forEach(role -> {
			 authorityNames.add(role.toString());
		});
		 
		if(!authorityNames.contains(configurableEnvironment.getProperty("rmncha.admin.role"))){
		acc.setLocked(true); // we are setting lock as true to make this user
		}else{
			acc.setLocked(false);
		}
		List<Integer> areaIds=null;	
		// pending for approval
		
			areaIds = (List<Integer>) map.get("areaId");
		
		//	areaIds = Arrays.asList(Integer.parseInt(configurableEnvironment.getProperty("createuser.nationallevel.userid")));
		
			if(areaIds.isEmpty()){
				areaIds = Arrays.asList(Integer.parseInt(configurableEnvironment.getProperty("createuser.nationallevel.userid")));
			}

		// verify whether areaId provided is exist or not
		List<Area> arIds = areaRepositoy.findByAreaIdIn(areaIds);
		if (!arIds.isEmpty() && arIds.size() == areaIds.size()) {
			acc.setMappedAreaIds(areaIds);
			accountRepository.save(acc);
			return true;
		} else {
			throw new RuntimeException("Key : areaId is invalid");
		}
	}

	@Override
	public boolean updateAccountDetails(Map<String, Object> map, Object account, Principal p) {
		Account acc = (Account) account;
		UserDetails userDetails = (UserDetails) acc.getUserDetails();
		
		 List<String> authorityNames =new ArrayList<>();
		 SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().forEach(role -> {
			 authorityNames.add(role.toString());
		});
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"); 
		Date date = Calendar.getInstance().getTime(); 
		
		 List<String> duplicateAuthorityIds = new ArrayList<>();
		
		        
		 List<String> authorityIds = ((List<String>) map.get("authorityIds"));
		 authorityIds.stream().forEach(id -> {
			 duplicateAuthorityIds.add(id);
		 });
		 
		 
		 List<Integer> areaIds=((List<Integer>) map.get("areaId"));
		 List<Integer> updatedAreaIds = new ArrayList<Integer>();
		 areaIds.stream().forEach(id-> {
			 updatedAreaIds.add(id);
		 });
		 
		 
		 List<Authority> changepassWordAuth=null;
		 if(map.get("isAdmin")!=null){
		if(map.get("isAdmin").toString().equals("Admin")){
			changepassWordAuth = customAuthorityRepository.findByAuthorityIn(new ArrayList<String>(Arrays.asList(configurableEnvironment.getProperty("change.password.authority.admin").split(","))));
		}else{
		  changepassWordAuth = customAuthorityRepository.findByAuthorityIn(new ArrayList<String>(Arrays.asList(configurableEnvironment.getProperty("change.password.authority.name").split(","))));
		}
		 }else{
			 changepassWordAuth = customAuthorityRepository.findByAuthorityIn(new ArrayList<String>(Arrays.asList(configurableEnvironment.getProperty("change.password.authority.name").split(","))));
		 }
		    if(!authorityIds.contains(changepassWordAuth.get(0).getId())){
		    	authorityIds.add(changepassWordAuth.get(0).getId());
		    }
		    if(!authorityIds.contains(changepassWordAuth.get(1).getId())){
		    	authorityIds.add(changepassWordAuth.get(1).getId());
		    }
	       
		    
		    
	
		
		if (Integer.parseInt(map.get("organization").toString()) != userDetails.getOrganization()
				|| !((List<Integer>) map.get("designation"))
						.contains(acc.getAssignedDesignations().get(0).getDesignationIds())
				|| map.get("areaLevel") != userDetails.getAreaLevel().getAreaLevelId()
				|| (((List<Integer>) map.get("areaId")).retainAll(acc.getMappedAreaIds()))
				|| (authorityIds.retainAll(acc.getAuthorityIds())) || (acc.getAuthorityIds().retainAll(authorityIds))
				|| (map.get("devPartner") == null ? false : userDetails.getDevPartner() == null ? false
						: ! map.get("devPartner").toString().equals(userDetails.getDevPartner()))
				|| ((map.get("othersDevPartner") == null ? false : userDetails.getOthersDevPartner() == null ? false
						:! map.get("othersDevPartner").toString().equals(userDetails.getOthersDevPartner())))) {
			acc.setLocked(true);
		} 
		
		if (authorityNames.contains(configurableEnvironment.getProperty("rmncha.admin.role"))) {
			acc.setLocked(false);
		} 
		
		
		userDetails = setInUserDetails(map, userDetails);
		 userDetails.setUpdateDate(dateFormat.format(date));
		
			// verify whether areaId provided is exist or not
			List<Area> arIds = areaRepositoy.findByAreaIdIn(updatedAreaIds);
			if (!arIds.isEmpty() && arIds.size() == updatedAreaIds.size()) {
				acc.setMappedAreaIds(updatedAreaIds);
		}
			
		
		
	    acc.setEmail(map.get("emailId") == null ? acc.getEmail() : map.get("emailId").toString().toLowerCase());
	    List<Designation> designations = designationRepository.findByIdIn((List<String>) map.get("designation"));
	     List<AssignedDesignations> listOfAssigneddesgnation = new ArrayList<>();
	      AssignedDesignations assignedDesignations =new AssignedDesignations();
	      assignedDesignations.setDesignationIds(designations.get(0).getId());
	    listOfAssigneddesgnation.add(assignedDesignations);
	    acc.setAssignedDesignations(listOfAssigneddesgnation);
	    
	    
	  
	    if(!duplicateAuthorityIds.contains(changepassWordAuth.get(0).getId())){
	    	duplicateAuthorityIds.add(changepassWordAuth.get(0).getId());
	    }
	    if(!duplicateAuthorityIds.contains(changepassWordAuth.get(1).getId())){
	    	duplicateAuthorityIds.add(changepassWordAuth.get(1).getId());
	    }
	    acc.setAuthorityIds(duplicateAuthorityIds);
	    
        
	    
	    
	    
		accountRepository.save(acc);
		return true;

	}

	private UserDetails setInUserDetails(Map<String, Object> map, UserDetails userDetails) {

		userDetails.setOrganization(map.get("organization") == null ? userDetails.getOrganization()
				: Integer.parseInt(map.get("organization").toString()));
//		userDetails.setDesignation(map.get("designation") == null ? userDetails.getDesignation()
//				: Integer.parseInt(map.get("designation").toString()));
		userDetails.setOrgName(map.get("orgName") == null ? userDetails.getOrgName() : map.get("orgName").toString());
//		userDetails.setDesgnName(
//				map.get("desgnName") == null ? userDetails.getDesgnName() : map.get("desgnName").toString());
		userDetails.setAreaLevel(map.get("areaLevel") == null ? userDetails.getAreaLevel()
				:mongoAreaLevelRepository.findByAreaLevelId(Integer.parseInt(map.get("areaLevel").toString())));
		userDetails.setFirstName(
				map.get("firstName") == null ? userDetails.getFirstName() : map.get("firstName").toString());
		userDetails.setMiddleName(
				map.get("middleName") == null ? userDetails.getMiddleName() : map.get("middleName").toString());
		userDetails
				.setLastName(map.get("lastName") == null ? userDetails.getLastName() : map.get("lastName").toString());
		userDetails.setGender(
				map.get("gender") == null ? userDetails.getGender() : Gender.valueOf(map.get("gender").toString()));
		userDetails.setMobNo(map.get("mobNo") == null ? userDetails.getMobNo() : map.get("mobNo").toString());
		userDetails.setOthersDevPartner(map.get("othersDevPartner") == null ? userDetails.getOthersDevPartner()
				: map.get("othersDevPartner").toString());
		userDetails.setOthersIdProof(map.get("othersIdProof") == null ? userDetails.getOthersIdProof()
				: map.get("othersIdProof").toString());
		userDetails.setDob(map.get("dob") == null ? userDetails.getDob() : map.get("dob").toString());
		userDetails.setDevPartner(map.get("devPartner") == null ? userDetails.getDevPartner()
				: map.get("devPartner").toString());
		userDetails.setIdProType(map.get("idProType") == null ? null
				: map.get("idProType").toString());
		
		userDetails.setIdProofFile(map.get("idProofFile")==null ? null : map.get("idProofFile").toString());
		return userDetails;
	}

	@Override
	public List<Authority> getAllAuthorities() {
		List<Authority> authorities = customAuthorityRepository
				.findByAuthorityIn(Arrays.asList("Data Entry & Visualization", "Visualization","Submission Management"));

		return authorities;
	}

}
