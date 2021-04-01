package org.sdrc.rmncha.service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.poi.ss.usermodel.BorderStyle;
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
import org.sdrc.rmncha.rabbitMQ.AllChecklistFormDataEvent;
import org.sdrc.rmncha.repositories.AreaRepository;
import org.sdrc.rmncha.util.ExcelStyleSheet;
import org.sdrc.rmncha.util.Mail;
import org.sdrc.usermgmt.mongodb.domain.Account;
import org.sdrc.usermgmt.mongodb.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.co.sdrc.sdrcdatacollector.document.EnginesForm;
import in.co.sdrc.sdrcdatacollector.document.Question;
import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import in.co.sdrc.sdrcdatacollector.models.FormAttachmentsModel;
import in.co.sdrc.sdrcdatacollector.mongorepositories.EngineFormRepository;
import in.co.sdrc.sdrcdatacollector.mongorepositories.QuestionRepository;
import in.co.sdrc.sdrcdatacollector.mongorepositories.TypeDetailRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ReportServiceImpl implements ReportService {

	@Autowired
	private AreaRepository mongoAreaRepository;

	@Autowired
	private TypeDetailRepository typeDetailRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private EngineFormRepository engineFormRepository;

	@Value("${ramncha.photoIdfilepath}")
	private String photoIdfilepath;

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Autowired
	@Qualifier("mongoAccountRepository")
	private AccountRepository accountRepository;

	@Autowired
	public JavaMailSender emailSender;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

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
	public void getNotification(AllChecklistFormDataEvent savesData) throws Exception {
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
		String path = photoIdfilepath + "notification/";

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

}
