package org.sdrc.rmncha.service;

//import java.awt.geom.Area;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.mongoclientdetails.MongoClientDetails;
import org.sdrc.mongoclientdetails.repository.MongoClientDetailsRepository;
import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmncha.domain.AreaLevel;
import org.sdrc.rmncha.repositories.AreaLevelRepository;
import org.sdrc.rmncha.repositories.AreaRepository;
import org.sdrc.rmncha.repositories.CustomTypeDetailRepository;
import org.sdrc.usermgmt.mongodb.domain.Authority;
import org.sdrc.usermgmt.mongodb.domain.Designation;
import org.sdrc.usermgmt.mongodb.domain.DesignationAuthorityMapping;
import org.sdrc.usermgmt.mongodb.repository.AuthorityRepository;
import org.sdrc.usermgmt.mongodb.repository.DesignationAuthorityMappingRepository;
import org.sdrc.usermgmt.mongodb.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import in.co.sdrc.sdrcdatacollector.mongorepositories.TypeDetailRepository;
import in.co.sdrc.sdrcdatacollector.mongorepositories.TypeRepository;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 */
@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired
	MongoClientDetailsRepository mongoClientDetailsRepository;

	@Autowired
	AreaLevelRepository mongoAreaLevelRepository;

	@Autowired
	AreaRepository mongoAreaRepository;

	@Autowired
	AuthorityRepository authorityRepository;

	@Autowired
	DesignationAuthorityMappingRepository designationAuthorityMappingRepository;

	@Autowired
	DesignationRepository designationRepository;
	
	@Autowired
	private TypeDetailRepository typeDetailsRepository;
	
	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private CustomTypeDetailRepository customTypeDetailRepository;

	@Override
	public ResponseEntity<String> importAreas() {

		AreaLevel areaLevel = new AreaLevel();
		areaLevel.setAreaLevelId(1);
		areaLevel.setAreaLevelName("NATIONAL");

		mongoAreaLevelRepository.save(areaLevel);

		areaLevel = new AreaLevel();
		areaLevel.setAreaLevelId(2);
		areaLevel.setAreaLevelName("STATE");

		mongoAreaLevelRepository.save(areaLevel);

		areaLevel = new AreaLevel();
		areaLevel.setAreaLevelId(3);
		areaLevel.setAreaLevelName("DISTRICT");

		mongoAreaLevelRepository.save(areaLevel);

		areaLevel = new AreaLevel();
		areaLevel.setAreaLevelId(4);
		areaLevel.setAreaLevelName("BLOCK");

		mongoAreaLevelRepository.save(areaLevel);

		areaLevel = new AreaLevel();
		areaLevel.setAreaLevelId(5);
		areaLevel.setAreaLevelName("FACILITY");

		mongoAreaLevelRepository.save(areaLevel);
		
		
		List<TypeDetail> typeDetails = customTypeDetailRepository.findByNameInAndTypeTypeName(Arrays.asList("PHC","CHC","SC","SDH","UPHC","DH","MC","AH"), "Facility Type");

		Map<String, List<TypeDetail>> formTypeDetails = new HashMap<>();
		
		for (TypeDetail typeDetail : typeDetails) {
			
			if(formTypeDetails.containsKey(typeDetail.getName())) {
				formTypeDetails.get(typeDetail.getName()).add(typeDetail);
			}else {
				List<TypeDetail> ty = new ArrayList<>();
				ty.add(typeDetail);	
				formTypeDetails.put(typeDetail.getName(), ty);
			}
		}
		
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("area/");
		String path = url.getPath().replaceAll("%20", " ");
		File files[] = new File(path).listFiles();

		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}

		for (int f = 0; f < files.length; f++) {

			XSSFWorkbook workbook = null;

			try {
				workbook = new XSSFWorkbook(files[f]);
			} catch (InvalidFormatException | IOException e) {

				e.printStackTrace();
			}
			XSSFSheet areaSheet = workbook.getSheetAt(0);

			Integer id = null;
			String areaCode = null;
			String areaName = null;
			String parentAreaId = null;
			Integer areaLevelId = null;
//			Integer facilityTypeId=null;
//			Integer facilityLevelId=null;
			String facilityType=null;
			String facilityLevel=null;
			
			Area area = null;
			Map<String, Object> formIdTypeDeatils = new HashMap<>();
			
			for (int row = 1; row <= areaSheet.getLastRowNum(); row++) {// row
																		// loop
				 System.out.println("Rows::" + row);

				area = new Area();

				XSSFRow xssfRow = areaSheet.getRow(row);

				for (int cols = 0; cols < areaSheet.getRow(row).getLastCellNum(); cols++) {// column loop

					Cell cell = xssfRow.getCell(cols);

					switch (cols) {

					case 0:
						id = (int) cell.getNumericCellValue();
						break;

					case 1:
						areaCode = cell.getStringCellValue();
						break;

					case 2:
						areaName = cell.getStringCellValue();
						break;

					case 3:
						parentAreaId = cell.getStringCellValue();
						break;
						
					case 4:
//						facilityTypeId = (int)cell.getNumericCellValue();
						facilityType = cell.getStringCellValue();
						break;
						
                    case 5:
//                    	facilityLevelId=(int)cell.getNumericCellValue();
                    	facilityLevel = cell.getStringCellValue();
						break;
					

					case 6:
						areaLevelId = (int) cell.getNumericCellValue();

						Area parentArea = null;
						if (areaLevelId >= 3) {
							parentArea = mongoAreaRepository.findByAreaCode(parentAreaId);
						}

						switch (areaLevelId) {

						case 3:
							// district
							area.setStateId(parentArea.getAreaId());
							break;
						case 4:
							// block
							area.setDistrictId(parentArea.getAreaId());
							area.setStateId(mongoAreaRepository.findByAreaId(parentArea.getAreaId()).getParentAreaId());
							break;

						case 5:
							// facility
							
							switch(facilityType) {
							case "PHC":
							case "CHC":
							case "SC":
							case "UPHC":
								area.setBlockId(parentArea.getAreaId());
								area.setDistrictId(mongoAreaRepository.findByAreaId(parentArea.getAreaId()).getParentAreaId());
								area.setStateId(mongoAreaRepository.findByAreaId(area.getDistrictId()).getParentAreaId());
								
								formIdTypeDeatils = new HashMap<>();
								
								for (TypeDetail typeDetail : formTypeDetails.get(facilityType)) {
									formIdTypeDeatils.put(typeDetail.getFormId().toString(), typeDetail);
								}
								
								area.setFormIdTypeDetails(formIdTypeDeatils);
								
							break;
							case "SDH":
							case "DH":
							case "MC":
							case "AH":
								//block is not present
								//set only district and state
								area.setDistrictId(parentArea.getAreaId());
								area.setStateId(mongoAreaRepository.findByAreaId(parentArea.getAreaId()).getParentAreaId());
								
								formIdTypeDeatils = new HashMap<>();
								
								for (TypeDetail typeDetail : formTypeDetails.get(facilityType)) {
									formIdTypeDeatils.put(typeDetail.getFormId().toString(), typeDetail);
								}
								
								area.setFormIdTypeDetails(formIdTypeDeatils);
								
								break;
							}
//							area.setFacilityType(typeDetailsRepository.findByTypeAndNameAndFormId(typeRepository.findByTypeNameAndFormId("Facility Type", formId), 
//									facilityType, formId));
									
							area.setFacilityLevel(typeDetailsRepository.findByTypeAndNameAndFormId(typeRepository.findByTypeNameAndFormId("Facility Level", 1), 
									facilityLevel, 1));
							break;
						}
						break;
					}

				}
				area.setAreaId(id);
				area.setAreaCode(areaCode);
				area.setAreaName(areaName);
				area.setAreaLevel(mongoAreaLevelRepository.findByAreaLevelId(areaLevelId));
				area.setParentAreaId(parentAreaId != "" ? mongoAreaRepository.findByAreaCode(parentAreaId).getAreaId() : -1);
				area.setLive(true);
				mongoAreaRepository.save(area);
			}
		}

		return new ResponseEntity<>("succsess", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> formsValue() {

		return null;
	}

	@Override
	public String createMongoOauth2Client() {

		try {

			MongoClientDetails mongoClientDetails = new MongoClientDetails();

			HashSet<String> scopeSet = new HashSet<>();
			scopeSet.add("read");
			scopeSet.add("write");

			List<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority("dashboard"));

			Set<String> authorizedGrantTypes = new HashSet<>();
			authorizedGrantTypes.add("refresh_token");
			authorizedGrantTypes.add("client_credentials");
			authorizedGrantTypes.add("password");

			Set<String> resourceIds = new HashSet<>();
			resourceIds.add("web-service");

			mongoClientDetails.setClientId("rmncha");
			mongoClientDetails.setClientSecret("rmncha@123#!");
			mongoClientDetails.setScope(scopeSet);
			mongoClientDetails.setAccessTokenValiditySeconds(30000);
			mongoClientDetails.setRefreshTokenValiditySeconds(40000);
			mongoClientDetails.setAuthorities(authorities);
			mongoClientDetails.setAuthorizedGrantTypes(authorizedGrantTypes);
			mongoClientDetails.setResourceIds(resourceIds);

			mongoClientDetailsRepository.save(mongoClientDetails);
			return "success";

		} catch (Exception e) {

			throw new RuntimeException(e);
		}

	}

	@Override
	public ResponseEntity<String> config() {
		// create designation
		List<Designation> designationList = new ArrayList<>();

		Designation desg = new Designation();
		desg.setCode("001");
		desg.setName("ADMIN");
		desg.setSlugId(1);
		designationList.add(desg);

		desg = new Designation();
		desg.setCode("002");
		desg.setName("FACILITATOR");
		desg.setSlugId(2);
		designationList.add(desg);
		
		desg = new Designation();
		desg.setCode("003");
		desg.setName("COMMUNITY FACILITATOR");
		desg.setSlugId(3);
		designationList.add(desg);
		
		

		designationRepository.save(designationList);

		// create Authority

		List<Authority> authorityList = new ArrayList<>();

		Authority authority = new Authority();
		authority.setAuthority("USER_MGMT_ALL_API");
		authority.setDescription("Allow user to manage usermanagement module");
		authorityList.add(authority);

		authority = new Authority();
		authority.setAuthority("CREATE_USER");
		authority.setDescription("Allow user to access createuser API");
		authorityList.add(authority);
		
		authority = new Authority();
		authority.setAuthority("CHANGE_PASSWORD");
		authority.setDescription("Allow user to access changepassword API");
		authorityList.add(authority);
		
		authority = new Authority();
		authority.setAuthority("UPDATE_USER");
		authority.setDescription("Allow user to access updateuser API");
		authorityList.add(authority);
		
		authority = new Authority();
		authority.setAuthority("ENABLE_DISABLE_USER");
		authority.setDescription("Allow user to access enable/disable user API");
		authorityList.add(authority);
		
		authority = new Authority();
		authority.setAuthority("RESET_PASSWORD");
		authority.setDescription("Allow user to access resetpassword API");
		authorityList.add(authority);
		
		authority = new Authority();
		authority.setAuthority("dataentry_HAVING_write");
		authority.setDescription("Allow user to  dataentry module");
		authorityList.add(authority);

		authorityRepository.save(authorityList);

		// Designation-Authority Mapping

		List<DesignationAuthorityMapping> damList = new ArrayList<>();

		DesignationAuthorityMapping dam = new DesignationAuthorityMapping();

		dam.setAuthority(authorityRepository.findByAuthority("USER_MGMT_ALL_API"));
		dam.setDesignation(designationRepository.findByCode("001"));
		damList.add(dam);

		dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("dataentry_HAVING_write"));
		dam.setDesignation(designationRepository.findByCode("002"));
		damList.add(dam);
		
		dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("UPDATE_USER"));
		dam.setDesignation(designationRepository.findByCode("002"));
		damList.add(dam);
		
		dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("dataentry_HAVING_write"));
		dam.setDesignation(designationRepository.findByCode("003"));
		damList.add(dam);
		
		dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("CHANGE_PASSWORD"));
		dam.setDesignation(designationRepository.findByCode("003"));
		damList.add(dam);

		designationAuthorityMappingRepository.save(damList);
		return new ResponseEntity<String>("success", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> importFilterExpInTypeDetail() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("templates/");
		String path = url.getPath().replaceAll("%20", " ");
		File files[] = new File(path).listFiles();

		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}

		for (int f = 0; f < files.length; f++) {
			System.out.println(files[f].toString());
		}
		for (int f = 0; f < files.length; f++) {

			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(files[f]);
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			XSSFSheet choicesSheet = workbook.getSheet("Choices");

			for (int row = 1; row <= choicesSheet.getLastRowNum(); row++) {
				if (choicesSheet.getRow(row) == null)
					break;

				XSSFRow xssfRow = choicesSheet.getRow(row);
				int formId = 0; // initialize with outer loop when looping
								// multiple forms
				String pTypeName = "";
				String pTypeDetailName = "";
				for (int cols = 0; cols < 4; cols++) {// column loop
					Cell cell = xssfRow.getCell(cols);
					switch (cols) {
					case 0:
						if (cell == null)
							break;
						if (cell != null && (CellType.STRING == cell.getCellTypeEnum())) {
							formId = Integer.valueOf(cell.getStringCellValue());
						} else if (cell != null && (CellType.NUMERIC == cell.getCellTypeEnum())) {
							formId = (int) cell.getNumericCellValue();
						}
						break;
					case 1:
						if (cell == null)
							break;
						if (cell != null && (CellType.NUMERIC == cell.getCellTypeEnum())) {
							pTypeName = cell.getStringCellValue();
						}
						break;

					case 2:
						if (cell == null)
							break;
						if (cell != null && (CellType.NUMERIC == cell.getCellTypeEnum())) {
							pTypeDetailName = cell.getStringCellValue();
						}
						break;

					case 3:
						if (cell == null)
							break;
						if (cell != null && CellType.STRING == cell.getCellTypeEnum()) {
							String expressionArray[] = cell.getStringCellValue().split("\\&");
							String modifiedExp = "";

							for (String expression : expressionArray) {
								if (expression.contains("selected")) { // set typedetail id in case of dropdown only

									for (int i = 0; i < expression.split("").length; i++) {

										char ch = expression.charAt(i);

										if (ch == '}') {
											String typeTypeDetails = "";
											for (int j = i + 2; j < expression.split("").length; j++) {

												char chNext = expression.charAt(j);
												if (chNext == ')') {
													break;
												}

												typeTypeDetails = typeTypeDetails + chNext;
											}

											String typeName = typeTypeDetails.split(":")[0];
											String typeDetailsName = typeTypeDetails.split(":")[1];

											TypeDetail td = typeDetailsRepository.findByTypeAndNameAndFormId(
													typeRepository.findByTypeNameAndFormId(
															StringUtils.trimWhitespace(typeName), formId),
													StringUtils.trimWhitespace(typeDetailsName), formId);

											String newExp = expression.replace(typeTypeDetails,
													td.getSlugId().toString());
											modifiedExp = modifiedExp + newExp + " | ";
										}
									}

								} else {
									modifiedExp = modifiedExp + expression + " | ";

								}
							}

							modifiedExp = modifiedExp.substring(0, modifiedExp.length() - 3);
							TypeDetail parentTd = typeDetailsRepository
									.findByTypeAndNameAndFormId(
											typeRepository.findByTypeNameAndFormId(
													StringUtils.trimWhitespace(pTypeName), formId),
											StringUtils.trimWhitespace(pTypeDetailName), formId);
							parentTd.setFilterByExp(modifiedExp);
							typeDetailsRepository.save(parentTd);
						}

					}
				}
			}
		}
		return new ResponseEntity<String>("success", HttpStatus.OK);
	}
	
}
