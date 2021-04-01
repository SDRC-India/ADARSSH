package org.sdrc.rmncha.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.sdrc.rmncha.domain.AllChecklistFormData;
import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmncha.domain.NotificationDetail;
import org.sdrc.rmncha.domain.TimePeriod;
import org.sdrc.rmncha.model.ChecklistSubmissionStatus;
import org.sdrc.rmncha.model.NotificationModel;
import org.sdrc.rmncha.model.SubmissionCompleteStatus;
import org.sdrc.rmncha.model.UserModel;
import org.sdrc.rmncha.rabbitMQ.AllChecklistFormDataEvent;
import org.sdrc.rmncha.rabbitMQ.AreaModelEvent;
import org.sdrc.rmncha.rabbitMQ.CollectionChannel;
import org.sdrc.rmncha.rabbitMQ.TimePeriodEvent;
import org.sdrc.rmncha.repositories.AllChecklistFormDataRepository;
import org.sdrc.rmncha.repositories.AreaRepository;
import org.sdrc.rmncha.repositories.CustomTypeDetailRepository;
import org.sdrc.rmncha.repositories.NotificationDetailRepository;
import org.sdrc.rmncha.repositories.TimePeriodRepository;
import org.sdrc.rmncha.util.TokenInfoExtracter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import in.co.sdrc.sdrcdatacollector.models.FormAttachmentsModel;
import in.co.sdrc.sdrcdatacollector.models.ReceiveEventModel;
import lombok.extern.slf4j.Slf4j;

/**
 * This service is for data submission from web and mobile
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 19-Jul-2019 3:19:45 PM
 */
@Service
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

	@Autowired
	private AllChecklistFormDataRepository allChecklistFormDataRepository;

	@Autowired
	private TokenInfoExtracter tokenInfoExtracter;

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Autowired
	private CollectionChannel collectionChannel;

	@Autowired
	private AreaRepository mongoAreaRepository;
	
//	@Autowired
//	private TypeDetailRepository typeDetailRepository;
	
	@Autowired
	private NotificationDetailRepository notificationDetailRepository;
	
	@Autowired
	private TimePeriodRepository timePeriodRepository;
	
	@Autowired
	private CustomTypeDetailRepository customTypeDetailRepository;
	
//	private final ZoneId defaultZoneId = ZoneId.systemDefault();
	
	@Autowired
	private MongoTemplate mongoTemplate;
	

	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 19-Jul-2019 3:19:36 PM
	 * @see org.sdrc.rmncha.service.SubmissionService#saveSubmission(in.co.sdrc.sdrcdatacollector.models.ReceiveEventModel, org.springframework.security.oauth2.provider.OAuth2Authentication)
	 */
	@Override
