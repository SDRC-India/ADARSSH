package org.sdrc.rmncha.service;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.rmncha.domain.Organization;
import org.sdrc.rmncha.domain.TimePeriod;
import org.sdrc.rmncha.repositories.OrganizationRepository;
import org.sdrc.rmncha.repositories.RegistrationOTPRepository;
import org.sdrc.usermgmt.mongodb.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import in.co.sdrc.sdrcdatacollector.document.Type;
import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import in.co.sdrc.sdrcdatacollector.mongorepositories.TypeDetailRepository;
import in.co.sdrc.sdrcdatacollector.mongorepositories.TypeRepository;

@Service
public class CollectionServiceImpl implements CollectionService {

	@Autowired
	OrganizationRepository organizationRepository;

	@Autowired
	@Qualifier("mongoAccountRepository")
	private AccountRepository accountRepository;

	@Autowired
	RegistrationOTPRepository registrationOTPRepository;

//	@Autowired
//	UserDesignationRepository userDesignationRepository;

	@Autowired
	TypeRepository typeRepository;

	@Autowired
	TypeDetailRepository typeDetailRepository;

	@Override
	public String updateArea() {
		return "area Saved";
	}

	@Override
	public String saveDate() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("organization/");
		String path = url.getPath().replaceAll("%20", " ");
		File[] files = new File(path).listFiles();

		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}

		for (int f = 0; f < files.length; f++) {

			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(files[f]);

				// organization import
				XSSFSheet orgSheet = workbook.getSheetAt(0);
				for (int row = 1; row <= orgSheet.getLastRowNum(); row++) {
					int cols = 0;
					if (orgSheet.getRow(row) == null)
						break;

					XSSFRow xssfRow = orgSheet.getRow(row);

					Cell cell = xssfRow.getCell(cols);
					Organization organization = new Organization();
					organization.setOrganizationId((int) cell.getNumericCellValue());
					++cols;
					Cell cell2 = xssfRow.getCell(cols);
					organization.setOrganizationName(cell2.getStringCellValue());
					organizationRepository.save(organization);

				}
				// userImport
				/*
				 * XSSFSheet areaSheet = workbook.getSheetAt(1);
				 * 
				 * for (int row = 1; row <= orgSheet.getLastRowNum(); row++) {
				 * int cols=0; if (orgSheet.getRow(row) == null) break;
				 * 
				 * XSSFRow xssfRow = orgSheet.getRow(row); Account account = new
				 * Account();
				 * account.setUserName(xssfRow.getCell(cols).getStringCellValue(
				 * )); account.setPassword(xssfRow.getCell(++cols).
				 * getStringCellValue());
				 * account.setEmail(xssfRow.getCell(++cols).getStringCellValue()
				 * ); List<Integer> mappedAreaIds =new ArrayList<>(); String[]
				 * ids =
				 * xssfRow.getCell(++cols).getStringCellValue().split(","); for
				 * (String id : ids) { mappedAreaIds.add(Integer.parseInt(id));
				 * } account.setMappedAreaIds(mappedAreaIds); UserDetails
				 * userDetails =new UserDetails();
				 * userDetails.setOrganization((int)
				 * xssfRow.getCell(++cols).getNumericCellValue());
				 * userDetails.setDesignation((int)
				 * xssfRow.getCell(++cols).getNumericCellValue());
				 * account.setUserDetails(userDetails);
				 * 
				 * accountRepository.save(account); }
				 */

				// FacilityType import
				/*
				 * XSSFSheet facilityTypeareaSheet = workbook.getSheetAt(2); for
				 * (int row = 1; row <= facilityTypeareaSheet.getLastRowNum();
				 * row++) { int cols=0; if (facilityTypeareaSheet.getRow(row) ==
				 * null) break;
				 * 
				 * XSSFRow xssfRow = facilityTypeareaSheet.getRow(row);
				 * FacilityType facilityType = new FacilityType();
				 * facilityType.setFacilityTypeId((int)
				 * xssfRow.getCell(cols).getNumericCellValue());
				 * facilityType.setFacilityTypeName(xssfRow.getCell(++cols).
				 * getStringCellValue() );
				 * facilityTypeRepository.save(facilityType); }
				 */

				// FacilityLevel import
				/*
				 * XSSFSheet facilityTypeareaSheet = workbook.getSheetAt(3); for
				 * (int row = 1; row <= facilityTypeareaSheet.getLastRowNum();
				 * row++) { int cols=0; if (facilityTypeareaSheet.getRow(row) ==
				 * null) break;
				 * 
				 * XSSFRow xssfRow = facilityTypeareaSheet.getRow(row);
				 * FacilityLevel facilityLevel = new FacilityLevel();
				 * facilityLevel.setFacilityLevelId((int)
				 * xssfRow.getCell(cols).getNumericCellValue());
				 * facilityLevel.setFacilityLevelName(xssfRow.getCell(++cols).
				 * getStringCellValue ());
				 * facilityLevelRepository.save(facilityLevel); }
				 */
				XSSFSheet userDesignationSheet = workbook.getSheetAt(4);
//				for (int row = 1; row <= userDesignationSheet.getLastRowNum(); row++) {
//					int cols = 0;
//					if (userDesignationSheet.getRow(row) == null)
//						break;
//
//					XSSFRow xssfRow = userDesignationSheet.getRow(row);
//					UserDesignation userDesignation = new UserDesignation();
//					userDesignation.setDesignationId((int) xssfRow.getCell(cols).getNumericCellValue());
//					userDesignation.setDesignationName(xssfRow.getCell(++cols).getStringCellValue());
//					userDesignationRepository.save(userDesignation);
//				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "done";
	}

	@Override
	public String saveTimePeriod() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM");
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		TimePeriod timePeriod = new TimePeriod();
		timePeriod.setCreatedDate(date);
		timePeriod.setStartDate(cal.getTime());
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		timePeriod.setEndDate(cal.getTime());
		timePeriod.setTimePeriodId(1);
		timePeriod.setTimePeriod(simpleDateFormat.format(date).toUpperCase());
		timePeriod.setPeriodicity("1");
		simpleDateFormat = new SimpleDateFormat("YYYY");
		if (cal.get(Calendar.MONTH) > 2) {
			timePeriod.setFinancialYear(simpleDateFormat.format(date).toUpperCase() + "-"
					+ (Integer.parseInt(simpleDateFormat.format(date).toUpperCase()) + 1));
		} else {
			timePeriod.setFinancialYear(Integer.parseInt(simpleDateFormat.format(date).toUpperCase()) - 1 + "-"
					+ simpleDateFormat.format(date).toUpperCase());
		}
		timePeriod.setYear(Integer.parseInt(simpleDateFormat.format(date).toUpperCase()));
		// timePeriodRepository.save(timePeriod);
		return "succes";
	}

	@Override
	public String saveDevelopmentPartners() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("Development partner typedetails/");
		String path = url.getPath().replaceAll("%20", " ");
		File[] files = new File(path).listFiles();

		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}

		for (int f = 0; f < files.length; f++) {

			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(files[f]);

				// save type
				XSSFSheet typeSheet = workbook.getSheetAt(0);
				for (int row = 1; row <= typeSheet.getLastRowNum(); row++) {
					int cols = 0;
					if (typeSheet.getRow(row) == null)
						break;

					XSSFRow xssfRow = typeSheet.getRow(row);

//					Cell cell = xssfRow.getCell(cols);
					Type type = new Type();
					type.setSlugId(typeRepository.findAll().size() + 1);
					type.setTypeName(xssfRow.getCell(cols).getStringCellValue());
					type.setDescription(xssfRow.getCell(++cols).getStringCellValue());
					
					typeRepository.save(type);

				}
//				typeDetailsRepository.findAll().size() + 1
//				typeRepository.findAll().size() + 1
				/*
				 * XSSFSheet typeSheet = workbook.getSheetAt(0); for (int row =
				 * 1; row <= typeSheet.getLastRowNum(); row++) { int cols = 0;
				 * if (typeSheet.getRow(row) == null) break;
				 * 
				 * XSSFRow xssfRow = typeSheet.getRow(row);
				 * 
				 * Cell cell = xssfRow.getCell(cols); Type type = new Type();
				 * type.setSlugId((int) cell.getNumericCellValue());
				 * type.setTypeName(xssfRow.getCell(++cols).getStringCellValue()
				 * ); type.setDescription(xssfRow.getCell(++cols).
				 * getStringCellValue());
				 * 
				 * typeRepository.save(type);
				 * 
				 * }
				 */
				XSSFSheet typeDetailSheet = workbook.getSheetAt(1);

				// save typedetails
				for (int row = 1; row <= typeDetailSheet.getLastRowNum(); row++) {
					int cols = 0;
					if (typeDetailSheet.getRow(row) == null)
						break;

					XSSFRow xssfRow = typeDetailSheet.getRow(row);
					TypeDetail typeDetail = new TypeDetail();
					typeDetail.setSlugId(typeDetailRepository.findAll().size() + 1);
					typeDetail.setName(xssfRow.getCell(cols).getStringCellValue());
					typeDetail.setOrderLevel((int) xssfRow.getCell(++cols).getNumericCellValue());
					typeDetail.setType(typeRepository.findByTypeName(xssfRow.getCell(++cols).getStringCellValue()));

					typeDetailRepository.save(typeDetail);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "done";
	}

}
