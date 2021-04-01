package org.sdrc.rmncha.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmncha.domain.AreaLevel;
import org.sdrc.rmncha.domain.BulkUserRegistration;
import org.sdrc.rmncha.domain.Organization;
import org.sdrc.rmncha.domain.RegistrationOTP;
import org.sdrc.rmncha.domain.UserDetails;
import org.sdrc.rmncha.model.AreaModel;
import org.sdrc.rmncha.model.UserModel;
import org.sdrc.rmncha.rabbitMQ.CollectionChannel;
import org.sdrc.rmncha.repositories.AreaLevelRepository;
import org.sdrc.rmncha.repositories.AreaRepository;
import org.sdrc.rmncha.repositories.BulkUserRegistrationRepository;
import org.sdrc.rmncha.repositories.CustomAccountRepository;
import org.sdrc.rmncha.repositories.CustomAuthorityRepository;
import org.sdrc.rmncha.repositories.CustomDesignationRepository;
import org.sdrc.rmncha.repositories.CustomTypeDetailRepository;
import org.sdrc.rmncha.repositories.OrganizationRepository;
import org.sdrc.rmncha.repositories.RegistrationOTPRepository;
import org.sdrc.rmncha.util.Gender;
import org.sdrc.rmncha.util.Mail;
import org.sdrc.usermgmt.model.AuthorityControlType;
import org.sdrc.usermgmt.mongodb.domain.Account;
import org.sdrc.usermgmt.mongodb.domain.AssignedDesignations;
import org.sdrc.usermgmt.mongodb.domain.Authority;
import org.sdrc.usermgmt.mongodb.domain.Designation;
import org.sdrc.usermgmt.mongodb.repository.DesignationRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sarita Panigrahi Created Date: 05-02-2018
 * 
 *         Pending users: enabled = true; expired = false; Locked = true
 *         Approved users: enabled = true; expired = false; Locked = false
 *         Rejected users: enabled = true; expired = true; Locked = false
 */
@Service
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

	/*
	 * @Autowired private AccountRepository accountRepository;
	 */

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Autowired
	@Qualifier("customAccountRepository")
	private CustomAccountRepository customAccountRepository;

	@Autowired
	private CustomTypeDetailRepository customTypeDetailRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private AreaLevelRepository mongoAreaLevelRepository;

	@Autowired
	@Qualifier("mongoDesignationRepository")
	private DesignationRepository designationRepository;
	
	@Autowired
	private CustomDesignationRepository customDesignationRepository;

	@Autowired
	private RegistrationOTPRepository registrationOTPRepository;

//	@Autowired
//	private MailService mailService;

	@Autowired
	private AreaRepository mongoAreaRepository;

	@Autowired
	private CustomTypeDetailRepository customtypeDetailRepository;

	@Autowired
	private CustomAuthorityRepository customAuthorityRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private BulkUserRegistrationRepository bulkUserRegistrationRepository;
	
	@Autowired
	private SessionMapInitializerClass sessionMapInitializerClass;
	
	@Autowired
	private CollectionChannel collectionChannel;

	@Value("${ramncha.photoIdfilepath}")
	private String photoIdfilepath;

	@Value("${templatedownloadpath.path}")
	private String downloadtemplatepath;

	@Value("#{'${header.string}'.split(',')}")
	private List<String> headersList;

	@Value("#{'${manadatory.templatecolumn}'.split(',')}")
	private List<String> columnNo;
	
	@Value("#{'${manadatory.templatecolumnstatedistrictblock}'.split(',')}")
	private List<String> columnNo1;
	
	@Value("${download.filepath}")
	private String downloadTemplateFromPath;
	
