package org.sdrc.rmncha.service;

import static java.util.Comparator.comparing;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateUtils;
import org.sdrc.rmncha.domain.AllChecklistFormData;
import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmncha.domain.NotificationDetail;
import org.sdrc.rmncha.domain.TimePeriod;
import org.sdrc.rmncha.domain.UserDetails;
import org.sdrc.rmncha.model.ChecklistSubmissionStatus;
import org.sdrc.rmncha.model.DateModel;
import org.sdrc.rmncha.model.FormModel;
import org.sdrc.rmncha.model.NotificationModel;
import org.sdrc.rmncha.model.UserModel;
import org.sdrc.rmncha.model.ValueObject;
import org.sdrc.rmncha.rabbitMQ.CollectionChannel;
import org.sdrc.rmncha.rabbitMQ.SubmissionActionEvent;
import org.sdrc.rmncha.repositories.AllChecklistFormDataRepository;
import org.sdrc.rmncha.repositories.AreaRepository;
import org.sdrc.rmncha.repositories.CustomAccountRepository;
import org.sdrc.rmncha.repositories.EnginesFormRepository;
import org.sdrc.rmncha.repositories.NotificationDetailRepository;
import org.sdrc.rmncha.util.Mail;
import org.sdrc.rmncha.util.TokenInfoExtracter;
import org.sdrc.usermgmt.mongodb.domain.Account;
import org.sdrc.usermgmt.mongodb.repository.AccountRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import in.co.sdrc.sdrcdatacollector.document.EnginesForm;
import in.co.sdrc.sdrcdatacollector.document.Question;
import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import in.co.sdrc.sdrcdatacollector.engine.FormsServiceImpl;
import in.co.sdrc.sdrcdatacollector.handlers.ICameraAndAttachmentsDataHandler;
import in.co.sdrc.sdrcdatacollector.handlers.IDbFetchDataHandler;
import in.co.sdrc.sdrcdatacollector.handlers.IDbReviewQueryHandler;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.DataObject;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;
import in.co.sdrc.sdrcdatacollector.models.ReviewPageModel;
import in.co.sdrc.sdrcdatacollector.mongorepositories.QuestionRepository;
import in.co.sdrc.sdrcdatacollector.mongorepositories.TypeDetailRepository;
import in.co.sdrc.sdrcdatacollector.util.EngineUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SubmissionManagementServiceImpl implements SubmissionManagementService {

	@Autowired
	private TokenInfoExtracter tokenInfoExtracter;

	@Autowired
	private EnginesFormRepository enginesFormRepository;

	@Autowired
	private SubmissionService submissionService;

	@Autowired
	private AllChecklistFormDataRepository allChecklistFormDataRepository;
	
	@Autowired
	@Qualifier("mongoAccountRepository")
	private AccountRepository accountRepository;
	
	@Autowired
	private IDbReviewQueryHandler iDbReviewQueryHandler;

	@Autowired
	private TypeDetailRepository typeDetailRepository;
	
	@Autowired
	private IDbFetchDataHandler iDbFetchDataHandler;

	@Autowired
	private QuestionRepository questionRepository;
	
	private SimpleDateFormat sdfDateTimeWithSeconds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private EngineUtils engineUtils;
	
	@Autowired
	private ICameraAndAttachmentsDataHandler iCameraDataHandler;
	
	@Autowired
	private FormsServiceImpl formsServiceImpl;
	
	@Autowired
	private CustomAccountRepository customAccountRepository;
	
	private final String BEGIN_REPEAT = "beginrepeat";
	
	private DateFormat ymdDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private AreaRepository mongoAreaRepository;
	
	@Autowired
	private ConfigurableEnvironment configurableEnvironment;
	
//	@Autowired
//	private MailService mailService;
	
	@Autowired
	private NotificationDetailRepository notificationDetailRepository;
	
	@Autowired
	private CollectionChannel collectionChannel;
	/**
	 * get all the form associated to logged in user role for review
	 */
	@Override
	public List<FormModel> getAllForms(OAuth2Authentication auth) {

		List<FormModel> formModelList = new ArrayList<>();

		List<EnginesForm> enginesForms = enginesFormRepository.findAll();

		for (EnginesForm form : enginesForms) {

			FormModel model = new FormModel();
			model.setFormId(form.getFormId());
			model.setFormName(form.getName());
			model.setId(form.getId());
			formModelList.add(model);
		}
		return formModelList;
	}

	private DateModel getRejectionDates(Date date, Integer deadlineDays) {

		DateModel model = new DateModel();

		Calendar cal = Calendar.getInstance();

		cal.setTime(date);

		cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));

		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MILLISECOND, 0);

		model.setStartDate(cal.getTime());

		cal.set(Calendar.DATE, deadlineDays);

		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 59);

		model.setEndDate(cal.getTime());

		return model;
	}

	@Override
	public ReviewPageModel getReviewData(ReviewPageModel model) {

		Map<Integer, List<DataObject>> reviewDataMap = model.getReviewDataMap();

		Integer formId = null;
		Date currentDate = new Date();

		Map<Integer, NotificationModel> formIdNotificationMap = submissionService.findAllNotificationDetail();

		for (Map.Entry<Integer, List<DataObject>> map : reviewDataMap.entrySet()) {
			formId = map.getKey();
		}
		List<DataObject> dataList = null;

		if (formId != null) {
			try {

				dataList = reviewDataMap.get(formId);

				// filter all the rejected data from the dataList
				dataList = dataList.stream().filter(data -> data.getRejected() == false).collect(Collectors.toList());

				for (DataObject data : dataList) {

					Map<String, Object> extraKeys = data.getExtraKeys();
					Date dateOfVisit = new SimpleDateFormat("dd-MM-yyyy")
							.parse(((String) extraKeys.get("dateOfVisit")));
					
					TimePeriod dateOfVistiTimePeriod = submissionService.getCurrentTimePeriod(dateOfVisit);

					TimePeriod currentTimePeriod = submissionService.getCurrentTimePeriod(currentDate);

					if (dateOfVistiTimePeriod.equals(currentTimePeriod)) {
						extraKeys.put("isRejectable", false);
					} else {
						/**
						 * timeperiods are not equal then check the current date is in between 1 to 25th
						 * (both inclusive)
						 */
						DateModel rejectionDates = getRejectionDates(currentDate, formIdNotificationMap.get(formId).getDeadlineDays());
						
						if ((DateUtils.isSameDay(currentDate, rejectionDates.getStartDate())
								|| (currentDate.after(rejectionDates.getStartDate()))
										&& (DateUtils.isSameDay(currentDate, rejectionDates.getEndDate())
												|| currentDate.before(rejectionDates.getEndDate()))))
							extraKeys.put("isRejectable", true);
						else
							extraKeys.put("isRejectable", false);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

		return model;
	}
	
	@Override
//	@Caching(evict = { @CacheEvict(value = "formdata", key = "#valueObject.formId")})
	public ResponseEntity<String> rejectSubmissions(ValueObject valueObject, OAuth2Authentication auth) {

		try {
			UserModel user = tokenInfoExtracter.getUserModelInfo(auth);

			Account acc = accountRepository.findById(user.getUserId());
			
			List<Account> accountList = accountRepository.findAll();
			Map<String, Account> accountMap = accountList.stream().collect(Collectors.toMap(Account::getUserName, Function.identity()));
			
			List<Area> areaList = mongoAreaRepository.findAll();
			Map<Integer, String> areaMap = areaList.stream().collect(Collectors.toMap(Area::getAreaId, Area::getAreaName));
			
			

			Gson gson = new Gson();

			switch (valueObject.getFormId()) {

			case 1:
			case 2:
			case 3:
			case 4:{

				List<AllChecklistFormData> dataSubmit = allChecklistFormDataRepository.findByIdIn(valueObject.getSubmissionList());
				dataSubmit.forEach(data -> {
					data.setRejected(valueObject.getIsRejected());
					data.setRejectedApprovedDate(new Date());
					data.setActionByUserName(acc.getUserName());
					data.setActionByFullName(((UserDetails) acc.getUserDetails()).getFirstName()+" "+((UserDetails) acc. getUserDetails()).getLastName());
					
					if(valueObject.getIsRejected()) {
						data.setRejectMessage(valueObject.getMessage());
						data.setChecklistSubmissionStatus(ChecklistSubmissionStatus.REJECTED);
						
						//if mail exist then send email
						if (accountMap.get(data.getUserName()).getEmail() != null
								&& !accountMap.get(data.getUserName()).getEmail().isEmpty()) {
							sendRejectionMail(data,accountMap,areaMap,valueObject.getMessage());
						}
						
						
					}else {
						data.setIsValid(true);
						data.setChecklistSubmissionStatus(ChecklistSubmissionStatus.APPROVED);
					}
					
				});

				AllChecklistFormData savedFormData=	allChecklistFormDataRepository.save(dataSubmit).get(0);
				
				SubmissionActionEvent targetEvent = new SubmissionActionEvent();
				targetEvent.setSubmissionId(valueObject.getSubmissionList().get(0));
				
				BeanUtils.copyProperties(savedFormData, targetEvent);
				
				//update the status of submission in dashboard service
				collectionChannel.approveRejectChannel().send(MessageBuilder.withPayload(targetEvent).build());
				
				log.info("Action : Rejection/approval of data successfull with payload {}",valueObject);
				return new ResponseEntity<String>(gson.toJson("Submission "+(valueObject.getIsRejected()?"rejected":"approved")+" successfully."), HttpStatus.OK);
			}

			}
			log.error("Action : Rejection/approval of data with payload {}",valueObject);
			throw new RuntimeException("invalid formId");
		} catch (Exception e) {
			log.error("Action : while rejecting/approving submission with payload {}", valueObject, e);
			throw new RuntimeException(e);
		}

	}
	
	private void sendRejectionMail(AllChecklistFormData data, Map<String, Account> accountMap, Map<Integer, String> areaMap, String message) {
		
		String areaName=null;
        switch (data.getFormId()) {
		case 1:
			areaName=areaMap.get(data.getData().get("f1_facility_name"));
			break;
		case 2:
			areaName=areaMap.get(data.getData().get("f2qblock"));
			break;
		case 3:
			areaName=areaMap.get(data.getData().get("f3q6_district"));
			break;
		case 4:
			areaName=areaMap.get(data.getData().get("hwc8_facility_name"));
			break;

		default:
			break;
		}
		
		Mail mailModel = new Mail();
		mailModel.setToEmailIds(Arrays.asList(accountMap.get(data.getUserName()).getEmail().toString()));
		
		mailModel.setSubject("ADARSSH: Submission Rejected");
		mailModel.setToUserName("Dear "+((UserDetails) accountMap.get(data.getUserName()).getUserDetails()).getFirstName()+",");
		mailModel.setMessage( "\n"+"Your submitted data has been rejected due to "+ message +" by the admin for area: "+ areaName +". Please get in touch with the admin if you might require any further information."+ "\n" + "\n" );
		
		mailModel.setFromUserName("Thank you!" + "\n"+ "\n"+ configurableEnvironment.getProperty("email.donot.reply")  + "\n"+ configurableEnvironment.getProperty("email.disclaimer"));
		mailModel.setEmail(accountMap.get(data.getUserName()).getEmail().toString());
		
		
		//publish email in rabbimq
		
		collectionChannel.sendEmailChannel().send(MessageBuilder.withPayload(mailModel).build());
		
	/*	ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

		emailExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
		mailService.sendSimpleMessage(mailModel);
				} catch (Exception e) {
				}
			}
		});
		emailExecutor.shutdown();*/
		
	}

	@Override
//	@Cacheable(value="formData", key = "#formId")
	public List<DataObject> getReiewData(Integer formId, UserModel user, Map<String, Object> paramKeyValMap) {
		
		EnginesForm form = enginesFormRepository.findByFormId(formId);
		
		List<TypeDetail> typeDetails = typeDetailRepository.findByFormId(form.getFormId());

		Map<Integer, TypeDetail> typeDetailsMap = typeDetails.stream()
				.collect(Collectors.toMap(TypeDetail::getSlugId, typeDe -> typeDe));
		
		List<DataModel> submissionDatas = iDbFetchDataHandler.fetchDataFromDb(form, "dataReview", null, new Date(),
				new Date(), paramKeyValMap, null, user);
		
		List<String> userIds = submissionDatas.stream().map(DataModel::getUserId).collect(Collectors.toList());
		
		List<Account> users = customAccountRepository.findByIdIn(userIds);
		Map<String,String> userIdNameMap = new HashMap<>();
		
		users.forEach(u->{
			userIdNameMap.put(u.getId(),((UserDetails) u.getUserDetails()).getFirstName()+" "+((UserDetails) u.getUserDetails()).getLastName());
		});
		List<DataObject> dataObjects = new ArrayList<>();
	
		
		for(DataModel submissionData : submissionDatas){
			
			DataObject dataObject = new DataObject();
			dataObject.setFormId(submissionData.getFormId());
			dataObject.setTime(new Timestamp(((Date)submissionData.getExtraKeys().get("syncDate")).getTime()));
			dataObject.setUsername(submissionData.getUserName());
			dataObject.setExtraKeys(submissionData.getExtraKeys());
			dataObject.setCreatedDate(sdfDateTimeWithSeconds.format(submissionData.getCreatedDate()));
			dataObject.setUpdatedDate(sdfDateTimeWithSeconds.format(submissionData.getUpdatedDate()));
			dataObject.setUniqueId(submissionData.getUniqueId());
			dataObject.setRejected(submissionData.isRejected());
			dataObject.setUniqueName(submissionData.getUniqueName());
			
			
			List<Question> questionList = questionRepository
					.findAllByFormIdAndFormVersionAndActiveTrueOrderByQuestionOrderAsc(form.getFormId(),
							submissionData.getFormVersion());
			
			for (Question question : questionList) {
				iDbReviewQueryHandler.setReviewHeaders(dataObject, question, typeDetailsMap, submissionData,
						"dataReview_" + userIdNameMap.get(submissionData.getUserId()));
			}
			
			dataObjects.add(dataObject);
		}
		Collections.sort(dataObjects, comparing(DataObject::getTime).reversed());
		return dataObjects;
	}
	
	@Override
	@Cacheable(value="submissionData", key = "#submissionId")
	public Map<String, List<Map<String, List<QuestionModel>>>> getViewMoreDataForReview(Integer formId, UserModel user, String submissionId,Map<String, Object> paramKeyValMap,HttpSession session) {
		
		DataModel submissionData = iDbFetchDataHandler.getSubmittedData(submissionId,formId);
	
		EnginesForm form = enginesFormRepository.findByFormId(formId);
		
		List<TypeDetail> typeDetails = typeDetailRepository.findByFormId(form.getFormId());

		Map<Integer, TypeDetail> typeDetailsMap = typeDetails.stream()
				.collect(Collectors.toMap(TypeDetail::getSlugId, typeDe -> typeDe));
		
		Map<String, List<Map<String, List<QuestionModel>>>> mapOfSectionSubsectionListOfQuestionModel = new LinkedHashMap<>();

		List<QuestionModel> listOfQuestionModel = new LinkedList<>();

		Map<String, Map<String, List<QuestionModel>>> sectionMap = new LinkedHashMap<String, Map<String, List<QuestionModel>>>();
		Map<String, List<QuestionModel>> subsectionMap = null;

		/**
		 * for accordion
		 */

		QuestionModel questionModel = null;

		List<Question> questionList = questionRepository
				.findAllByFormIdAndFormVersionAndActiveTrueOrderByQuestionOrderAsc(form.getFormId(),
						submissionData.getFormVersion());

		Map<String, Question> questionMap = questionList.stream()
				.collect(Collectors.toMap(Question::getColumnName, question -> question));

		for (Question question : questionList) {

			questionModel = null;
			switch (question.getControllerType()) {
			case "Date Widget":
				if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {
					questionModel = engineUtils.prepareQuestionModel(question);
					if(submissionData.getData().get(question.getColumnName()) instanceof Date) {
						questionModel.setValue(ymdDateFormat.format(submissionData.getData().get(question.getColumnName())));
					}else {
						if (String.class.cast(submissionData.getData().get(question.getColumnName())) != null) {
							String dt = formsServiceImpl.getDateFromString(
									String.class.cast(submissionData.getData().get(question.getColumnName())));
							questionModel.setValue(dt);
						} else
							questionModel.setValue(null);
					}
					
					
				}
				break;
			case "Time Widget":
				if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {
					questionModel = engineUtils.prepareQuestionModel(question);
					questionModel
							.setValue(String.class.cast(submissionData.getData().get(question.getColumnName())));
				}
				break;

			case "checkbox": {
				if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {

					questionModel = engineUtils.prepareQuestionModel(question);

					// setting model
					if (submissionData != null) {
						questionModel = engineUtils.setTypeDetailsAsOptions(questionModel, typeDetailsMap, question,
								String.class.cast(submissionData.getData().get(question.getColumnName())), user,
								paramKeyValMap, session);
					} else {
						questionModel = engineUtils.setTypeDetailsAsOptions(questionModel, typeDetailsMap, question,
								null, user, paramKeyValMap, session);
					}
				}
			}
				break;
			case "Month Widget":
			case "checklist-score-keeper":
			case "textbox":
			case "textarea":
			case "geolocation": {
				if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {

					questionModel = engineUtils.prepareQuestionModel(question);
					switch (question.getFieldType()) {

					case "singledecimal":
					case "doubledecimal":
					case "threedecimal":
						questionModel.setValue(submissionData.getData().get(question.getColumnName()) != null
								? String.valueOf(submissionData.getData().get(question.getColumnName()).toString())
								: null);
						break;

					case "tel":
						questionModel.setValue(submissionData.getData().get(question.getColumnName()) != null
								? Long.parseLong(submissionData.getData().get(question.getColumnName()).toString())
								: "N/A");

						break;
					default:
						questionModel.setValue(submissionData.getData().get(question.getColumnName()) != null
								? String.valueOf(submissionData.getData().get(question.getColumnName()).toString())
								: "N/A");
						break;
					}

				}
			}
				break;

			case "dropdown":
			case "segment": {
				switch (question.getFieldType()) {

				case "checkbox":
					if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {

						questionModel = engineUtils.prepareQuestionModel(question);

						// setting model
						if (submissionData != null) {
							if (submissionData.getData().get(question.getColumnName()) != null && submissionData
									.getData().get(question.getColumnName()) instanceof ArrayList) {

								String values = ((List<Integer>) submissionData.getData()
										.get(question.getColumnName())).stream().map(e -> e.toString())
												.collect(Collectors.joining(","));
								questionModel = engineUtils.setTypeDetailsAsOptions(questionModel, typeDetailsMap,
										question, values, user, paramKeyValMap, session);

							} else if (submissionData.getData().get(question.getColumnName()) != null
									&& submissionData.getData().get(question.getColumnName()) instanceof String) {
								questionModel = engineUtils.setTypeDetailsAsOptions(questionModel, typeDetailsMap,
										question,
										String.class.cast(submissionData.getData().get(question.getColumnName())),
										user, paramKeyValMap, session);
							}

						} else {
							questionModel = engineUtils.setTypeDetailsAsOptions(questionModel, typeDetailsMap,
									question, null, user, paramKeyValMap, session);
						}

						questionModel.setValue(submissionData.getData().get(question.getColumnName()));
					}
					break;
				default:
					if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {
						questionModel = engineUtils.prepareQuestionModel(question);
						questionModel = engineUtils.setTypeDetailsAsOptions(questionModel, typeDetailsMap, question,
								null, user, paramKeyValMap, session);

						questionModel
								.setValue(submissionData.getData().get(question.getColumnName()) != null
										? Integer.parseInt(
												submissionData.getData().get(question.getColumnName()).toString())
										: null);
					}
				}

			}
				break;
			case "table":
			case "tableWithRowWiseArithmetic": {
				questionModel = engineUtils.prepareQuestionModel(question);
				/**
				 * from table question id and cell parent id getting all matched cells here
				 */
				List<Question> tableCells = questionList.stream()
						.filter(q -> q.getParentColumnName().equals(question.getColumnName()))
						.collect(Collectors.toList());

				Map<String, List<Question>> groupWiseQuestionsMap = new LinkedHashMap<>();

				tableCells.forEach(cell -> {

					if (groupWiseQuestionsMap.get(cell.getQuestion().split("@@split@@")[0].trim()) == null) {
						List<Question> questionPerGroup = new ArrayList<>();
						questionPerGroup.add(cell);
						groupWiseQuestionsMap.put(cell.getQuestion().split("@@split@@")[0].trim(),
								questionPerGroup);
					} else {
						List<Question> questionPerGroup = groupWiseQuestionsMap
								.get(cell.getQuestion().split("@@split@@")[0].trim());
						questionPerGroup.add(cell);
						groupWiseQuestionsMap.put(cell.getQuestion().split("@@split@@")[0].trim(),
								questionPerGroup);
					}

				});

				List<Map<String, Object>> array = new LinkedList<>();
				Integer index = 0;
				for (Map.Entry<String, List<Question>> map : groupWiseQuestionsMap.entrySet()) {
					List<Question> qs = map.getValue();
					;
					Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
					jsonMap.put(question.getQuestion(), map.getKey());

					for (Question qdomain : qs) {
						QuestionModel qModel = engineUtils.prepareQuestionModel(qdomain);

						qModel.setValue(submissionData == null ? "N/A"
								: (List<Map<String, Integer>>) submissionData.getData()
										.get(question.getColumnName()) != null ? ((List<Map<String, Integer>>) submissionData.getData()
												.get(question.getColumnName())).get(index)
												.get(qdomain.getColumnName())!=null
												? (((List<Map<String, Integer>>) submissionData.getData()
														.get(question.getColumnName())).get(index)
																.get(qdomain.getColumnName()))
												: "N/A" : "N/A");
						jsonMap.put(qdomain.getQuestion().split("@@split@@")[1].trim(), qModel);
					}
					index++;
					array.add(jsonMap);
				}

				questionModel.setTableModel(array);
			}
				break;

			case BEGIN_REPEAT: {
				questionModel = formsServiceImpl.prepareBeginRepeatModelWithData(question, questionList, submissionData, questionMap,
						typeDetailsMap, paramKeyValMap, session, user);

			}

				break;

			case "camera": {
				if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {

					questionModel = engineUtils.prepareQuestionModel(question);
					questionModel = iCameraDataHandler.readExternal(questionModel, submissionData,paramKeyValMap);

				}
			}
				break;
			}

			if (sectionMap.containsKey(question.getSection())) {

				if (subsectionMap.containsKey(question.getSubsection())) {

					/**
					 * checking the type of accordian here ie. RepeatSubSection()==no means not an
					 * accordian, and yes means accordian
					 */
					List<QuestionModel> list = (List<QuestionModel>) subsectionMap.get(question.getSubsection());
					if (questionModel != null)
						list.add(questionModel);

				} else {
					listOfQuestionModel = new LinkedList<>();
					if (questionModel != null)
						listOfQuestionModel.add(questionModel);
					subsectionMap.put(question.getSubsection(), listOfQuestionModel);
				}

			} else {
				subsectionMap = new LinkedHashMap<>();
				listOfQuestionModel = new ArrayList<>();
				if (questionModel != null)
					listOfQuestionModel.add(questionModel);
				subsectionMap.put(question.getSubsection(), listOfQuestionModel);

				sectionMap.put(question.getSection(), subsectionMap);
			}
		}

		/**
		 * adding list of subsection against a section.
		 */

		for (Map.Entry<String, Map<String, List<QuestionModel>>> entry : sectionMap.entrySet()) {

			if (mapOfSectionSubsectionListOfQuestionModel.containsKey(entry.getKey())) {
				mapOfSectionSubsectionListOfQuestionModel.get(entry.getKey()).add(entry.getValue());
			} else {
				mapOfSectionSubsectionListOfQuestionModel.put(entry.getKey(), Arrays.asList(entry.getValue()));
			}
		}
	
		return mapOfSectionSubsectionListOfQuestionModel;
	}

	@Override
	public ResponseEntity<String> saveNotificationDetail(Integer formId, Integer deadlineDays, Integer notifyByDays) {
		NotificationDetail fetchedNotificationDetail = notificationDetailRepository.findByFormId(formId);
		
		if(deadlineDays>28 && deadlineDays<1 ) {
			return new ResponseEntity<>("please select deadline days between 1 to 28 ", HttpStatus.OK);
		}
		if(notifyByDays>28 && notifyByDays<1 ) {
			return new ResponseEntity<>("please select deadline days between 1 to 28 ", HttpStatus.OK);
		}
		
		
		if(fetchedNotificationDetail==null) {
			fetchedNotificationDetail = new NotificationDetail();
			fetchedNotificationDetail.setFormId(formId);
			fetchedNotificationDetail.setDeadlineDays(deadlineDays);
			fetchedNotificationDetail.setNotifyByDays(notifyByDays);
		}else {
			fetchedNotificationDetail.setDeadlineDays(deadlineDays);
			fetchedNotificationDetail.setNotifyByDays(notifyByDays);
		}
		
		notificationDetailRepository.save(fetchedNotificationDetail);
		 return new ResponseEntity<>("Success", HttpStatus.OK);
	}
}