//	@Caching(evict = { @CacheEvict(value = "formData", key = "#event.formId")})
	public ResponseEntity<String> saveSubmission(ReceiveEventModel event, OAuth2Authentication oauth) {
		Gson gson = new Gson();
		try {
			if (event.getFormId() == null) {
				log.warn("Invalid submission request, missing formId in submission payload");
				return new ResponseEntity<>("Missing formId key in payload", HttpStatus.BAD_REQUEST);
			}

			UserModel principal = tokenInfoExtracter.getUserModelInfo(oauth);
			AllChecklistFormData dataSubmit = new AllChecklistFormData();
			
			
			Date dateOfVisit = null;
			
//			Integer deadlineDay = findAllNotificationDetail().get(event.getFormId()).getDeadlineDays();
			
			// {REJECTION====>>>>>>>>>>>>>>>}
			// if facility type or level changes after 1 submission, then reject the
			// submission
			
			List<TypeDetail> typeDetails = customTypeDetailRepository.findByNameInAndTypeTypeName(Arrays.asList("Non 24x7 PHC","24x7 PHC"), "PHC Type");

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
			Map<String, Object> formIdTypeDeatils = new HashMap<>();
			
		
			Area submittedArea = null;
			
			switch (event.getFormId()) {
			case 1:
				dateOfVisit = new SimpleDateFormat("dd-MM-yyyy").parse(event.getSubmissionData().get("f1q_date_of_visit").toString());
				
				event.getSubmissionData().put("f1q_date_of_visit", dateOfVisit);
				
//				List<AllChecklistFormData> facilityChecklistFormDatas = allChecklistFormDataRepository
//						.findByFormIdAndDataColumnId(event.getFormId(),(Integer) event.getSubmissionData().get(configurableEnvironment.getProperty("form1.facility.name.column")));

				Integer phcTypeId = (Integer) event.getSubmissionData().get(configurableEnvironment.getProperty("form1.phc.type.column"));
				Integer chcTypeId =  (Integer) event.getSubmissionData().get(configurableEnvironment.getProperty("form1.chc.type.column"));
				
				Area facility = mongoAreaRepository
						.findByAreaId(Integer.parseInt(event.getSubmissionData().get(configurableEnvironment.getProperty("form1.facility.name.column")).toString()));
				
				if (phcTypeId != null || chcTypeId != null) {

					// if a submission is already present for the facility'
					// if previously submitted facility differs then reject the submission
					if (facility.getPhcChcType() != null
							&&( ((TypeDetail)facility.getPhcChcType().get(event.getFormId().toString())).getSlugId() != phcTypeId
									&& ((TypeDetail)facility.getPhcChcType().get(event.getFormId().toString())).getSlugId() != chcTypeId) ){

						dataSubmit.setRejected(true);
						dataSubmit.setRejectMessage(
								configurableEnvironment.getProperty("facility.type.mismatch.rejection.msg"));
						dataSubmit.setRejectedApprovedDate(new Date());
						log.info("Rejected Submission", facility.getAreaId());
					} else if(facility.getPhcChcType() == null) {
						Integer phcChcTypeId = phcTypeId != null ? phcTypeId : chcTypeId;
						Map<String, Object> formIdTypeMap = new HashMap<>();
						TypeDetail phcChcType = customTypeDetailRepository.findByFormIdAndSlugId(event.getFormId(), phcChcTypeId);
						
						formIdTypeMap.put(event.getFormId().toString(),
								phcChcType);
					
						if(phcTypeId != null) {
							
							formIdTypeDeatils = new HashMap<>();
							
								for (TypeDetail typeDetail : formTypeDetails.get(phcChcType.getName())) {
									formIdTypeDeatils.put(typeDetail.getFormId().toString(), typeDetail);
								}
								
							
							formIdTypeMap.put("2", formIdTypeDeatils.get("2"));
						}
						
						//put both form's facility type
						facility.setPhcChcType(formIdTypeMap);
						mongoAreaRepository.save(facility);
						
						
					}
				}			
				submittedArea = facility;
				break;
			case 2: //community checklist
				dateOfVisit = new SimpleDateFormat("dd-MM-yyyy").parse(event.getSubmissionData().get("f2qdateofvisit").toString());
				event.getSubmissionData().put("f2qdateofvisit", dateOfVisit);
				
				
				Integer communityPhcTypeId = (Integer) event.getSubmissionData().get(configurableEnvironment.getProperty("form2.phc.type.column"));
				
				Area communityFacility = mongoAreaRepository
						.findByAreaId(Integer.parseInt(event.getSubmissionData().get(configurableEnvironment.getProperty("form2.facility.name.column")).toString()));
				
				if (communityPhcTypeId != null) {

					if (communityFacility.getPhcChcType() != null
							&& ((TypeDetail) communityFacility.getPhcChcType().get(event.getFormId().toString()))
									.getSlugId() != communityPhcTypeId) {
						dataSubmit.setRejected(true);
						dataSubmit.setRejectMessage(
								configurableEnvironment.getProperty("facility.type.mismatch.rejection.msg"));
						dataSubmit.setRejectedApprovedDate(new Date());
						log.info("Rejected Submission", communityFacility.getAreaId());
					} else if (communityFacility.getPhcChcType() == null) {
						Integer phcChcTypeId = communityPhcTypeId;
						Map<String, Object> formIdTypeMap = new HashMap<>();

						TypeDetail commPhcType = customTypeDetailRepository.findByFormIdAndSlugId(event.getFormId(),
								phcChcTypeId);
						// put both form's facility type
						formIdTypeMap.put(event.getFormId().toString(), commPhcType);

						if (phcChcTypeId != null) {

							formIdTypeDeatils = new HashMap<>();

							for (TypeDetail typeDetail : formTypeDetails.get(commPhcType.getName())) {
								formIdTypeDeatils.put(typeDetail.getFormId().toString(), typeDetail);
							}

							formIdTypeMap.put("1", formIdTypeDeatils.get("1"));
						}

						
						communityFacility.setPhcChcType(formIdTypeMap);
						mongoAreaRepository.save(communityFacility);
						
					}
				}
				submittedArea = communityFacility;
				break;
				
			case 3:
				dateOfVisit = new SimpleDateFormat("dd-MM-yyyy").parse(event.getSubmissionData().get("f3q7_dov").toString());
				event.getSubmissionData().put("f3q7_dov", dateOfVisit);
				
				Area district = mongoAreaRepository
						.findByAreaId(Integer.parseInt(event.getSubmissionData().get(configurableEnvironment.getProperty("form3.district.name.column")).toString()));
				submittedArea = district;
				break;
				
			case 4:
				dateOfVisit =  new SimpleDateFormat("dd-MM-yyyy").parse(event.getSubmissionData().get("hwc10_dateOfVisit").toString());
				event.getSubmissionData().put("hwc10_dateOfVisit", dateOfVisit);
				//in case of HWC checklist set area table ISHWC attribute 
				Area hwcFacility = mongoAreaRepository
						.findByAreaId(Integer.parseInt(event.getSubmissionData().get(configurableEnvironment.getProperty("form4.facility.name.column")).toString()));
				
			
				hwcFacility.setHwc(true);
				mongoAreaRepository.save(hwcFacility);
				
				submittedArea = hwcFacility;
				
				break;
			
			}
			
			
			//get the current time period from date of visit
			TimePeriod timePeriod = getCurrentTimePeriod(dateOfVisit);
			
			if(timePeriod==null) {
				
				log.error("Failed to persisit data with payload : {}", event);
				return new ResponseEntity<>(new Gson().toJson("Please enter valid DATE OF VISIT"), HttpStatus.BAD_REQUEST);
			}
			
			
			
			//this code is commented after UAT feedback to remove default status
			/* //get the deadline date of that time period's 
			LocalDate deadlineDate =timePeriod.getEndDate().toInstant().atZone(defaultZoneId).toLocalDate().plusDays(deadlineDay);
			
			LocalDate syncDateLocalDate = new Date().toInstant().atZone(defaultZoneId).toLocalDate();
			
			//check if sync date is same or before the deadline date
			//in case of delayed submission keep status DEFAULT
			
			if(dataSubmit.isRejected()) {
				dataSubmit.setChecklistSubmissionStatus(ChecklistSubmissionStatus.REJECTED);
			}else if(syncDateLocalDate.isAfter(deadlineDate)) {
				dataSubmit.setChecklistSubmissionStatus(ChecklistSubmissionStatus.DEFAULT);
			}else {
				dataSubmit.setChecklistSubmissionStatus(ChecklistSubmissionStatus.PENDING);
			}*/
			
//			Data submitted to server shall be auto approved by the system.
			if(dataSubmit.isRejected()) {
				dataSubmit.setChecklistSubmissionStatus(ChecklistSubmissionStatus.REJECTED);
			}else
				dataSubmit.setChecklistSubmissionStatus(ChecklistSubmissionStatus.APPROVED);
			
			AllChecklistFormData oldSubmission = allChecklistFormDataRepository
					.findByFormIdAndAreaAreaIdAndTimePeriodTimePeriodIdAndLatestTrue(event.getFormId(), submittedArea.getAreaId(),
							timePeriod.getTimePeriodId());
			
			//make the last submission false for the same time period,area id, checklist
			if(oldSubmission!=null) {
				oldSubmission.setLatest(false);
				allChecklistFormDataRepository.save(oldSubmission);
			}
			
//			if data is already submitted for the given time period then make the last submitted data's latest false.
			
			dataSubmit.setTimePeriod(timePeriod);
			dataSubmit.setArea(submittedArea);
			dataSubmit.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(event.getCreatedDate()));
			dataSubmit.setUpdatedDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(event.getUpdatedDate()));
			dataSubmit.setSyncDate(new Date());
			dataSubmit.setUserName(principal.getName());
			dataSubmit.setFormId(event.getFormId());
			dataSubmit.setUserId(principal.getUserId());
			dataSubmit.setData(event.getSubmissionData());
			dataSubmit.setUniqueId(event.getUniqueId());
			dataSubmit.setUniqueName(event.getUniqueName());
			dataSubmit.setSubmittedBy(principal.getName());
			dataSubmit.setLatest(true);
			dataSubmit.setAttachmentCount(event.getAttachmentCount());
			// dataSubmit.setTimePeriod(timePeriod);
			/*
			 * field to be modified later according to inputs
			 */
			dataSubmit.setIsAggregated(false);
			dataSubmit.setIsValid(true);
			dataSubmit.setRejectedApprovedDate(new Date());
			/**
			 * check for same unique id and formid whether any submission is exist which is fresh data that is duplicate=false
			 * if exist than make the previous data as duplicate true 
			 */
			List<AllChecklistFormData> dupData = allChecklistFormDataRepository.findByFormIdAndUniqueIdAndDuplicateFalse(dataSubmit.getFormId(),dataSubmit.getUniqueId());
			
			List<String> duplicateSubIds = new ArrayList<>();
			
			dupData.forEach(d -> {
				d.setDuplicate(true);
				duplicateSubIds.add(d.getId());
			});
			allChecklistFormDataRepository.save(dupData);
			
			//publishes duplicate status to dashboard service
			if(!duplicateSubIds.isEmpty())
				collectionChannel.duplicateChannel().send(MessageBuilder.withPayload(duplicateSubIds).build());
			
			AllChecklistFormData savedData=allChecklistFormDataRepository.save(dataSubmit);
			
			AllChecklistFormDataEvent targetEvent = new AllChecklistFormDataEvent();
			AreaModelEvent areaModelEvent = new AreaModelEvent();
			TimePeriodEvent timePeriodEvent = new TimePeriodEvent();
			
			BeanUtils.copyProperties(submittedArea, areaModelEvent);
			BeanUtils.copyProperties(timePeriod, timePeriodEvent);
			
			targetEvent.setArea(areaModelEvent);
			targetEvent.setTimePeriod(timePeriodEvent);
			BeanUtils.copyProperties(savedData, targetEvent);
			
