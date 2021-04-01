package org.sdrc.rmnchadashboard.web.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmncha.domain.TimePeriod;
import org.sdrc.rmncha.mongodomain.AllChecklistFormData;
import org.sdrc.rmncha.mongorepository.AllChecklistFormDataRepository;
import org.sdrc.rmncha.repositories.AreaRepository;
import org.sdrc.rmncha.repositories.CustomTypeDetailRepository;
import org.sdrc.rmncha.repositories.TimePeriodRepository;
import org.sdrc.rmnchadashboard.utils.ExcelStyleSheet;
import org.sdrc.rmnchadashboard.utils.Mail;
import org.sdrc.rmnchadashboard.web.model.AreaModel;
import org.sdrc.rmnchadashboard.web.model.ReportModel;
import org.sdrc.rmnchadashboard.web.model.TypeDetailModel;
import org.sdrc.usermgmt.mongodb.domain.Account;
import org.sdrc.usermgmt.mongodb.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import in.co.sdrc.sdrcdatacollector.document.EnginesForm;
import in.co.sdrc.sdrcdatacollector.document.Question;
import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import in.co.sdrc.sdrcdatacollector.models.FormAttachmentsModel;
import in.co.sdrc.sdrcdatacollector.mongorepositories.EngineFormRepository;
import in.co.sdrc.sdrcdatacollector.mongorepositories.QuestionRepository;
import in.co.sdrc.sdrcdatacollector.mongorepositories.TypeDetailRepository;
import in.co.sdrc.sdrcdatacollector.util.Status;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ReportServiceImpl implements ReportService {

	@Autowired
	private AreaRepository mongoAreaRepository;

	@Autowired
	private TypeDetailRepository typeDetailRepository;

	@Autowired(required = false)
	private EngineFormRepository formRepository;

	@Autowired
	private AllChecklistFormDataRepository allChecklistFormDataRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private CustomTypeDetailRepository customTypeDeatilsRepository;

	@Autowired
	private EngineFormRepository engineFormRepository;

	@Value("${output.path.pdf}")
	private String directoryPath;

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Autowired
	@Qualifier("mongoAccountRepository")
	private AccountRepository accountRepository;

	@Autowired
	private TimePeriodRepository timePeriodRepository;
	
	@Autowired
	public JavaMailSender emailSender;

	private SimpleDateFormat sdfMonthYear = new SimpleDateFormat("MM/yyyy");
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat sdfTime = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	private SimpleDateFormat simpleDateformater = new SimpleDateFormat("dd/MM/yyyy");
	private DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
	
	@Override
	public ReportModel getReportData() {

		ReportModel reportModel = new ReportModel();

		reportModel.setArea(getAllAreaList());
		Map<Integer, List<TypeDetailModel>> typeDetailModelMap = new HashMap<>();
		List<TypeDetail> fetchedTypeDetails = customTypeDeatilsRepository
				.findByTypeTypeName(configurableEnvironment.getProperty("report_data_typename"));
		TypeDetailModel typeDetailModel = null;
		List<TypeDetailModel> listOfTypeDetail = null;

		for (TypeDetail fetchedTypeDetail : fetchedTypeDetails) {
			if (typeDetailModelMap.containsKey(fetchedTypeDetail.getType().getFormId())) {
				typeDetailModel = new TypeDetailModel();
				typeDetailModel.setId(fetchedTypeDetail.getId());
				typeDetailModel.setName(fetchedTypeDetail.getName());
				typeDetailModel.setOrderLevel(fetchedTypeDetail.getOrderLevel());
				typeDetailModel.setSlugId(fetchedTypeDetail.getSlugId());
				typeDetailModelMap.get(fetchedTypeDetail.getType().getFormId()).add(typeDetailModel);
			} else {
				typeDetailModel = new TypeDetailModel();
				typeDetailModel.setId(fetchedTypeDetail.getId());
				typeDetailModel.setName(fetchedTypeDetail.getName());
				typeDetailModel.setOrderLevel(fetchedTypeDetail.getOrderLevel());
				typeDetailModel.setSlugId(fetchedTypeDetail.getSlugId());
				listOfTypeDetail = new ArrayList<>();
				listOfTypeDetail.add(typeDetailModel);
				typeDetailModelMap.put(fetchedTypeDetail.getType().getFormId(), listOfTypeDetail);
			}
		}
		reportModel.setFacilityType(typeDetailModelMap);
		List<EnginesForm> enginesForm = formRepository.findAll();
		List<EnginesForm> enginesFormIsActive = new ArrayList<EnginesForm>();
		for (EnginesForm form : enginesForm) {
			if (form.getStatus().equals(Status.ACTIVE)) {
				enginesFormIsActive.add(form);
			}
		}
		reportModel.setEnginesForm(enginesFormIsActive);
		return reportModel;
	}
	
	private Map<String, List<AreaModel>> getAllAreaList() {

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
	
	@Override
	public ResponseEntity<String> getRawDataReport(Integer formId, Integer facilityTypeId, String sDate, String eDate,
			Integer stateId, Integer districtId, Integer blockId, HttpServletResponse response, Boolean inline,
			List<String> submissionIds) throws Exception {

		/*
		 * OAuth2Authentication auth =
		 * (OAuth2Authentication)SecurityContextHolder.getContext().getAuthentication();
		 * UserModel userModelInfo = tokenInfoExtracter.getUserModelInfo(auth);
		 */

		List<AllChecklistFormData> listOfData = null;
		List<Question> listOfQuestions = questionRepository.findAllByFormIdOrderByQuestionOrderAsc(formId);
		EnginesForm form = engineFormRepository.findByFormId(formId);

		String path = directoryPath + "RawData-" + form.getName() + "-" + new Date().getTime() + ".xlsx";

		File newFile = null;
		newFile = new File(directoryPath);
		if (!newFile.exists()) {
			newFile.mkdirs();
		}

		if (new File(path).exists()) {
			new File(path).delete();
		}

		if (submissionIds == null) {

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdfMonthYear.parse(sDate));
			
//			String startDate = String.valueOf(calendar.getActualMinimum(Calendar.DATE)) + "/" + sDate;
//			String startend = String.valueOf(calendar.getActualMaximum(Calendar.DATE)) + "/" + eDate;
			
			
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			String startDateStr = simpleDateformater.format(calendar.getTime());
			
			Date startDate1 = (Date) formatter.parse(startDateStr + " 23:59:59.000");
			
			calendar = Calendar.getInstance();
			calendar.setTime(sdfMonthYear.parse(eDate));
			
			calendar.add(Calendar.MONTH, 0);
			calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

			String endDateStr = simpleDateformater.format(calendar.getTime());
			Date endDate1 = (Date) formatter.parse(endDateStr + " 23:59:59.000");
			
			List<Area> areas = null;

			List<Integer> areaIds = null;

			switch (formId) {
			case 1:
				// facility

				if (stateId == null) {

					areas = mongoAreaRepository.findAllByAreaLevelAreaLevelIdInOrderByAreaIdAsc(Arrays.asList(5));
				} else {
					if (districtId == null) {
						areas = mongoAreaRepository.findByStateIdAndAreaLevelAreaLevelIdOrderByAreaIdAsc(stateId, 5);
					} else {
						if (blockId == null) {
							areas = mongoAreaRepository.findByDistrictIdAndAreaLevelAreaLevelIdOrderByAreaIdAsc(districtId,
									5);
						} else {
							areas = mongoAreaRepository.findByBlockIdAndAreaLevelAreaLevelIdOrderByAreaIdAsc(blockId, 5);
						}
					}
				}
				areaIds = areas.stream().map(v -> new Integer(v.getAreaId())).collect(Collectors.toList());

				if(facilityTypeId == null) {
					listOfData = allChecklistFormDataRepository.findByFormIdAndAreaId(formId, areaIds,
							startDate1, endDate1);
				}else {
					listOfData = allChecklistFormDataRepository.findByFormIdAndFacilityId(formId, areaIds, facilityTypeId,
							startDate1, endDate1);
				}


				break;
			case 2:
				// Community

				if (stateId == null) {

					areas = mongoAreaRepository.findAllByAreaLevelAreaLevelIdInOrderByAreaIdAsc(Arrays.asList(4));
				} else {
					if (districtId == null) {
						areas = mongoAreaRepository.findByStateIdAndAreaLevelAreaLevelIdOrderByAreaIdAsc(stateId, 4);
					} else {
						if (blockId == null) {
							areas = mongoAreaRepository.findByDistrictIdAndAreaLevelAreaLevelIdOrderByAreaIdAsc(districtId,
									4);
						} else {
							areas = mongoAreaRepository.findByAreaIdAndAreaLevelAreaLevelIdOrderByAreaIdAsc(blockId, 4);
						}
					}
				}
				areaIds = areas.stream().map(v -> new Integer(v.getAreaId())).collect(Collectors.toList());

				listOfData = allChecklistFormDataRepository.findByFormIdAndBlockId(formId, areaIds,
						startDate1, endDate1);

				break;
			case 3:
				// District

				if (stateId == null) {

					areas = mongoAreaRepository.findAllByAreaLevelAreaLevelIdInOrderByAreaIdAsc(Arrays.asList(3));
				} else {
					if (districtId == null) {
						areas = mongoAreaRepository.findByStateIdAndAreaLevelAreaLevelIdOrderByAreaIdAsc(stateId, 3);
					} else {
						if (blockId != null) {
							areas = mongoAreaRepository.findByAreaIdAndAreaLevelAreaLevelIdOrderByAreaIdAsc(blockId, 4);
						} else {
							areas = mongoAreaRepository.findByAreaIdAndAreaLevelAreaLevelIdOrderByAreaIdAsc(districtId,
									3);
						}

					}
				}

				areaIds = areas.stream().map(v -> new Integer(v.getAreaId())).collect(Collectors.toList());

				listOfData = allChecklistFormDataRepository.findByFormIdAndDistrictId(formId, areaIds,
						startDate1, endDate1);

				break;
			case 4:
				// Hwc
				if (stateId == null) {

					areas = mongoAreaRepository.findAllByAreaLevelAreaLevelIdInOrderByAreaIdAsc(Arrays.asList(5));
				} else {
					if (districtId == null) {
						areas = mongoAreaRepository.findByStateIdAndAreaLevelAreaLevelIdOrderByAreaIdAsc(stateId, 5);
					} else {
						if (blockId == null) {
							areas = mongoAreaRepository.findByDistrictIdAndAreaLevelAreaLevelIdOrderByAreaIdAsc(districtId,
									5);
						} else {
							areas = mongoAreaRepository.findByBlockIdAndAreaLevelAreaLevelIdOrderByAreaIdAsc(blockId, 5);
						}
					}
				}
				areaIds = areas.stream().map(v -> new Integer(v.getAreaId())).collect(Collectors.toList());

				if(facilityTypeId != null) {
				listOfData = allChecklistFormDataRepository.findByFormIdAndFacilityIdFacilityType(formId, areaIds,
						facilityTypeId, startDate1, endDate1);
				}else {
					listOfData = allChecklistFormDataRepository.findByFormIdAndFacility(formId, areaIds,
							startDate1, endDate1);
				}
				

				break;

			}

		} else {
			listOfData = allChecklistFormDataRepository.findByIdIn(submissionIds);
		}

		if (!listOfData.isEmpty()) {
			try {

				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("ADARSSH");

				CellStyle styleForHeading = ExcelStyleSheet.getStyleForHeading(workbook);
				/*
				 * get style for odd cell
				 */
				CellStyle colStyleOdd = ExcelStyleSheet.getStyleForOddCell(workbook);
				/*
				 * get style for even cell
				 */
				CellStyle colStyleEven = ExcelStyleSheet.getStyleForEvenCell(workbook);
				
				XSSFRow sectionRow = sheet.createRow((short) 0);
				Cell sCell= sectionRow.createCell(0);
				sCell.setCellValue("Section");
				sCell.setCellStyle(styleForHeading);
				
				XSSFRow subSectionRow = sheet.createRow((short) 1);
				Cell subCell= subSectionRow.createCell(0);
				subCell.setCellValue("Sub Section");
				subCell.setCellStyle(styleForHeading);
				
				XSSFRow tableHeaderRow = sheet.createRow((short) 2);
				XSSFRow questionRow = sheet.createRow((short) 3);
				tableHeaderRow.createCell(0);
				questionRow.createCell(0);
				sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
				
				Cell qCell= tableHeaderRow.createCell(0);
				qCell.setCellValue("Question");
				qCell.setCellStyle(styleForHeading);
				XSSFRow columnNameRow = sheet.createRow((short) 4);

				
				Map<String, Integer> sectionMap = new LinkedHashMap<String, Integer>();
				Map<String, Integer> subSectionMap = new LinkedHashMap<String, Integer>();
				Map<String, List<String>> tabularDataMap = new LinkedHashMap<String, List<String>>();
				Map<String, Question> questionMap = new LinkedHashMap<String, Question>();
				Map<String, Map<String, List<Question>>> sectionSubsectionQuestionMap = new LinkedHashMap<String, Map<String, List<Question>>>();
				Map<String, List<Question>> subsectionQuestionMap = new LinkedHashMap<String, List<Question>>();
				List<Question> listOfQuestion = null;
				List<String> listOfString = null;
				for (Question question : listOfQuestions) {

					questionMap.put(question.getColumnName(), question);
					boolean flag = question.getDefaultSettings()==null ? true : (!question.getDefaultSettings().contains("hidden"));
					

					if (!question.getControllerType().equals("tableWithRowWiseArithmetic") && flag ) {

						sectionMap.put(question.getSection().trim(),
								!sectionMap.containsKey(question.getSection().trim()) ? 1
										: sectionMap.get(question.getSection().trim()) + 1);
						subSectionMap
								.put((question.getSubsection().trim() + "-" + question.getSection().trim()).trim(),
										!subSectionMap.containsKey(
												(question.getSubsection().trim() + "-" + question.getSection().trim())
														.trim()) ? 1
																: subSectionMap
																		.get((question.getSubsection().trim() + "-"
																				+ question.getSection().trim()).trim())
																		+ 1);
						/* ========================== */
						if (sectionSubsectionQuestionMap.containsKey(question.getSection().trim())) {
							subsectionQuestionMap = sectionSubsectionQuestionMap.get(question.getSection().trim());
							if (sectionSubsectionQuestionMap.get(question.getSection().trim()).containsKey(
									(question.getSubsection().trim() + "-" + question.getSection().trim()).trim())) {
								listOfQuestion = subsectionQuestionMap.get(
										(question.getSubsection().trim() + "-" + question.getSection().trim()).trim());
								subsectionQuestionMap.get(
										(question.getSubsection().trim() + "-" + question.getSection().trim()).trim())
										.add(question);
							} else {
								listOfQuestion = new ArrayList<>();
								listOfQuestion.add(question);
								subsectionQuestionMap.put(
										(question.getSubsection().trim() + "-" + question.getSection().trim()).trim(),
										listOfQuestion);
							}
						} else {
							subsectionQuestionMap = new LinkedHashMap<String, List<Question>>();
							listOfQuestion = new ArrayList<>();
							if (subsectionQuestionMap.containsKey(
									(question.getSubsection().trim() + "-" + question.getSection().trim()).trim())) {
								subsectionQuestionMap.get(
										(question.getSubsection().trim() + "-" + question.getSection().trim()).trim())
										.add(question);
							} else {
								listOfQuestion.add(question);
								subsectionQuestionMap.put(
										(question.getSubsection().trim() + "-" + question.getSection().trim()).trim(),
										listOfQuestion);
							}
							sectionSubsectionQuestionMap.put(question.getSection().trim(), subsectionQuestionMap);
						}
						/* ====================================== */

						// forTableHader
						if (question.getQuestion().contains("@@split@@")) {
							if (tabularDataMap.containsKey((question.getQuestion().split("@@split@@")[0].trim() + "#"
									+ question.getParentColumnName().trim()).trim())) {
								tabularDataMap
										.get((question.getQuestion().split("@@split@@")[0].trim() + "#"
												+ question.getParentColumnName().trim()).trim())
										.add(question.getQuestion().split("@@split@@")[1].trim());
							} else {
								listOfString = new ArrayList<String>();
								listOfString.add(question.getQuestion().split("@@split@@")[1].trim());
								tabularDataMap.put((question.getQuestion().split("@@split@@")[0].trim() + "#"
										+ question.getParentColumnName().trim()).trim(), listOfString);
							}
						}

					}
				}

				int sectionStartIndex = 1;
				int sectionEndIndex = 0;
				int subSectionStartIndex = 1;
				int subSectionEndIndex = 0;
				int cellIndex = 1;
				Set<String> stringSet = new HashSet<String>();
				for (Entry<String, Map<String, List<Question>>> sectionEntry : sectionSubsectionQuestionMap
						.entrySet()) {

					sectionEndIndex = sectionMap.get(sectionEntry.getKey().trim()) - 1;
					CellRangeAddress cellRangeAddress = 	new CellRangeAddress(0, 0, sectionStartIndex, sectionEndIndex + sectionStartIndex);
					sheet.addMergedRegion(cellRangeAddress);

					RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress , sheet);
					RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress , sheet);
					RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress , sheet);
					RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress , sheet);
					
					XSSFCell sectionCell = sectionRow.createCell(sectionStartIndex);
					sectionCell.setCellValue(sectionEntry.getKey().trim());
					sectionCell.setCellStyle(styleForHeading);
					sectionStartIndex = sectionEndIndex + sectionStartIndex + 1;

					for (Entry<String, List<Question>> subSectionEntry : sectionEntry.getValue().entrySet()) {

						subSectionEndIndex = subSectionMap.get(subSectionEntry.getKey().trim()) - 1;
						if (subSectionMap.get(subSectionEntry.getKey().trim()) != 1) {
							cellRangeAddress = 	new CellRangeAddress(1, 1, subSectionStartIndex,
									subSectionEndIndex + subSectionStartIndex);
							 
							sheet.addMergedRegion(cellRangeAddress);
							
							RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress , sheet);
							RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress , sheet);
							RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress , sheet);
							RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress , sheet);
						}
						XSSFCell subSectionCell = subSectionRow.createCell(subSectionStartIndex);
						String subsection = subSectionEntry.getKey().split("-")[0].trim().split("@")[0].trim()
								.equals(subSectionEntry.getKey().split("-")[0].trim())
										? subSectionEntry.getKey().split("-")[0].trim()
										: subSectionEntry.getKey().split("-")[0].trim().split("@")[1].trim();
						subSectionCell.setCellValue(subsection);
						subSectionCell.setCellStyle(styleForHeading);

						subSectionStartIndex = subSectionEndIndex + subSectionStartIndex + 1;

						for (Question question : subSectionEntry.getValue()) {
							XSSFCell questionCell = null;
							if (!question.getQuestion().contains("@@split@@")) {

								cellRangeAddress = 
										new CellRangeAddress(2, 3, cellIndex, cellIndex);
								sheet.addMergedRegion(cellRangeAddress);
								
								XSSFCell tabHeadrCell = tableHeaderRow.createCell(cellIndex);
								tabHeadrCell = tableHeaderRow.createCell(cellIndex);
								tabHeadrCell.setCellValue(question.getQuestion().trim());
								tabHeadrCell.setCellStyle(styleForHeading);
								tableHeaderRow.setHeightInPoints(80);
								sheet.autoSizeColumn(question.getQuestion().length());
								
								RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress , sheet);
								RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress , sheet);
								RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress , sheet);
								RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress , sheet);
							} else {
								questionCell = questionRow.createCell(cellIndex);
								questionRow.setHeightInPoints(80);
								questionCell.setCellValue(question.getQuestion().split("@@split@@")[1].trim());
								questionCell.setCellStyle(styleForHeading);
								sheet.autoSizeColumn(question.getQuestion().length());
								if (!stringSet.contains((question.getQuestion().split("@@split@@")[0].trim() + "#"
										+ question.getParentColumnName().trim()).trim())) {
									
									cellRangeAddress = 
											new CellRangeAddress(2, 2, cellIndex,
													cellIndex + tabularDataMap
															.get((question.getQuestion().split("@@split@@")[0].trim()
																	+ "#" + question.getParentColumnName()).trim())
															.size() - 1);
									sheet.addMergedRegion(cellRangeAddress);
									RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress , sheet);
									RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress , sheet);
									RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress , sheet);
									RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress , sheet);
									
									XSSFCell tabHeadrCell = tableHeaderRow.createCell(cellIndex);
									tabHeadrCell.setCellValue(question.getQuestion().split("@@split@@")[0].trim());
									tabHeadrCell.setCellStyle(styleForHeading);
									tableHeaderRow.setHeightInPoints(120);
									sheet.autoSizeColumn(question.getQuestion().split("@@split@@")[0].trim().length());
									stringSet.add((question.getQuestion().split("@@split@@")[0].trim() + "#"
											+ question.getParentColumnName().trim()).trim());
								}

							}
							XSSFCell columnNameCell = columnNameRow.createCell(cellIndex);
							columnNameCell.setCellValue(question.getColumnName().trim());
							columnNameCell.setCellStyle(styleForHeading);
							++cellIndex;

						}
					}
				}
				int rowIndex = 5;
				int submissionIndex=0;
				Cell cell;
				Integer columnIndex = 0;
				
				cell = questionRow.createCell(cellIndex);
				cell.setCellValue("Aggregated");
				cell.setCellStyle(styleForHeading);
				
				cell = questionRow.createCell(cellIndex+1);
				cell.setCellValue("Submission Status");
				cell.setCellStyle(styleForHeading);
				
				cell = questionRow.createCell(cellIndex+2);
				cell.setCellValue("Rejection Remark");
				cell.setCellStyle(styleForHeading);
				
				cell = questionRow.createCell(cellIndex+3);
				cell.setCellValue("Submitted By");
				cell.setCellStyle(styleForHeading);

				
				cell = questionRow.createCell(cellIndex+4);
				cell.setCellValue("Submitted On");
				cell.setCellStyle(styleForHeading);

				List<TypeDetail> typeDetailList = typeDetailRepository.findByFormId(formId);
				Map<Integer, TypeDetail> typeDetailMap = typeDetailList.stream()
						.collect(Collectors.toMap(TypeDetail::getSlugId, typeDe -> typeDe));

				List<Area> areaList = mongoAreaRepository.findAll();
				Map<Integer, String> areaMap = areaList.stream()
						.collect(Collectors.toMap(Area::getAreaId, Area::getAreaName));
				cellIndex = 1;
				sheet.getRow(4).setZeroHeight(true);

				
				for (AllChecklistFormData facilityChecklistFormData : listOfData) {
					
					//filter duplicate submissions
					if(facilityChecklistFormData.getDuplicate()!=null && facilityChecklistFormData.getDuplicate()) {
						continue;
					}


					Map<String, Object> mainValue = setInCulomnValue(facilityChecklistFormData.getData(), questionMap,
							areaMap, facilityChecklistFormData.getAttachments(), typeDetailMap);

					XSSFRow dataRow = sheet.createRow((short) rowIndex);
					/*
					 * setting serial number value
					 */
					cell = dataRow.createCell(columnIndex);
					cell.setCellValue(submissionIndex + 1);
					cell.setCellStyle(rowIndex % 2 == 0 ? colStyleEven : colStyleOdd);
					
					Map<String, String> attachedMap = new HashMap<>();
					if (facilityChecklistFormData.getAttachments() != null) {
						for (Entry<String, List<FormAttachmentsModel>> entry : facilityChecklistFormData.getAttachments().entrySet()) {
							entry.getValue().stream().forEach(attachments -> {
								attachedMap.put(attachments.getColumnName().trim(), attachments.getFilePath());
							});
						}
					}

					
					for (int i1 = 1; i1 < sheet.getRow(4).getLastCellNum(); i1++) {
						XSSFCell dataCell = dataRow.createCell(i1);
						if (attachedMap.containsKey((columnNameRow.getCell(i1)).toString().trim())) {

							String value2 = mainValue.get((columnNameRow.getCell(i1)).toString().trim()) == null ? ""
									: mainValue.get((columnNameRow.getCell(i1)).toString().trim()).toString();
							dataCell.setCellType(CellType.FORMULA);
							dataCell.setCellFormula("HYPERLINK(\"" + configurableEnvironment
									.getProperty("sirmncha.baseurl.downloadFile.notification").concat(value2)
									+ "\", \"click here\")");
							dataCell.setCellStyle(rowIndex % 2 == 0 ? colStyleEven : colStyleOdd);

						} else {

							// XSSFCell dataCell = dataRow.createCell(i1);
							String value = mainValue.get((columnNameRow.getCell(i1)).toString().trim()) == null ? "N/A"
									: mainValue.get((columnNameRow.getCell(i1)).toString().trim()).toString();
							dataCell.setCellValue(value.toString());
							dataCell.setCellStyle(rowIndex % 2 == 0 ? colStyleEven : colStyleOdd);
						}

					}
					XSSFCell  dataCell = dataRow.createCell(sheet.getRow(4).getLastCellNum());
					dataCell.setCellValue(facilityChecklistFormData.getIsAggregated() ? "Yes" : "No");
					dataCell.setCellStyle(rowIndex % 2 == 0 ? colStyleEven : colStyleOdd);
					
					dataCell = dataRow.createCell(sheet.getRow(4).getLastCellNum() + 1);
							dataCell.setCellValue(facilityChecklistFormData.getChecklistSubmissionStatus().toString());
					dataCell.setCellStyle(rowIndex % 2 == 0 ? colStyleEven : colStyleOdd);
					
					dataCell = dataRow.createCell(sheet.getRow(4).getLastCellNum() + 2);
					dataCell.setCellValue(facilityChecklistFormData.getRejectMessage() == null ? "N/A"
							: facilityChecklistFormData.getRejectMessage());
					dataCell.setCellStyle(rowIndex % 2 == 0 ? colStyleEven : colStyleOdd);
					
					dataCell = dataRow.createCell(sheet.getRow(4).getLastCellNum() + 3);
					dataCell.setCellValue(facilityChecklistFormData.getSubmittedBy());
					dataCell.setCellStyle(rowIndex % 2 == 0 ? colStyleEven : colStyleOdd);
					
					dataCell = dataRow.createCell(sheet.getRow(4).getLastCellNum() + 4);
					dataCell.setCellValue(sdfTime.format(facilityChecklistFormData.getSyncDate()));
					dataCell.setCellStyle(rowIndex % 2 == 0 ? colStyleEven : colStyleOdd);
					
					rowIndex++;
					submissionIndex++;
				}

				FileOutputStream fileOut = new FileOutputStream(path);
				workbook.write(fileOut);
				fileOut.close();
				workbook.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			downLoadFile(path, response, inline);

			/*if (new File(path).exists()) {
				new File(path).delete();
			}*/
			return new ResponseEntity<>("Success", HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}

	}

	private Map<String, Object> setInCulomnValue(Map<String, Object> dataMap, Map<String, Question> questionMap,
			Map<Integer, String> areaMap, Map<String, List<FormAttachmentsModel>> listOfAttachments,
			Map<Integer, TypeDetail> typeDetailMap) {
		Map<String, Object> mainValue = new HashMap<String, Object>();
		List<Integer> values = null;
		for (Map.Entry<String, Object> data : dataMap.entrySet()) {

			Question question = questionMap.get(data.getKey().trim());

			if (question.getControllerType().equals("dropdown")) {

				if (question.getTableName() != null) {
					switch (question.getTableName().split("\\$\\$")[0].trim()) {
					case "area": {

						mainValue.put(data.getKey(), areaMap.get(data.getValue()));
					}
						break;

					}
				} else if (question.getFieldType().equals("checkbox")) {
					values = data.getValue() != null ? (List<Integer>) data.getValue() : null;
					String value = null;
					if (values != null && !values.isEmpty()) {
						for (Integer integer : values) {
							if (value == null) {
								value.concat(typeDetailMap.get(integer).getName());
							} else {
								value.concat("," + typeDetailMap.get(integer).getName());
							}
						}
					}
					mainValue.put(data.getKey(), value);
				} else {
					if (question.getTypeId() != null) {
						if (data.getValue() != null) {
							mainValue.put(data.getKey(), typeDetailMap.get(data.getValue()).getName());
						}
					}
				}

			} else if (question.getControllerType().equals("tableWithRowWiseArithmetic")) {

				List<Map<String, Object>> tableMapList = (List<Map<String, Object>>) data.getValue();
				for (Map<String, Object> tableMap : tableMapList) {
					for (Map.Entry<String, Object> tableData : tableMap.entrySet()) {
						mainValue.put(tableData.getKey(), tableData.getValue());

					}

				}
			} else if (question.getControllerType().equals("Date Widget")) {
				mainValue.put(data.getKey(), sdf.format(data.getValue()));
			} else {

				if (question.getFieldType().equals("multipleimage")) {

				} else {

					mainValue.put(data.getKey(), data.getValue());
				}
			}
		}
		if (listOfAttachments != null) {

			for (Entry<String, List<FormAttachmentsModel>> entry : listOfAttachments.entrySet()) {
				entry.getValue().stream().forEach(attachments -> {
					mainValue.put(attachments.getColumnName(), attachments.getFilePath());
				});
			}

		}

		return mainValue;
	}

	@Override
	public void getNotification(AllChecklistFormData savesData) throws Exception {
		/* AllChecklistFormData savesData =
		 allChecklistFormDataRepository.findById("5caca5303f37e005509b7280");*/
		List<Area> areaList = mongoAreaRepository.findAll();
		Map<Integer, String> areaMap = areaList.stream().collect(Collectors.toMap(Area::getAreaId, Area::getAreaName));
		String areaName = null;
		switch (savesData.getFormId()) {
		case 1:
			areaName = areaMap.get(savesData.getData().get("f1_facility_name"));
			break;
		case 2:
			areaName = areaMap.get(savesData.getData().get("f2qblock"));
			break;
		case 3:
			areaName = areaMap.get(savesData.getData().get("f3q6_district"));
			break;
		case 4:
			areaName = areaMap.get(savesData.getData().get("hwc8_facility_name"));
			break;

		default:
			break;
		}

		Account account = accountRepository.findByUserName(savesData.getUserName());
		EnginesForm form = engineFormRepository.findByFormId(savesData.getFormId());

		String fileName = (form.getName() + "-" + account.getUserName() + "_" + areaName +"_"+new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date())+ ".xlsx").replace("/", "");
		String path = directoryPath + "notification/";

		File newFile = null;
		newFile = new File(path);

		if (!newFile.exists()) {
			newFile.mkdirs();
		}

		if (new File(path + fileName).exists()) {
			new File(path + fileName).delete();

		}
		List<Question> listOfQuestions = questionRepository
				.findAllByFormIdOrderByQuestionOrderAsc(savesData.getFormId());

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Data");

		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setVerticalAlignment(VerticalAlignment.TOP);

		Map<String, Integer> sectionMap = new LinkedHashMap<String, Integer>();
		Map<String, Integer> subSectionMap = new LinkedHashMap<String, Integer>();
		Map<String, List<String>> tabularDataMap = new LinkedHashMap<String, List<String>>();
		Map<String, Question> questionMap = new LinkedHashMap<String, Question>();
		Map<String, Map<String, List<Question>>> sectionSubsectionQuestionMap = new LinkedHashMap<String, Map<String, List<Question>>>();
		Map<String, List<Question>> subsectionQuestionMap = new LinkedHashMap<String, List<Question>>();
		List<Question> listOfQuestion = null;
		List<String> listOfString = null;
		try {

			for (Question question : listOfQuestions) {
				questionMap.put(question.getColumnName().trim(), question);

				boolean flag = question.getDefaultSettings()==null ? true : (!question.getDefaultSettings().contains("hidden"));
				if (!question.getControllerType().equals("tableWithRowWiseArithmetic") && flag ) {

					sectionMap.put(question.getSection().trim(),
							!sectionMap.containsKey(question.getSection().trim()) ? 1
									: sectionMap.get(question.getSection().trim()) + 1);
					subSectionMap
							.put((question.getSubsection().trim() + "-" + question.getSection().trim()).trim(),
									!subSectionMap.containsKey(
											(question.getSubsection().trim() + "-" + question.getSection().trim())
													.trim()) ? 1
															: subSectionMap
																	.get((question.getSubsection().trim() + "-"
																			+ question.getSection().trim()).trim())
																	+ 1);

					/* ========================== */
					if (sectionSubsectionQuestionMap.containsKey(question.getSection().trim())) {
						subsectionQuestionMap = sectionSubsectionQuestionMap.get(question.getSection().trim());
						if (sectionSubsectionQuestionMap.get(question.getSection().trim()).containsKey(
								(question.getSubsection().trim() + "-" + question.getSection().trim()).trim())) {
							listOfQuestion = subsectionQuestionMap
									.get((question.getSubsection().trim() + "-" + question.getSection().trim()).trim());
							subsectionQuestionMap
									.get((question.getSubsection().trim() + "-" + question.getSection().trim()).trim())
									.add(question);
						} else {
							listOfQuestion = new ArrayList<>();
							listOfQuestion.add(question);
							subsectionQuestionMap.put(
									(question.getSubsection().trim() + "-" + question.getSection().trim()).trim(),
									listOfQuestion);
						}
					} else {
						subsectionQuestionMap = new LinkedHashMap<String, List<Question>>();
						listOfQuestion = new ArrayList<>();
						if (subsectionQuestionMap.containsKey(
								(question.getSubsection().trim() + "-" + question.getSection().trim()).trim())) {
							subsectionQuestionMap
									.get((question.getSubsection().trim() + "-" + question.getSection().trim()).trim())
									.add(question);
						} else {
							listOfQuestion.add(question);
							subsectionQuestionMap.put(
									(question.getSubsection().trim() + "-" + question.getSection().trim()).trim(),
									listOfQuestion);
						}
						sectionSubsectionQuestionMap.put(question.getSection().trim(), subsectionQuestionMap);
					}
					/* ====================================== */

					// forTableHader
					if (question.getQuestion().contains("@@split@@")) {
						if (tabularDataMap.containsKey((question.getQuestion().split("@@split@@")[0].trim() + "#"
								+ question.getParentColumnName()).trim())) {
							tabularDataMap
									.get((question.getQuestion().split("@@split@@")[0].trim() + "#"
											+ question.getParentColumnName()).trim())
									.add(question.getQuestion().split("@@split@@")[1].trim());
						} else {
							listOfString = new ArrayList<String>();
							listOfString.add(question.getQuestion().split("@@split@@")[1].trim());
							tabularDataMap.put((question.getQuestion().split("@@split@@")[0].trim() + "#"
									+ question.getParentColumnName()).trim(), listOfString);
						}
					}

				}

			}
			CellStyle styleForHeading = ExcelStyleSheet.getStyleForHeading(workbook);
			
			/*
			 * get style for odd cell
			 */
			CellStyle colStyleOdd = ExcelStyleSheet.getStyleForOddCell(workbook);
			/*
			 * get style for even cell
			 */
			CellStyle colStyleEven = ExcelStyleSheet.getStyleForEvenCell(workbook);
			
			
			XSSFRow headdingRow = sheet.createRow((short) 0);
			XSSFCell sectionCell = headdingRow.createCell(0);
			sectionCell.setCellValue("Section");
			sectionCell.setCellStyle(styleForHeading);
			
			XSSFCell subSectionCell = headdingRow.createCell(1);
			subSectionCell.setCellValue("Sub Section");
			subSectionCell.setCellStyle(styleForHeading);
			
			XSSFCell questionsCell = headdingRow.createCell(2);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 3));
			questionsCell.setCellValue("Question");
			questionsCell.setCellStyle(styleForHeading);
			
			XSSFCell responseCell = headdingRow.createCell(5);
			responseCell.setCellValue("Response");
			responseCell.setCellStyle(styleForHeading);

			int sectionStartIndex = 1;
			int sectionEndIndex = 0;
			int subSectionStartIndex = 1;
			int subSectionEndIndex = 0;
			int cellIndex = 1;
			Set<String> stringSet = new HashSet<String>();
			for (Entry<String, Map<String, List<Question>>> sectionEntry : sectionSubsectionQuestionMap.entrySet()) {

				XSSFRow sectionRow = sheet.getRow(sectionStartIndex) !=null ? sheet.getRow(sectionStartIndex) : sheet.createRow(sectionStartIndex);
				sectionEndIndex = sectionMap.get(sectionEntry.getKey().trim()) - 1;
				
				CellRangeAddress cellRangeAddress = 	new CellRangeAddress(sectionStartIndex, sectionEndIndex + sectionStartIndex, 0, 0);
				sheet.addMergedRegion(cellRangeAddress);
				
				
				RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress , sheet);
				RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress , sheet);
				RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress , sheet);
				RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress , sheet);
				
				sectionCell = sectionRow.createCell(0);
				sectionCell.setCellValue(sectionEntry.getKey().trim());
				sectionCell.setCellStyle(styleForHeading);
				sectionStartIndex = sectionEndIndex + sectionStartIndex + 1;

				for (Entry<String, List<Question>> subSectionEntry : sectionEntry.getValue().entrySet()) {
					XSSFRow subSsectionRow = sheet.getRow(subSectionStartIndex) != null ? sheet.getRow(subSectionStartIndex) : sheet.createRow(subSectionStartIndex);
					
					subSectionEndIndex = subSectionMap.get(subSectionEntry.getKey().trim()) - 1;
					if (subSectionMap.get(subSectionEntry.getKey().trim()) != 1) {
						cellRangeAddress = new CellRangeAddress(subSectionStartIndex,
								subSectionEndIndex + subSectionStartIndex, 1, 1);
						sheet.addMergedRegion(cellRangeAddress);
						RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress , sheet);
						RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress , sheet);
						RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress , sheet);
						RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress , sheet);
					}
					subSectionCell = subSsectionRow.createCell(1);
					String subsection = subSectionEntry.getKey().split("-")[0].trim().split("@")[0].trim()
							.equals(subSectionEntry.getKey().split("-")[0].trim())
									? subSectionEntry.getKey().split("-")[0].trim()
									: subSectionEntry.getKey().split("-")[0].trim().split("@")[1].trim();
					subSectionCell.setCellValue(subsection);
					subSectionCell.setCellStyle(styleForHeading);

					subSectionStartIndex = subSectionEndIndex + subSectionStartIndex + 1;
					for (Question question : subSectionEntry.getValue()) {
						XSSFCell questionCell = null;
						if (!question.getQuestion().contains("@@split@@")) {
							XSSFRow questionRow = sheet.getRow(cellIndex) !=null ? sheet.getRow(cellIndex) : sheet.createRow(cellIndex);
							questionRow.setHeightInPoints(30);
							questionCell = questionRow.createCell(3);
							questionCell.setCellValue(question.getQuestion().trim());
							questionCell.setCellStyle(cellIndex % 2 == 0 ? colStyleEven : colStyleOdd);
							sheet.autoSizeColumn(question.getQuestion().length());
						} else {
							XSSFRow questionRow = sheet.getRow(cellIndex) !=null ? sheet.getRow(cellIndex) : sheet.createRow(cellIndex);
							questionRow.setHeightInPoints(30);
							questionCell = questionRow.createCell(3);
							questionCell.setCellValue(question.getQuestion().split("@@split@@")[1].trim());
							questionCell.setCellStyle(cellIndex % 2 == 0 ? colStyleEven : colStyleOdd);
							sheet.autoSizeColumn(question.getQuestion().length());
							if (!stringSet.contains((question.getQuestion().split("@@split@@")[0].trim() + "#"
									+ question.getParentColumnName()).trim())) {
								cellRangeAddress =new CellRangeAddress(cellIndex, (cellIndex
										+ tabularDataMap.get((question.getQuestion().split("@@split@@")[0].trim() + "#"
												+ question.getParentColumnName()).trim()).size()
										- 1), 2, 2);
								sheet.addMergedRegion(cellRangeAddress);
								RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress , sheet);
								RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress , sheet);
								RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress , sheet);
								RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress , sheet);
								
								XSSFRow headerRow = sheet.getRow(cellIndex);
								XSSFCell tabHeadrCell = headerRow.createCell(2);
								tabHeadrCell.setCellValue(question.getQuestion().split("@@split@@")[0].trim());
								tabHeadrCell.setCellStyle(styleForHeading);
								stringSet.add((question.getQuestion().split("@@split@@")[0].trim() + "#"
										+ question.getParentColumnName()).trim());
							}

						}
						XSSFRow columnName = sheet.getRow(cellIndex);
						XSSFCell columnNameCell = columnName.createCell(4);
						columnNameCell.setCellValue(question.getColumnName().trim());
						columnNameCell.setCellStyle(styleForHeading);
						++cellIndex;

					}
				}
			}
			
			
			XSSFRow statusRow = sheet.createRow(++cellIndex);
			XSSFCell statusCell = statusRow.createCell(3);
			statusCell.setCellValue("STATUS");
			statusCell.setCellStyle(styleForHeading);
			
			sheet.setColumnHidden(4, true);
			List<TypeDetail> typeDetailList = typeDetailRepository.findByFormId(savesData.getFormId());
			Map<Integer, TypeDetail> typeDetailMap = typeDetailList.stream()
					.collect(Collectors.toMap(TypeDetail::getSlugId, typeDe -> typeDe));

			Map<String, Object> mainValue = setInCulomnValue(savesData.getData(), questionMap, areaMap,
					savesData.getAttachments(), typeDetailMap);

			Map<String, String> attachedMap = new HashMap<>();
			if (savesData.getAttachments() != null) {
				for (Entry<String, List<FormAttachmentsModel>> entry : savesData.getAttachments().entrySet()) {
					entry.getValue().stream().forEach(attachments -> {
						attachedMap.put(attachments.getColumnName().trim(), attachments.getFilePath());
					});
				}
			}
			int i1 = 1;
			for ( ; i1 <= sheet.getLastRowNum(); i1++) {
				XSSFRow valueRow = sheet.getRow(i1)!= null ? sheet.getRow(i1) : sheet.createRow(i1);
				XSSFCell dataCell = valueRow.createCell(5);
				if (valueRow.getCell(4) != null) {
					if (attachedMap.containsKey(valueRow.getCell(4).toString())) {
						String value2 = mainValue.get((valueRow.getCell(4)).toString().trim()) == null ? ""
								: mainValue.get((valueRow.getCell(4)).toString().trim()).toString();
						dataCell.setCellType(CellType.FORMULA);
						dataCell.setCellFormula("HYPERLINK(\"" + configurableEnvironment
								.getProperty("sirmncha.baseurl.downloadFile.notification").concat(value2)
								+ "\", \"click here\")");
						
						dataCell.setCellStyle(i1 % 2 == 0 ? colStyleEven : colStyleOdd);
					} else {
						String value = mainValue.get((valueRow.getCell(4)).toString().trim()) == null ? "N/A"
								: mainValue.get((valueRow.getCell(4)).toString().trim()).toString();
						dataCell.setCellValue(value.toString());
						dataCell.setCellStyle(i1 % 2 == 0 ? colStyleEven : colStyleOdd);
					}
				}

			}
			
			 statusCell = statusRow.createCell(5);
			 statusCell	.setCellValue(savesData.getChecklistSubmissionStatus().toString());
			 statusCell.setCellStyle(styleForHeading);
			 
			FileOutputStream fileOut = new FileOutputStream((path + fileName));
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			// mailSending

			if (account.getEmail() != null) {
				Mail mailModel = new Mail();

				Map<String, String> attachments = new HashMap<>();
				attachments.put(fileName, path);

				mailModel.setToEmailIds(Arrays.asList(account.getEmail()));
				mailModel.setFromUserName(
						"RMNCHA Admin" + "\n" + "\n" + configurableEnvironment.getProperty("email.disclaimer"));
				mailModel.setSubject("ADARSSH: Submission for " + form.getName());
				mailModel.setMessage("\n" + "Please find attached the submission file - " + form.getName() + " for  "
						+ areaName + ".");
				mailModel.setToUserName("Dear User");
				mailModel.setEmail(account.getEmail());
				mailModel.setAttachments(attachments);
				sendSimpleMessage(mailModel, (path + fileName));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sendSimpleMessage(Mail mail, String filePath) {
		try {

			MimeMessage msg = emailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			helper.setTo(mail.getEmail());
			helper.setSubject(mail.getSubject());
			helper.setText(
					mail.getToUserName() + "\n" + mail.getMessage() + "\n" + "\n" + "\n" + mail.getFromUserName());

			FileSystemResource file = new FileSystemResource(new File(filePath));
			helper.addAttachment(file.getFilename(), file);
			emailSender.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public String updateTpInSubmission() {
		
		List<AllChecklistFormData> allChecklistFormDatas = allChecklistFormDataRepository.findAll();
		
		List<AllChecklistFormData> updatedTpSubmissions = new ArrayList<>();
		
		for (AllChecklistFormData allChecklistFormData : allChecklistFormDatas) {
			TimePeriod tp = timePeriodRepository.findByTimePeriodId(allChecklistFormData.getTimePeriod().getTimePeriodId());
			
			allChecklistFormData.setTimePeriod(tp);
			updatedTpSubmissions.add(allChecklistFormData);
		}
		
		allChecklistFormDataRepository.save(updatedTpSubmissions);
		
		return "tps updated";
	}

}