//	TimeZone timezone = TimeZone.getDefault();
//	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	
	private  Path bulkUserPathLocation ;
	private Path photoIdfilePathLocation;
	
	@PostConstruct
	public void init() {
		bulkUserPathLocation = Paths.get(photoIdfilepath+"BulkUser/");
		photoIdfilePathLocation=Paths.get(photoIdfilepath);
	}
	
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sdrc.rmncha.service.UserManagementService#approveUser(java.lang.
	 * String)
	 */
	/*
	 * @Override
	 * 
	 * @Transactional public ResponseEntity<String> approveUser(String userId) {
	 * Gson gson = new Gson(); Account user =
	 * accountRepository.findById(userId); try { user.setLocked(false);
	 * accountRepository.save(user); return new
	 * ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty(
	 * "user.approve.success")), HttpStatus.OK); } catch (Exception e) { log.
	 * error("Action : approve-user By{}: error while user approval with userId {}"
	 * , user.getUserName(), user.getId(), e); throw new RuntimeException(); } }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sdrc.rmncha.service.UserManagementService#rejectUser(java.lang.
	 * String)
	 */
	@Override
	@Transactional
	public ResponseEntity<String> rejectAndApproveUser(List<String> listOfuserId, Boolean isApprove,String reason) {
		List<Account> listOfAccounts = new ArrayList<Account>();
		List<Account> users = customAccountRepository.findByIdIn(listOfuserId);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"); 
		Date date = Calendar.getInstance().getTime();  
		
		Map<String,String> map = new HashMap<>();
		if(reason!=null){
			for (String string : reason.split(",")) {
				map.put(string.split("-")[0], string.split("-")[1]);
			}
		}
		
		
		if (isApprove) {
			users.stream().forEach(user -> {
				user.setLocked(false);
				UserDetails userDetails = (UserDetails) user.getUserDetails();
				userDetails.setAproveRejectDate(dateFormat.format(date));
				user.setUserDetails(userDetails);
				listOfAccounts.add(user);
			});

		} else {
			users.stream().forEach(user -> {
				user.setLocked(false);
				user.setExpired(true); // in case of rejection only set expired
										// true
//				user.setEnabled(false);
				
				UserDetails userDetails = (UserDetails) user.getUserDetails();
				userDetails.setAproveRejectDate(dateFormat.format(date));
				userDetails.setRejectReason(map.get(user.getId()));
				user.setUserDetails(userDetails);
				listOfAccounts.add(user);
			});

		}

		try {
			List<Account> listOfAccount = customAccountRepository.save(listOfAccounts);

			mailSending(listOfAccount, isApprove);
			
			/*ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

			emailExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						mailSending(listOfAccount, isApprove);
					} catch (Exception e) {
					}
				}
			});
			emailExecutor.shutdown();*/

			if (isApprove) {
				return new ResponseEntity<>(configurableEnvironment.getProperty("user.approve.success"), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(configurableEnvironment.getProperty("user.reject.success"), HttpStatus.OK);
			}
		} catch (Exception e) {
			if (isApprove) {
				listOfAccounts.stream().forEach(user -> {
					log.error("Action : approve-user By{}: error while user approval with userId {}",
							user.getUserName(), user.getId(), e);
				});
				return new ResponseEntity<>(configurableEnvironment.getProperty("user.approve.fail"), HttpStatus.OK);
			} else {
				listOfAccounts.stream().forEach(user -> {
					log.error("Action : reject-user By{}: error while user rejection with userId {}",
							user.getUserName(), user.getId(), e);
				});
				return new ResponseEntity<>(configurableEnvironment.getProperty("user.approve.fail"), HttpStatus.OK);
			}
			
		}
	}

	private void mailSending(List<Account> listOfAccount, Boolean isApprove) {
		
		for (Account account : listOfAccount) {
			UserDetails user = (UserDetails) account.getUserDetails();
			Mail mailModel = new Mail();
			mailModel.setToEmailIds(Arrays.asList(account.getEmail()));
			mailModel.setFromUserName("Thank you!" + "\n"+ "\n"+ configurableEnvironment.getProperty("email.donot.reply") +"\n"+ configurableEnvironment.getProperty("email.disclaimer"));
			if(isApprove) {
			mailModel.setSubject("ADARSSH: Registration request approved ");
			mailModel.setMessage("\n"+"Your registration request has been approved by the admin. Please get in touch with the admin if you might require any further information."+ "\n" + "\n" );
			}else {
				mailModel.setSubject("ADARSSH: Registration request rejected ");
				mailModel.setMessage("\n"+"Your registration request has been rejected by the admin for "+user.getRejectReason()+". Please get in touch with the admin if you might require any further information."+ "\n" + "\n" );
			}
			mailModel.setToUserName("Dear "+user.getFirstName()+",");
			mailModel.setEmail(account.getEmail());

//			mailService.sendSimpleMessage(mailModel);
			collectionChannel.sendEmailChannel().send(MessageBuilder.withPayload(mailModel).build());
		}
	}




	// get all user by their approval status
	@Override
	public ResponseEntity<Map<String, List<UserModel>>> getAllUser() {
		List<Account> accountList = customAccountRepository.findAll();

		Map<String, List<UserModel>> statusUserListMap = new HashMap<>();
		// * Pending users: enabled = true; expired = false; Locked = true

		// boolean isExpired, boolean isEnable, boolean isLocked
		statusUserListMap.put("Pending",
				setInUserModel(filterDataModels(accountList, getUserByStatus(false, true, true))));
		// * Approved users: enabled = true; expired = false; Locked = false
		statusUserListMap.put("Approved",
				setInUserModel(filterDataModels(accountList, getUserByStatus(false, true, false))));
		// * Rejected users: enabled = false; expired = true; Locked = true
		statusUserListMap.put("Rejected",
				setInUserModel(filterDataModels(accountList, getUserByStatus(true, true, false))));

		return new ResponseEntity<>(statusUserListMap, HttpStatus.OK);
	}

	private List<UserModel> setInUserModel(List<Account> account) {
		List<UserModel> listOfUserList = new ArrayList<>();
		UserModel userModel = null;

		Map<String, String> authIdsNameMap = new HashMap<>();

		List<Authority> auths = customAuthorityRepository.findAll();

		auths.stream().forEach(auth -> {
			authIdsNameMap.put(auth.getId(), auth.getAuthority());
		});

		Map<String, String> designationIdNameMap = new HashMap<>();
		List<Designation> designations = designationRepository.findAll();

		designations.stream().forEach(desig -> {
			designationIdNameMap.put(desig.getId(), desig.getName());
		});

		Map<String, String> devPartnerIdNameMap = new HashMap<>();
		List<TypeDetail> devPartners = customtypeDetailRepository.findAll();

		devPartners.stream().forEach(devp -> {
			devPartnerIdNameMap.put(devp.getId(), devp.getName());
		});
		Map<String, String> areaLevelIdNameMap = new HashMap<>();
		List<AreaLevel> areaLevels = mongoAreaLevelRepository.findAll();

		areaLevels.stream().forEach(areaLevel -> {
			areaLevelIdNameMap.put(areaLevel.getId(), areaLevel.getAreaLevelName());
		});

		List<String> accountWiseAuths = null;
		List<String> accountWiseDesigs = null;
//		dateFormat.setTimeZone(timezone);
		
		for (Account acc : account) {
			if(acc.getAuthorityIds()!=null){
			if(!acc.getAuthorityIds().contains(customAuthorityRepository.findByAuthority(configurableEnvironment.getProperty("rmncha.admin.role")).getId())){
			accountWiseAuths = new ArrayList<>();
			accountWiseDesigs = new ArrayList<>();

			UserDetails userDetails = (UserDetails) acc.getUserDetails();
			userModel = new UserModel();
			BeanUtils.copyProperties(userDetails, userModel);
			userModel.setAreas(mongoAreaRepository.findByAreaIdIn(acc.getMappedAreaIds()));
			userModel.setEmailId(acc.getEmail());
			userModel.setName(acc.getUserName());
			userModel.setUserId(acc.getId());
			userModel.setEnable(acc.isEnabled());
			
			
		/*	Date createDate = null;
			Date aproveRejectDate = null;
			try {
				createDate = dateFormat.parse(userDetails.getCreatedDate());
				
				if(userDetails.getAproveRejectDate()!= null) {
					aproveRejectDate = dateFormat.parse(userDetails.getAproveRejectDate());
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			userModel.setCreatedDate(createDate.toString());
			userModel.setAproveRejectDate(aproveRejectDate.toString());*/
			
			userModel.setRoleId(userDetails.getAreaLevel()==null ? null : userDetails.getAreaLevel().getAreaLevelId());
			// userModel.setDesignation(designationRepository.findById(acc.getAssignedDesignations().get(0).getDesignationIds()).getSlugId());
			userModel.setDevPartner(
					userDetails.getDevPartner() == null ? "" : devPartnerIdNameMap.get(userDetails.getDevPartner()));
			userModel.setAreaLevel(userDetails.getAreaLevel() == null ? ""
					: areaLevelIdNameMap.get(userDetails.getAreaLevel().getId()));

			if (acc.getAuthorityIds() != null) {
				userModel.setAuthorities(new HashSet<String>(acc.getAuthorityIds()));
				for (String authId : acc.getAuthorityIds()) {
					accountWiseAuths.add(authIdsNameMap.get(authId));
				}
				userModel.setAuthorityNames(accountWiseAuths);
			}

			if (acc.getAssignedDesignations() != null) {
				for (AssignedDesignations designation : acc.getAssignedDesignations()) {
					accountWiseDesigs.add(designationIdNameMap.get(designation.getDesignationIds()));
				}
				userModel.setDesgnName(accountWiseDesigs.get(0));
				userModel.setDesignation(acc.getAssignedDesignations().get(0).getDesignationIds());
			}

			listOfUserList.add(userModel);

		}
		}
		}
		return listOfUserList;
	}

	public List<Account> filterDataModels(List<Account> userList, Predicate<Account> predicate) {
		return userList.stream().filter(predicate).collect(Collectors.<Account>toList());
	}

	public Predicate<Account> getUserByStatus(boolean isExpired, boolean isEnable, boolean isLocked) {
		return p -> p.isExpired() == isExpired && p.isEnabled() == isEnable && p.isLocked() == isLocked;
	}

	// get all typdetails by type name
	@Override
	public ResponseEntity<List<TypeDetail>> getTypeDetailsByType(String typeName) {

		List<TypeDetail> typeDetails = customTypeDetailRepository.findByTypeTypeName(typeName);
		return new ResponseEntity<>(typeDetails, HttpStatus.OK);
	}

	// get all organizations
	@Override
	public ResponseEntity<List<Organization>> getAllOrganization() {

		List<Organization> organization = organizationRepository.findAll();
		return new ResponseEntity<>(organization, HttpStatus.OK);
	}

	// get all area levels
	@Override
	public ResponseEntity<List<AreaLevel>> getAreaLevels() {

		List<AreaLevel> areaLevels = mongoAreaLevelRepository.findAll();
		areaLevels.removeIf(findFacilityLevel(configurableEnvironment.getProperty("facility.area.level.name"))); // remove
																													// facility
																													// level

		return new ResponseEntity<>(areaLevels, HttpStatus.OK);
	}

	public Predicate<AreaLevel> findFacilityLevel(String facility) {
		return f -> f.getAreaLevelName().equalsIgnoreCase(facility);
	}

	// @Override
	public ResponseEntity<String> saveDesignation(String designationName) {

		// designationRepository.find

		Designation designation = new Designation();
		designation.setName(designationName);

		return new ResponseEntity<>("", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> getEmailVerificationCode(String email) {
		try {
			Random random = new Random();
			int otp = random
					.nextInt(Integer.parseInt(configurableEnvironment.getProperty("generate.otp.max.digit"))
							- Integer.parseInt(configurableEnvironment.getProperty("generate.otp.min.digit")) + 1)
					+ Integer.parseInt(configurableEnvironment.getProperty("generate.otp.min.digit"));

			RegistrationOTP reOtp = registrationOTPRepository.findByEmailIdAndIsActiveTrue(email);
//			ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
			if (reOtp == null) {
				createNewOTPAndSendMail(email, String.valueOf(otp));
//				emailExecutor.execute(new Runnable() {
//					@Override
//					public void run() {
//						try {
//							createNewOTPAndSendMail(email, String.valueOf(otp));
//							// SendEmailUtility.sendmail(emaildummy);
//						} catch (Exception e) {
//							// logger.error("failed", e);
//						}
//					}
//				});
			} else {
				reOtp.setIsActive(false);
				registrationOTPRepository.save(reOtp);

				createNewOTPAndSendMail(email, String.valueOf(otp));
//				emailExecutor.execute(new Runnable() {
//					@Override
//					public void run() {
//						try {
//							createNewOTPAndSendMail(email, String.valueOf(otp));
//							// SendEmailUtility.sendmail(emaildummy);
//						} catch (Exception e) {
//							// logger.error("failed", e);
//						}
//					}
//				});

			}
//			emailExecutor.shutdown();
			return new ResponseEntity<>("OTP has been sent to your email", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			// return null;
			return new ResponseEntity<>("Try Again", HttpStatus.OK);
		}
	}

	public ResponseEntity<String> createNewOTPAndSendMail(String email, String varificationCode) throws Exception {

		RegistrationOTP registrationOTP = new RegistrationOTP();
		registrationOTP.setEmailId(email);
		registrationOTP.setIpAddress(InetAddress.getLocalHost().getHostAddress());
		registrationOTP.setIsActive(true);
		registrationOTP.setCreatedDateAndTime(new Timestamp(System.currentTimeMillis()));
		registrationOTP.setVarificationCode(Integer.parseInt(varificationCode));
		registrationOTPRepository.save(registrationOTP);

		Mail mailModel = new Mail();
		mailModel.setToEmailIds(Arrays.asList(email));
		
		mailModel.setSubject("ADARSSH: One Time Password");
		mailModel.setToUserName("Dear User,");
		mailModel.setMessage("\n"+"Your OTP for RMNCHA application: " + Integer.parseInt(varificationCode));
		
		mailModel.setFromUserName("Thank you!" + "\n"+ "\n"+ configurableEnvironment.getProperty("email.donot.reply") +"\n"+ configurableEnvironment.getProperty("email.disclaimer"));
		mailModel.setEmail(email);
		
		//publish the email to rabbitmq
		collectionChannel.sendEmailChannel().send(MessageBuilder.withPayload(mailModel).build());

//		mailService.sendSimpleMessage(mailModel);

		return new ResponseEntity<>("OTP verified", HttpStatus.OK);
		// return mailModel;
	}

	@Override
	public ResponseEntity<String> oTPAndEmailAvailibility(String email, Integer varificationCode) {

		RegistrationOTP registrationOTP = registrationOTPRepository
				.findByEmailIdAndVarificationCodeAndIsActiveTrue(email, varificationCode);
		if (registrationOTP != null) {
			long minutes = TimeUnit.MILLISECONDS
					.toMinutes(System.currentTimeMillis() - registrationOTP.getCreatedDateAndTime().getTime());
			if (minutes <= 30) {
				registrationOTP.setIsActive(false);
				registrationOTPRepository.save(registrationOTP);
				return new ResponseEntity<>("OTP verified", HttpStatus.OK);

			} else {
				registrationOTP.setIsActive(false);
				registrationOTPRepository.save(registrationOTP);

				return new ResponseEntity<>("OTP expired! Try another.", HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<>("Invalid OTP! Please enter valid OTP", HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<Boolean> usernameAvailablity(String username) {
		Account account = customAccountRepository.findByUserName(username);
		if (account == null) {
			return new ResponseEntity<>(false, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<String> getServerDate() {
		return new ResponseEntity<>(DateTimeFormatter.ofPattern("dd/MM/YYYY").format(LocalDateTime.now().toLocalDate()),
				HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Boolean> getEmailAvailablity(String email) {
		Account account = customAccountRepository.findByEmailAndExpiredFalse(email);
		if (account == null) {
			return new ResponseEntity<>(false, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(true, HttpStatus.OK);
		}

	}

	@Override
	public ResponseEntity<String> uploadFile(MultipartFile multipartfiles) {
		String filePath = null;

		if (!new File(photoIdfilepath).exists()) {
			new File(photoIdfilepath).mkdirs();
		}
       if(multipartfiles!=null){
		if (!multipartfiles.isEmpty()  ) {
			try {
				String extentionName = FilenameUtils.getExtension(multipartfiles.getOriginalFilename());
				String fileNameWithDateTime = FilenameUtils.getBaseName(multipartfiles.getOriginalFilename()) + "_"
						+ new Date().getTime() + "." + extentionName;

				filePath = photoIdfilepath + fileNameWithDateTime;
			
				Files.copy(multipartfiles.getInputStream(),
						this.photoIdfilePathLocation.resolve(fileNameWithDateTime),
						StandardCopyOption.REPLACE_EXISTING);

			} catch (Exception e) {
				log.error("Exception occcured in uploadFile ", e);
			}
			
		}
		return new ResponseEntity<>(filePath, HttpStatus.OK);
	}else{
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
		

	}

	

	@Override
	public Map<String, List<AreaModel>> getAllAreaList() {

		List<Area> areas = mongoAreaRepository.findAllByAreaLevelAreaLevelIdIn(Arrays.asList(2, 3, 4));
		List<AreaModel> areaModelList = new ArrayList<>();
		Map<String, List<AreaModel>> areaMap = new LinkedHashMap<>();
		// setting areas is area-model list
		for (Area area : areas) {
			AreaModel areaModel = new AreaModel();
			areaModel.setAreaCode(area.getAreaCode());
			areaModel.setAreaId(area.getAreaId());
			areaModel.setAreaLevel(area.getAreaLevel().getAreaLevelName());
			areaModel.setAreaName(area.getAreaName());
			areaModel.setParentAreaId(area.getParentAreaId());
			areaModelList.add(areaModel);
		}
		// making levelName as a key
		for (AreaModel areaModel : areaModelList) {
			if (areaMap.containsKey(areaModel.getAreaLevel())) {
				areaMap.get(areaModel.getAreaLevel()).add(areaModel);
			} else {
				areaModelList = new ArrayList<>();
				areaModelList.add(areaModel);
				areaMap.put(areaModel.getAreaLevel(), areaModelList);
			}
		}
		return areaMap;
	}

	@Override
	public ResponseEntity<String> addDesignation(String designation) {
		
		if(customDesignationRepository.findByName(designation)!=null) {
			return new ResponseEntity<>("failed", HttpStatus.CONFLICT);
		}
		List<Designation> listOfDesignation = designationRepository.findAll();
		Designation desgn = new Designation();
		desgn.setCode("00" + (listOfDesignation.size() + 1));
		desgn.setSlugId(listOfDesignation.size() + 1);
		desgn.setName(designation);
		designationRepository.save(desgn);

		return new ResponseEntity<>("success", HttpStatus.OK);
	}

	@Override
	public String downLoadBulkTemplate() throws Exception {
		
		//List<Area> listOfAreas = areaRepository.findAllByOrderByAreaNameAsc();
		List<Area> listOfAreas = mongoAreaRepository.findAll();
		List<Authority> authorities = sessionMapInitializerClass.getAllAuthorities();
		List<Organization> listOfOrganization = organizationRepository.findAll();
		List<Designation> listOfDesignation = designationRepository.findAll();
		List<TypeDetail> listOfTypeDetails = customTypeDetailRepository.findByTypeTypeName("Development Partner");
		List<AreaLevel> listOfareaLevel = mongoAreaLevelRepository.findAll();
		
		HashMap<String, String> map = new HashMap<>(); 
		List<String> userLevelList = new ArrayList<>();
		authorities.stream().forEach(authorit ->{
			userLevelList.add(authorit.getAuthority());
			map.put(authorit.getAuthority(), authorit.getId());
		});
		
		List<String> organisationNameList = new ArrayList<>();
		listOfOrganization.stream().forEach(organisation ->{
			organisationNameList.add(organisation.getOrganizationName());
			map.put(organisation.getOrganizationName(), organisation.getOrganizationId().toString());
		});
		
		List<String> designationnNameList = new ArrayList<>();
		listOfDesignation.stream().forEach(designation -> {
			
			if(designation.getSlugId()!=1) {
				designationnNameList.add(designation.getName());
				map.put(designation.getName(), designation.getId());
			}
		});
		
		
		List<String> typeDetailsList = new ArrayList<>();
		listOfTypeDetails.stream().forEach(typeDetail -> {
			typeDetailsList.add(typeDetail.getName());
			map.put(typeDetail.getName(), typeDetail.getId());
		});
		
		listOfareaLevel.removeIf(findFacilityLevel(configurableEnvironment.getProperty("facility.area.level.name")));
		List<String> areaLevelsList = new ArrayList<>();
		listOfareaLevel.stream().forEach(areaLevel -> {
			areaLevelsList.add(areaLevel.getAreaLevelName());
		});
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("bulkusertemplate/");
		String path = url.getPath().replaceAll("%20", " ");
		File files[] = new File(path).listFiles();

		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}
		
		if (!new File(downloadtemplatepath).exists()) {
			new File(downloadtemplatepath).mkdirs();

		}

		if (new File(downloadtemplatepath + "/userListTemplatedownload.xls").exists()) {
			new File(downloadtemplatepath + "/userListTemplatedownload.xls").delete();

		}
		
		copyFileUsingChannel(files[0], new File(downloadtemplatepath + "/userListTemplatedownload.xls"));
		FileInputStream file = new FileInputStream(new File(downloadtemplatepath + "/userListTemplatedownload.xls"));
		// Get the workbook instance for XLS file
		HSSFWorkbook workbook = new HSSFWorkbook(file);

		// Get the sheet from the workbook
		HSSFSheet userListSheet = workbook.getSheet("UserList Template");

		CellRangeAddressList addressList = new CellRangeAddressList(1, 5000, 7, 7);
		CellRangeAddressList addressList1 = new CellRangeAddressList(1, 5000, 11, 11);
		CellRangeAddressList addressList2 = new CellRangeAddressList(1, 5000, 12, 12);
	//  CellRangeAddressList addressList3 = new CellRangeAddressList(1, 5000, 14, 14);
     CellRangeAddressList addressList4 = new CellRangeAddressList(1, 5000, 15, 15);

		DVConstraint dvConstraint = DVConstraint
				.createExplicitListConstraint(Arrays.stream(areaLevelsList.toArray()).toArray(String[]::new));
		DVConstraint dvConstraint1 = DVConstraint
				.createExplicitListConstraint(Arrays.stream(organisationNameList.toArray()).toArray(String[]::new));
		DVConstraint dvConstraint2 = DVConstraint
				.createExplicitListConstraint(Arrays.stream(typeDetailsList.toArray()).toArray(String[]::new));
		DVConstraint dvConstraint3 = DVConstraint
				.createExplicitListConstraint(Arrays.stream(designationnNameList.toArray()).toArray(String[]::new));
		DVConstraint dvConstraint4 = DVConstraint
				.createExplicitListConstraint(Arrays.stream(userLevelList.toArray()).toArray(String[]::new));

		DataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
		DataValidation dataValidation1 = new HSSFDataValidation(addressList1, dvConstraint1);
		DataValidation dataValidation2 = new HSSFDataValidation(addressList2, dvConstraint2);
		//DataValidation dataValidation3 = new HSSFDataValidation(addressList3, dvConstraint3);
     	DataValidation dataValidation4 = new HSSFDataValidation(addressList4, dvConstraint4);

		userListSheet.addValidationData(dataValidation);
		userListSheet.addValidationData(dataValidation1);
		userListSheet.addValidationData(dataValidation2);
		//userListSheet.addValidationData(dataValidation3);
		userListSheet.addValidationData(dataValidation4);
		for (int i = 1; i < 12; i++) {
			userListSheet.removeRow(userListSheet.getRow(i));
			
		}
		
		
		
		HSSFSheet masterDataSheet = workbook.getSheet("Master_Data");
		
		int areRowNum=1;
		for(Area area:listOfAreas) {
		 HSSFRow row = masterDataSheet.createRow(areRowNum);
        row.createCell(0).setCellValue(area.getAreaId());
        row.createCell(1).setCellValue(area.getAreaCode());
        row.createCell(2).setCellValue(area.getAreaLevel().getAreaLevelId().toString());
        row.createCell(3).setCellValue(area.getAreaName());
        row.createCell(4).setCellValue(area.getParentAreaId());
		++areRowNum;
		
	}
		
		
		HSSFSheet dropDownSheet = workbook.createSheet("DropDown_Data");
		int i=0;
		boolean flag=true;
	for (Map.Entry<String,String> entry : map.entrySet()) { 
		if (flag){
			HSSFRow row = dropDownSheet.createRow(5555);
			row.createCell(55).setCellValue("sirmncha");
			flag=false;
		}
		HSSFRow row = dropDownSheet.createRow(i);
         row.createCell(0).setCellValue(entry==null ? "" : entry.getKey().toString());
         row.createCell(1).setCellValue(entry==null ? "" : entry.getValue().toString());
         i++;
	}
	
	
	HSSFSheet designationSheet = workbook.getSheet("Sheet2");
	i=0;
    for (String designation: designationnNameList) { 
	HSSFRow row = designationSheet.createRow(i);
     row.createCell(25).setCellValue(designation.toString());
     
     i++;
}
	
	
	
	masterDataSheet.protectSheet("pks@123#");
	dropDownSheet.protectSheet("pks@123#");
	workbook.setSheetHidden(0, true);
	workbook.setSheetHidden(3,true);
	
		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream(downloadtemplatepath + "/userListTemplatedownload" + ".xls");

		//workbook.write(fileOut);
		workbook.write(fileOut);
		fileOut.close();

		// Closing the workbook
		workbook.close();
		return downloadtemplatepath + "/userListTemplatedownload" + ".xls";
	}

	private void copyFileUsingChannel(File source, File dest) throws Exception {
		 
			    FileChannel sourceChannel = null;
			    FileChannel destChannel = null;
			    try {
			        sourceChannel = new FileInputStream(source).getChannel();
			        destChannel = new FileOutputStream(dest).getChannel();
			        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
			       }finally{
			           sourceChannel.close();
			           destChannel.close();
			           }
			    
			   
		
	}




	@Override
	public List<String> uploadBulkTemplate(MultipartFile multipartfile) throws Exception {
		
		
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"); 
		Date date = Calendar.getInstance().getTime(); 
		
		List<Account> accountLists = customAccountRepository.findAll();
		List<String> emailList = new ArrayList<String>();
		List<String> username = new ArrayList<String>();
		accountLists.stream().forEach(account ->{
			if(account.getEmail()!=null){
				if(!account.isExpired())
				emailList.add(account.getEmail().toLowerCase());
			}
			
			username.add(account.getUserName().toLowerCase()); 
		});
		
		File newFile = null;
		newFile=new File(photoIdfilepath+"BulkUser/");
		if (!newFile.exists()) {
			newFile.mkdirs();
		}
		String filePath = FilenameUtils.getBaseName(multipartfile.getOriginalFilename()) + "_"
				+ new Date().getTime() + "." + FilenameUtils.getExtension(multipartfile.getOriginalFilename());
		Files.copy(multipartfile.getInputStream(),
				this.bulkUserPathLocation.resolve(filePath),
				StandardCopyOption.REPLACE_EXISTING);
		
			
		
		FileInputStream file = new FileInputStream(photoIdfilepath+"BulkUser/"+filePath);
		
		List<String> messagelist = new ArrayList<String>();
		
		if( !FilenameUtils.getExtension(multipartfile.getOriginalFilename()).equals("xls")){
			 messagelist.add("Invalid File");
			 return messagelist;
		}
		
		
		
		
		
		// Get the workbook instance for XLS file
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet dropDown = workbook.getSheet("DropDown_Data");
		if(dropDown!=null){
		if(dropDown.getRow(5555)==null){
			messagelist.add("Invalid File");
			return messagelist;
		}
		
		}else{
			messagelist.add("Invalid File");
			return messagelist;
		}
		HSSFSheet sheet = workbook.getSheet("UserList Template");
		
		
		
		Map<String,Integer> emailMap =new HashMap<>();
		Map<String,Integer> usernameMap =new HashMap<>();
		
		
		
		if(isSheetEmpty(sheet)){
		
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				if (!isRowEmpty(sheet.getRow(i))) {

					if (sheet.getRow(i) != null) {
						boolean flag = true;
						// Email duplicate check
						
						if (!isCellBlank(sheet.getRow(i).getCell(6))) {
							
							//Email id validation
							 if(!isValidEmailAddress(sheet.getRow(i).getCell(6).toString().toLowerCase())) {
								 messagelist.add("Email " + sheet.getRow(i).getCell(6).toString()
											+ " is incorect format for Row No " + (sheet.getRow(i).getRowNum()+1));
							 }
							 //sheet contains duplicate EmailId
							if(!emailMap.containsKey(sheet.getRow(i).getCell(6).toString().trim())) {
								emailMap.put(sheet.getRow(i).getCell(6).toString().trim(), (sheet.getRow(i).getRowNum()+1));
							}else {
								messagelist.add("Email " + sheet.getRow(i).getCell(6).toString()
										+ " in Row No " + (sheet.getRow(i).getRowNum()+1)+" is duplicate for Row no " + emailMap.get(sheet.getRow(i).getCell(6).toString().trim()));
							}
							
							//duplicate MailId
							if (emailList.contains(sheet.getRow(i).getCell(6).toString().toLowerCase())) {
								messagelist.add("Email " + sheet.getRow(i).getCell(6).toString()
										+ " already exists for Row No " + (sheet.getRow(i).getRowNum()+1));
							}
						}

						// UserName duplicate Check
						if (!isCellBlank(sheet.getRow(i).getCell(16))) {
							if (username.contains(sheet.getRow(i).getCell(16).getStringCellValue().toLowerCase())) {
								messagelist.add("Username " + sheet.getRow(i).getCell(16).getStringCellValue()
										+ " already exists for Row No " + (sheet.getRow(i).getRowNum()+1));
							}
							
							 //sheet contains duplicate userName
							if(!usernameMap.containsKey(sheet.getRow(i).getCell(16).getStringCellValue().toLowerCase().trim())) {
								usernameMap.put(sheet.getRow(i).getCell(16).getStringCellValue().toLowerCase().trim(), (sheet.getRow(i).getRowNum()+1));
							}else {
								messagelist.add("Username " + sheet.getRow(i).getCell(16).getStringCellValue().toLowerCase()
										+ " in Row No " + (sheet.getRow(i).getRowNum()+1)+" is duplicate for Row no " + usernameMap.get(sheet.getRow(i).getCell(16).getStringCellValue().toLowerCase().toLowerCase()));
							}
							
							
						}

						for (int j = 0; j < columnNo.size(); j++) {
							if (isCellBlank(sheet.getRow(i).getCell(Integer.parseInt(columnNo.get(j))))) {
								messagelist.add("Manadatory field is blank for "
										+ sheet.getRow(0).getCell(Integer.parseInt(columnNo.get(j))).toString()
										+ " of row no." + (sheet.getRow(i).getRowNum()+1));
							}
							/* ====== */
							if (!isCellBlank(sheet.getRow(i).getCell(11))) {
								if (sheet.getRow(i).getCell(11).toString().equals("Development Partners") && flag) {
									if (isCellBlank(sheet.getRow(i).getCell(12))) {
										messagelist.add("Manadatory field is blank for "
												+ sheet.getRow(0).getCell(Integer.parseInt("12")).toString()
												+ " of row no." + (sheet.getRow(i).getRowNum()+1));
									}
									if (!isCellBlank(sheet.getRow(i).getCell(12))) {
										if (sheet.getRow(i).getCell(12).getStringCellValue()
												.equals("Others (specify)")) {
											if(isCellBlank(sheet.getRow(i).getCell(13))) {
											messagelist.add("Manadatory field is blank for "
													+ sheet.getRow(0).getCell(Integer.parseInt("13")).toString()
													+ " of row no." + (sheet.getRow(i).getRowNum()+1));
										}
										}
									}
									flag = false;
								}
							}
							/* ====== */

						}
					}

					if (sheet.getRow(i).getCell(7) != null) {

						switch (sheet.getRow(i).getCell(7).getStringCellValue()) {
						case "STATE":
							if (isCellBlank(sheet.getRow(i).getCell(Integer.parseInt(columnNo1.get(0))))) {
								messagelist.add("Mandatory field is blank for "
										+ sheet.getRow(0).getCell(Integer.parseInt(columnNo1.get(0))).toString()
										+ " of row no." + (sheet.getRow(i).getRowNum()+1));
							}
							break;
						case "DISTRICT":
							if (isCellBlank(sheet.getRow(i).getCell(Integer.parseInt(columnNo1.get(0))))) {
								messagelist.add("Manadatory field is blank for "
										+ sheet.getRow(0).getCell(Integer.parseInt(columnNo1.get(0))).toString()
										+ " of row no." + (sheet.getRow(i).getRowNum()+1));
							}
							if(isCellBlank(sheet.getRow(i).getCell(Integer.parseInt(columnNo1.get(1))))) {
								messagelist.add("Manadatory field is blank for "
										+ sheet.getRow(0).getCell(Integer.parseInt(columnNo1.get(1))).toString()
										+ " of row no." + (sheet.getRow(i).getRowNum()+1));
							}
							break;
						case "BLOCK":
							if (isCellBlank(sheet.getRow(i).getCell(Integer.parseInt(columnNo1.get(0))))) {
								messagelist.add("Manadatory field is blank for "
										+ sheet.getRow(0).getCell(Integer.parseInt(columnNo1.get(0))).toString()
										+ " of row no." + (sheet.getRow(i).getRowNum()+1));
							}
							if(isCellBlank(sheet.getRow(i).getCell(Integer.parseInt(columnNo1.get(1))))) {
								messagelist.add("Manadatory field is blank for "
										+ sheet.getRow(0).getCell(Integer.parseInt(columnNo1.get(1))).toString()
										+ " of row no." + (sheet.getRow(i).getRowNum()+1));
								
							} 
							if(isCellBlank(sheet.getRow(i).getCell(Integer.parseInt(columnNo1.get(2))))) {
								messagelist.add("Manadatory field is blank for "
										+ sheet.getRow(0).getCell(Integer.parseInt(columnNo1.get(2))).toString()
										+ " of row no." + (sheet.getRow(i).getRowNum()+1));
							}
							break;
						}

					}
				}
			}
			if (messagelist.isEmpty()) {

				Map<String, Area> stateMap = new HashMap<String, Area>();
				Map<String, Area> districtBlockMap = new HashMap<String, Area>();
				List<String> authId = new ArrayList<>();

				List<Authority> changepassWordAuth = customAuthorityRepository
						.findByAuthorityIn(new ArrayList<String>(Arrays.asList(
								configurableEnvironment.getProperty("change.password.authority.name").split(","))));
				changepassWordAuth.stream().forEach(auth -> {
					authId.add(auth.getId());
				});

				mongoAreaRepository.findAll().stream().forEach(area -> {
					switch (area.getAreaLevel().getAreaLevelId()) {
					case 2:
						stateMap.put(area.getAreaName(), area);
						break;
					case 3:
						districtBlockMap.put(area.getAreaName() + "-" + area.getParentAreaId(), area);
						break;
					case 4:
						districtBlockMap.put(area.getAreaName() + "-" + area.getParentAreaId(), area);
						break;
					}
				});

				BulkUserRegistration bulkUserRegistration = new BulkUserRegistration();
				bulkUserRegistration.setFilePath(photoIdfilepath + "BulkUser/" + filePath);
				bulkUserRegistration.setCreatedDate(dateFormat.format(date));
				BulkUserRegistration bulkUser = bulkUserRegistrationRepository.save(bulkUserRegistration);

				Map<String, AreaLevel> areaLevelMap = new HashMap<>();
				mongoAreaLevelRepository.findAll().stream().forEach(arealevel -> {
					areaLevelMap.put(arealevel.getAreaLevelName(), arealevel);
				});

			/*	Account acc = new Account();
				UserDetails userDetails = new UserDetails();*/
				HashMap<String, String> map = new HashMap<>();
				List<Account> accountList = new ArrayList<Account>();
				HSSFSheet sheet2 = workbook.getSheet("DropDown_Data");
				for (int i = 0; i <= sheet2.getLastRowNum(); i++) {
					if (sheet2.getRow(i) != null) {
						if (sheet2.getRow(i).getCell(0) != null) {
							map.put(sheet2.getRow(i).getCell(0).getStringCellValue(),
									sheet2.getRow(i).getCell(1).getStringCellValue());
						}
					}
				}
				
				List<Integer> mappedAreaId = null;
				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					if (!isRowEmpty(sheet.getRow(i))) {
						
						Account acc = new Account();
						UserDetails userDetails = new UserDetails();
						
						mappedAreaId = new ArrayList<>();
						userDetails.setFirstName(sheet.getRow(i).getCell(0).toString());
						userDetails.setMiddleName(
								sheet.getRow(i).getCell(1) == null ? null : sheet.getRow(i).getCell(1).toString());
						userDetails.setLastName(
								sheet.getRow(i).getCell(2) == null ? null : sheet.getRow(i).getCell(2).toString());
						userDetails.setGender(sheet.getRow(i).getCell(3) == null ? null
								: Gender.valueOf(sheet.getRow(i).getCell(3).toString()));
						userDetails.setDob(sheet.getRow(i).getCell(4) == null || sheet.getRow(i).getCell(4).getCellTypeEnum()!= CellType.NUMERIC ? null
								: new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("dd-MMM-yyyy").parse(sheet.getRow(i).getCell(4).toString())));
						
						userDetails.setMobNo(
								sheet.getRow(i).getCell(5) == null ? null : sheet.getRow(i).getCell(5).toString());
						acc.setEmail(sheet.getRow(i).getCell(6) == null ? null
								: sheet.getRow(i).getCell(6).toString().toLowerCase());
						userDetails.setAreaLevel(areaLevelMap.get(sheet.getRow(i).getCell(7).toString()));
						userDetails.setOrganization(Integer.parseInt(map.get(sheet.getRow(i).getCell(11).toString())));
						userDetails.setOrgName(sheet.getRow(i).getCell(11).toString());
						if (sheet.getRow(i).getCell(11)!=null && sheet.getRow(i).getCell(11).toString().equals("Development Partners")) {
							userDetails.setDevPartner(sheet.getRow(i).getCell(12) == null ? null
									: map.get(sheet.getRow(i).getCell(12).toString()));
						}
						if (sheet.getRow(i).getCell(12)!=null && sheet.getRow(i).getCell(12).toString().equals("Others (specify)")) {
							userDetails.setOthersDevPartner(sheet.getRow(i).getCell(13) == null ? null
									: sheet.getRow(i).getCell(13).toString());
						}
						// userDetails.setOthersDevPartner(sheet.getRow(i).getCell(13)==null ? null :
						// sheet.getRow(i).getCell(13).toString());
						userDetails.setBulkId(bulkUser.getId());
						userDetails.setCreatedDate(dateFormat.format(date));
						userDetails.setAproveRejectDate(dateFormat.format(date));
						AssignedDesignations asg = new AssignedDesignations();
						asg.setDesignationIds(map.get(sheet.getRow(i).getCell(14).toString()));
						asg.setEnable(true);
						acc.setAssignedDesignations(Arrays.asList(asg));

						authId.addAll(
								Arrays.asList(map.get(sheet.getRow(i).getCell(15).getStringCellValue()).split(",")));
						acc.setAuthorityIds(authId);

						acc.setUserName(sheet.getRow(i).getCell(16).getStringCellValue().toLowerCase());

						acc.setPassword(
								passwordEncoder.encode(sheet.getRow(i).getCell(17).getCellTypeEnum() == CellType.STRING
										? sheet.getRow(i).getCell(17).getStringCellValue()
										: Integer.toString(((int) sheet.getRow(i).getCell(17).getNumericCellValue()))));
						switch (sheet.getRow(i).getCell(7).toString()) {
						case "NATIONAL":
							mappedAreaId.add(1);
							break;
						case "STATE":
							mappedAreaId.add(stateMap.get(sheet.getRow(i).getCell(8).toString()).getAreaId());
							break;
						case "DISTRICT":
							mappedAreaId.add(stateMap.get(sheet.getRow(i).getCell(8).toString()).getAreaId());
							mappedAreaId.add(districtBlockMap
									.get(sheet.getRow(i).getCell(9).toString() + "-"
											+ (stateMap.get(sheet.getRow(i).getCell(8).toString()).getAreaId()))
									.getAreaId());
							break;
						case "BLOCK":
							mappedAreaId.add(stateMap.get(sheet.getRow(i).getCell(8).toString()).getAreaId());
							mappedAreaId.add(districtBlockMap
									.get(sheet.getRow(i).getCell(9).toString() + "-"
											+ stateMap.get(sheet.getRow(i).getCell(8).toString()).getAreaId())
									.getAreaId());
							mappedAreaId.add(districtBlockMap.get(sheet.getRow(i).getCell(10).toString() + "-"
									+ (districtBlockMap
											.get(sheet.getRow(i).getCell(9).toString() + "-"
													+ stateMap.get(sheet.getRow(i).getCell(8).toString()).getAreaId())
											.getAreaId()))
									.getAreaId());
							break;

						}
						acc.setAuthorityControlType(AuthorityControlType.AUTHORITY);
						acc.setMappedAreaIds(mappedAreaId);
						acc.setUserDetails(userDetails);
						accountList.add(acc);

					}

				}
				customAccountRepository.save(accountList);
			}
		workbook.close();
		return messagelist;
		}else{
			messagelist.add("Sheet is blank  ");
			workbook.close();
			return messagelist;
		}
		
	}

	private boolean isCellBlank(HSSFCell cell) {
		
		if(cell==null) {
			return true;
		}else if(cell.getCellTypeEnum()==CellType.BLANK) {
			return true;
		}else {
		return false;
		}
	}




	private boolean isValidEmailAddress(String email) {

        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
	}




	private boolean isRowEmpty(HSSFRow hssfRow) {
		for (int c = hssfRow.getFirstCellNum(); c < hssfRow.getLastCellNum(); c++) {
			HSSFCell cell = hssfRow.getCell(c);
	        if (cell != null && cell.getCellTypeEnum() != CellType.BLANK )
	            return false;
	    }
	    return true;
	}




	private boolean isSheetEmpty(HSSFSheet sheet) {

	       Iterator rows = sheet.rowIterator();
	       while (rows.hasNext()) {
	           HSSFRow row = (HSSFRow) rows.next();
	          if( row.getRowNum()!=0){
	           Iterator cells = row.cellIterator();
	           while (cells.hasNext()) {
	                HSSFCell cell = (HSSFCell) cells.next();
	                if(!cell.getStringCellValue().isEmpty()){
	                    return true;
	                }
	           }
	          }
	       }
	       return false;
	}




	@Override
	public List<UserModel> getUser(Integer roleId, Integer areaId) {
		List<Account> accountList = customAccountRepository.findByExpiredFalse();
		List<UserModel> userModelList = new ArrayList<>();
		
		List<Organization> listOfOrganization = organizationRepository.findAll();
		HashMap<Integer, String> map = new HashMap<>();
		listOfOrganization.stream().forEach(organisation ->{
			map.put(organisation.getOrganizationId(), organisation.getOrganizationName());
		});

		Map<String, String> devPartnerIdNameMap = new HashMap<>();
		List<TypeDetail> devPartners = customtypeDetailRepository.findAll();

		devPartners.stream().forEach(devp -> {
			devPartnerIdNameMap.put(devp.getId(), devp.getName());
		});

		Map<String, String> areaLevelIdNameMap = new HashMap<>();
		List<AreaLevel> areaLevels = mongoAreaLevelRepository.findAll();

		areaLevels.stream().forEach(areaLevel -> {
			areaLevelIdNameMap.put(areaLevel.getId(), areaLevel.getAreaLevelName());
		});

		Map<String, String> designationIdNameMap = new HashMap<>();
		List<Designation> designations = designationRepository.findAll();

		designations.stream().forEach(desig -> {
			designationIdNameMap.put(desig.getId(), desig.getName());
		});

		Map<String, String> authIdsNameMap = new HashMap<>();

		List<Authority> auths = customAuthorityRepository.findAll();

		auths.stream().forEach(auth -> {
			authIdsNameMap.put(auth.getId(), auth.getAuthority());
		});

		List<String> accountWiseAuths = null;
		List<String> accountWiseDesigs = null;
		for (Account acc : accountList) {

			UserDetails userDetails = (UserDetails) acc.getUserDetails();
			if(acc.getAuthorityIds()!=null){
			if(!acc.getAuthorityIds().contains(customAuthorityRepository.findByAuthority(configurableEnvironment.getProperty("rmncha.admin.role")).getId())){
			if(areaId==null && userDetails.getAreaLevel() != null ){
				if (userDetails.getAreaLevel().getAreaLevelId() == roleId){
					userModelList.add(setAllUser(userDetails,acc,devPartnerIdNameMap,areaLevelIdNameMap,designationIdNameMap,authIdsNameMap,accountWiseAuths,accountWiseDesigs,map));
				}
				
			}else if (userDetails.getAreaLevel() != null) {
				if (userDetails.getAreaLevel().getAreaLevelId() == roleId && acc.getMappedAreaIds().contains(areaId)) {
			
					userModelList.add(setAllUser(userDetails,acc,devPartnerIdNameMap,areaLevelIdNameMap,designationIdNameMap,authIdsNameMap,accountWiseAuths,accountWiseDesigs,map));

				}
			}
			}
		}
		}

		return userModelList;
	}

	private UserModel setAllUser(UserDetails userDetails, Account acc, Map<String, String> devPartnerIdNameMap, Map<String, String> areaLevelIdNameMap, Map<String, String> designationIdNameMap, Map<String, String> authIdsNameMap, List<String> accountWiseAuths, List<String> accountWiseDesigs,Map<Integer, String> map) {
		accountWiseAuths = new ArrayList<>();
		accountWiseDesigs = new ArrayList<>();
		UserModel userModel = new UserModel();
		BeanUtils.copyProperties(userDetails, userModel);
		userModel.setAreas(mongoAreaRepository.findByAreaIdIn(acc.getMappedAreaIds()));
		userModel.setEmailId(acc.getEmail());
		userModel.setName(acc.getUserName());
		// userModel.setDesignation(designationRepository.findById(acc.getAssignedDesignations().get(0).getDesignationIds()).getSlugId());
		userModel.setDevPartner(userDetails.getDevPartner() == null ? ""
				: devPartnerIdNameMap.get(userDetails.getDevPartner()));
		userModel.setAreaLevel(userDetails.getAreaLevel() == null ? ""
				: areaLevelIdNameMap.get(userDetails.getAreaLevel().getId()));
		userModel.setUserId(acc.getId());
		userModel.setEnable(acc.isEnabled());
		userModel.setRoleId(userDetails.getAreaLevel()==null ? null : userDetails.getAreaLevel().getAreaLevelId());
		userModel.setDevPartnerId(userDetails.getDevPartner() == null ? ""
				: userDetails.getDevPartner());
		userModel.setIdProTypeName(userDetails.getIdProType() == null ? ""
				: devPartnerIdNameMap.get(userDetails.getIdProType()));
		userModel.setOrgName(map.get(userDetails.getOrganization()));

		if (acc.getAuthorityIds() != null) {
			userModel.setAuthorities(new HashSet<String>(acc.getAuthorityIds()));
			for (String authId : acc.getAuthorityIds()) {
				accountWiseAuths.add(authIdsNameMap.get(authId));
			}
			userModel.setAuthorityNames(accountWiseAuths);
		}

		if (acc.getAssignedDesignations() != null) {
			for (AssignedDesignations designation : acc.getAssignedDesignations()) {
				accountWiseDesigs.add(designationIdNameMap.get(designation.getDesignationIds()));
			}
			userModel.setDesgnName(accountWiseDesigs.get(0));
			userModel.setDesignation(acc.getAssignedDesignations().get(0).getDesignationIds());
		}
		return userModel;

	}

	@Override
	public ResponseEntity<Boolean> userEmailAvailablity(String email) {
		Account account = customAccountRepository.findByEmailAndExpiredFalse(email);
		if (account == null) {
			return new ResponseEntity<>(false, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
	}

	@Override
	public void downLoadFile(String filePath, HttpServletResponse response, Boolean inline) {

	       InputStream inputStream;
	       String fileName = "";
	       try {
	       fileName =  filePath.trim().replaceAll("%3A", ":").replaceAll("%2F", "/")
	       .replaceAll("%5C", "/").replaceAll("%2C", ",").replaceAll("\\\\", "/")
	       .replaceAll("\\+", " ").replaceAll("%22", "").replaceAll("%3F", "?").replaceAll("%3D", "=");
	       inputStream = new FileInputStream(fileName);
	       log.info(fileName);
	       String headerKey = "Content-Disposition";
	       String headerValue = "";

	       if(inline!=null && inline) {
	       headerValue= String.format("inline; filename=\"%s\"",
	       new java.io.File(fileName).getName());
	       response.setContentType("application/pdf"); // for pdf file
	       // type
	       }else {
	       headerValue= String.format("attachment; filename=\"%s\"",
	       new java.io.File(fileName).getName());
	       response.setContentType("application/octet-stream"); // for all file
	       // type
	       }

	       response.setHeader(headerKey, headerValue);

	       ServletOutputStream outputStream = response.getOutputStream();
	       FileCopyUtils.copy(inputStream, outputStream);
	       inputStream.close();
	       outputStream.flush();
	       outputStream.close();
	       
	       } catch (FileNotFoundException e) {
          log.error("FileNotFoundException",  e);
	       } catch (IOException e) {
	       log.error("IOException",  e);
	       }

	       
		
		
	}
	

}