//			targetEvent.setAreaCode(savedData.getArea().getAreaCode());
//			targetEvent.setTimePeriodId(savedData.getTimePeriod().getTimePeriodId());
			
			collectionChannel.dataSubmissionChannel().send(MessageBuilder.withPayload(targetEvent).build());
			try {
				if(savedData.getAttachmentCount()==0) {
					
					collectionChannel.sendSubmitEmailChannel().send(MessageBuilder.withPayload(targetEvent).build());
					
				}
			} catch (Exception e) {
				log.error("Failed to send payload to dashboard service with payload : {}", event, e);
				throw new Exception(e.getMessage(), e);
			}
			
			return new ResponseEntity<String>(gson.toJson(dataSubmit.getId()), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Failed to persisit data with payload : {}", event, e);
			return new ResponseEntity<>(new Gson().toJson("Something went wrong. Please try again later"), HttpStatus.CONFLICT);
		}

	}

	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 19-Jul-2019 3:19:23 PM
	 * @see org.sdrc.rmncha.service.SubmissionService#uploadFiles(org.springframework.web.multipart.MultipartFile, in.co.sdrc.sdrcdatacollector.models.FormAttachmentsModel)
	 */
	@Override
	@Transactional
	public String uploadFiles(MultipartFile file, FormAttachmentsModel fileModel) throws Exception {

		String filePath = "";
		
		switch (fileModel.getFormId()) {

		case 1: {
			filePath = getFilePath(file, configurableEnvironment.getProperty("form1.name"),
					fileModel.getFileExtension(), fileModel.getOriginalName());

		}
			break;
		case 2: {
			filePath = getFilePath(file, configurableEnvironment.getProperty("form2.name"),
					fileModel.getFileExtension(), fileModel.getOriginalName());

		}
			break;
		case 3: {
			filePath = getFilePath(file, configurableEnvironment.getProperty("form3.name"),
					fileModel.getFileExtension(), fileModel.getOriginalName());

		}
			break;
		case 4: 
			filePath = getFilePath(file, configurableEnvironment.getProperty("form4.name"),
					fileModel.getFileExtension(), fileModel.getOriginalName());
		}

			FormAttachmentsModel model = new FormAttachmentsModel();
			List<FormAttachmentsModel> modelList = new ArrayList<FormAttachmentsModel>();
			
			AllChecklistFormData submissionData = allChecklistFormDataRepository.findById(fileModel.getSubmissionId());

			Map<String, List<FormAttachmentsModel>> attachments = submissionData.getAttachments();
			
			model.setFilePath(filePath);
			model.setFileSize(file.getSize());
			model.setOriginalName(
					fileModel.getOriginalName().substring(0, fileModel.getOriginalName().lastIndexOf('.')));
			model.setFileExtension(fileModel.getFileExtension());
			model.setColumnName(fileModel.getColumnName());
			model.setFileExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
			model.setFormId(fileModel.getFormId());

			if (attachments == null) {
				attachments = new HashMap<>();
				modelList.add(model);
				
				attachments.put(fileModel.getColumnName(), modelList);
			} 
			else {
				
				modelList.add(model);
				attachments.put(fileModel.getColumnName(), modelList);
			}
			if (submissionData.getAttachmentCount() == attachments.size()) {
				submissionData.setSubmissionCompleteStatus(SubmissionCompleteStatus.C);
			}
			submissionData.setAttachments(attachments);

			allChecklistFormDataRepository.save(submissionData);
			
			//send report notification email channel
			if (submissionData.getAttachmentCount() == submissionData.getAttachments().size()) {
				
				Map<String, Map<String, List<FormAttachmentsModel>> > submissionIdFormIdMap = new HashMap<>();
				
				//send this to rabbitmq
				submissionIdFormIdMap.put(fileModel.getSubmissionId(), attachments);
				collectionChannel.attachmentChannel().send(MessageBuilder.withPayload(submissionIdFormIdMap).build());
				
				AllChecklistFormDataEvent targetEvent = new AllChecklistFormDataEvent();
				
				BeanUtils.copyProperties(submissionData, targetEvent);
				
//				targetEvent.setAreaCode(submissionData.getArea().getAreaCode());
//				targetEvent.setTimePeriodId(submissionData.getTimePeriod().getTimePeriodId());
				collectionChannel.sendSubmitEmailChannel().send(MessageBuilder.withPayload(targetEvent).build());

			}

			return new Gson().toJson("success");
		}

	/*
	 * it save the file in hard disk and return the complete file path
	 */
	private String getFilePath(MultipartFile file, String formName, String extension, String originalFileName) {
		String path = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			for (int readNum; (readNum = bis.read(file.getBytes())) != -1;) {
				bos.write(file.getBytes(), 0, readNum);
			}

			byte[] getFileBytes = bos.toByteArray();

			String dir = configurableEnvironment.getProperty("upload.file.path");

			dir = dir + formName.concat("/");

			File filePath = new File(dir);

			/*
			 * make directory if doesn't exist
			 */
			if (!filePath.exists())
				filePath.mkdirs();

			String name = originalFileName.substring(0, originalFileName.lastIndexOf('.'))
					+ new SimpleDateFormat("ddMMyyyyHHmmssSSSS").format(new Date()).concat(".") + extension;

			path = dir + name;

			FileOutputStream fos = new FileOutputStream(path);
			fos.write(getFileBytes);
			fos.flush();
			fos.close();

		} catch (Exception e) {
			log.error("Action : While uploading file formName {}", formName);
		}

		return path;

	}
	
	/* (non-Javadoc)
	 * @see org.sdrc.rmncha.service.SubmissionService#findAllNotificationDetail()
	 * get all deadline dates of all forms
	 */
	@Override
	public Map<Integer, NotificationModel> findAllNotificationDetail(){
		List<NotificationDetail> notificationDetails = notificationDetailRepository.findAll();
		
		Map<Integer, NotificationModel> formIdNotificationModelMap = new HashMap<>();
		notificationDetails.forEach(notificationDetail -> {
			NotificationModel notificationModel = new NotificationModel();
			notificationModel.setFormId(notificationDetail.getFormId());
			notificationModel.setDeadlineDays(notificationDetail.getDeadlineDays());
			notificationModel.setNotifyByDays(notificationDetail.getNotifyByDays());
			
			formIdNotificationModelMap.put(notificationDetail.getFormId(), notificationModel);
			
		});
		
		return formIdNotificationModelMap;
	}
	
	/**
	 * get current time period
	 * @return
	 */
	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 19-Jul-2019 3:20:33 PM
	 * @see org.sdrc.rmncha.service.SubmissionService#getCurrentTimePeriod(java.util.Date)
	 */
	@Override
	public TimePeriod getCurrentTimePeriod(Date dateOfVisit) {

		TimePeriod currentTimePeriod = null;

		currentTimePeriod = timePeriodRepository.getCurrentTimePeriod(dateOfVisit,
				configurableEnvironment.getProperty("timeperiod.periodicity.monthly"));

		return currentTimePeriod;
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
	
	@Override
	public String updateSubmissionStatusToApprove() {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("checklistSubmissionStatus").in(Arrays.asList(ChecklistSubmissionStatus.PENDING, ChecklistSubmissionStatus.DEFAULT)));
		Update update = new Update();
		update.set("checklistSubmissionStatus",ChecklistSubmissionStatus.APPROVED);
		update.set("isValid", true);
		mongoTemplate.updateMulti(query, update, AllChecklistFormData.class);
		
		return "updated";
	}
}
