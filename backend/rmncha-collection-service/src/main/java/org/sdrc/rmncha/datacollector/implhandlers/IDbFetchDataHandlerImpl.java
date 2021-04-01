package org.sdrc.rmncha.datacollector.implhandlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.sdrc.rmncha.domain.AllChecklistFormData;
import org.sdrc.rmncha.model.UserModel;
import org.sdrc.rmncha.repositories.AllChecklistFormDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import in.co.sdrc.sdrcdatacollector.document.EnginesForm;
import in.co.sdrc.sdrcdatacollector.handlers.IDbFetchDataHandler;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.RawDataModel;
import in.co.sdrc.sdrcdatacollector.mongorepositories.EngineFormRepository;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 * @author Azaruddin (azaruddin@sdrc.co.in)
 */
@Component
public class IDbFetchDataHandlerImpl implements IDbFetchDataHandler {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private AllChecklistFormDataRepository allChecklistFormDataRepository;
	
	
	@Autowired
	private EngineFormRepository engineFormRepository;
	/*@Override
	public List<DataModel> fetchDataFromDb(
			EnginesRoleFormMapping mapping, String type, Map<Integer, String> mapOfForms,
			Date startDate, Date endDate, Map<String, Object> paramKeyValMap, HttpSession session, Object user) {
		
		List<DataModel> submissionDatas = new ArrayList<>();
		// @formatter:off
		List<?> entriesList = "dataReview".equals(type)
				? (mongoTemplate
						.aggregate(
								getAggregationResults(mapping.getForm().getFormId(),
										"dataReviewTracker" , startDate, endDate ),
								FacilityChecklistFormData.class, FacilityChecklistFormData.class)
						.getMappedResults().stream().filter(value -> value.isRejected() == false)
						.collect(Collectors.toList()))
				: (mongoTemplate
						.aggregate(getAggregationResults(mapping.getForm().getFormId(), "rejectedDataTracker"),
								FacilityChecklistFormData.class, FacilityChecklistFormData.class)
						.getMappedResults().stream().filter(value -> value.isRejected() == true)
						.collect(Collectors.toList()));

		// @formatter:on

		if (entriesList != null && !entriesList.isEmpty()) {
			for (Object entry : entriesList) {
				if (entry instanceof FacilityChecklistFormData) {
					FacilityChecklistFormData data = (FacilityChecklistFormData) entry;
				
					DataModel model = new DataModel();
					model.setId(data.getId());
					model.setData(data.getData());
					model.setFormId(data.getFormId());
					model.setRejected(true);
					model.setUniqueId(data.getUniqueId());
					model.setUniqueName(data.getUniqueName());
					model.setUpdatedDate(data.getUpdatedDate());
					model.setUserId(data.getUserId());
					model.setUserName(data.getUserName());
					model.setCreatedDate(data.getCreatedDate());
					
					Map<String,Object> extraInfoKeys = new HashMap<>();
					extraInfoKeys.put("name", data.getSubmittedBy());
					model.setExtraKeys(extraInfoKeys);

					submissionDatas.add(model);
				}
			}
		}

		return submissionDatas;
	}*/

	@Override
	public RawDataModel findAllByRejectedFalseAndSyncDateBetween(Integer formId, Date startDate, Date endDate) {

		return null;
	}

	public Aggregation getAggregationResults(Integer formId, String type/* , Date startDate, Date endDate */, Object user) {

		UserModel userModel = (UserModel) user;
		
		MatchOperation match = null;
		 if ("dataReview".equals(type)) {
			 
			
			
			   switch (formId) {
				case 1:
					match = Aggregation.match(Criteria.where("formId").is(formId).and("isAggregated").is(false)
							.and("latest").is(true)
							.and("duplicate").is(false)
							.and("data.f1q_state").is(userModel.getAreas().get(0).getAreaId()));
					break;
				case 2:
					match = Aggregation.match(Criteria.where("formId").is(formId).and("isAggregated").is(false)
							.and("latest").is(true)
							.and("duplicate").is(false)
							.and("data.f2qstate").is(userModel.getAreas().get(0).getAreaId()));
					break;
				case 3:
					match = Aggregation.match(Criteria.where("formId").is(formId).and("isAggregated").is(false)
							.and("latest").is(true)
							.and("duplicate").is(false)
							.and("data.f3q5_state").is(userModel.getAreas().get(0).getAreaId()));
					break;
				case 4:
					match = Aggregation.match(Criteria.where("formId").is(formId).and("isAggregated").is(false)
							.and("latest").is(true)
							.and("duplicate").is(false)
							.and("data.hwc5_state").is(userModel.getAreas().get(0).getAreaId()));
					break;

				default:
					break;
				}
			
			
			
			
			GroupOperation groupReview = Aggregation.group("id")
					.push("$$ROOT").as("submissions");
			
			
			AggregationOperation replaceRoot = Aggregation.replaceRoot()
					.withValueOf(ArrayOperators.ArrayElemAt.arrayOf("submissions").elementAt(0));
			
			SortOperation sortreviewData = Aggregation
					.sort(Sort.Direction.DESC, "syncDate");
			
			return Aggregation.newAggregation(match, sortreviewData, groupReview, replaceRoot);

		} else if ("rejectedData".equals(type)) {
			match = Aggregation.match(Criteria.where("formId").is(formId).and("latest").is(true).and("isAggregated").is(false).and("duplicate").is(false)
					.and("userId").is(userModel.getUserId()));

		}

		SortOperation sort = Aggregation.sort(Sort.Direction.ASC, "userId").and(Sort.Direction.ASC, "formId")
				.and(Sort.Direction.DESC, "uniqueId").and(Sort.Direction.ASC, "syncDate");
		// @formatter:off
		GroupOperation group = Aggregation.group("uniqueId")
				.last("syncDate").as("syncDate")
				.last("submittedBy").as("submittedBy")
				.last("data").as("data")
				.last("userName").as("userName")
				.last("userId").as("userId")
				.last("createdDate").as("createdDate")
				.last("updatedDate").as("updatedDate")
				.last("formId").as("formId")
				.last("uniqueId").as("uniqueId")
				.last("uniqueName").as("uniqueName")
				.last("rejected").as("rejected")
				.last("rejectMessage").as("rejectMessage")
				.last("isAggregated").as("isAggregated")
				.last("attachmentCount").as("attachmentCount")
				.last("attachments").as("attachments")
				.last("isValid").as("isValid")
				.last("timePeriod").as("timePeriod")
				.last("checklistSubmissionStatus").as("checklistSubmissionStatus")
				.last("id").as("id")
				.last("actionByUserName").as("actionByUserName")
				.last("actionByFullName").as("actionByFullName")
				.last("rejectedApprovedDate").as("rejectedApprovedDate");
		// @formatter:on
		return Aggregation.newAggregation(match, sort, group);
	}

	/* (non-Javadoc)
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 19-Jul-2019 3:19:10 PM
	 * @see in.co.sdrc.sdrcdatacollector.handlers.IDbFetchDataHandler#fetchDataFromDb(in.co.sdrc.sdrcdatacollector.document.EnginesForm, java.lang.String, java.util.Map, java.util.Date, java.util.Date, java.util.Map, javax.servlet.http.HttpSession, java.lang.Object)
	 */
	@Override
	public List<DataModel> fetchDataFromDb(EnginesForm engineForm, String type, Map<Integer, String> arg2, Date startDate,
			Date endDate, Map<String, Object> paramKeyValMap, HttpSession session, Object user) {
		List<DataModel> submissionDatas = new ArrayList<>();
		// @formatter:off
		List<?> entriesList = "dataReview".equals(type)
				? (mongoTemplate
						.aggregate(
								getAggregationResults(engineForm.getFormId(),
										"dataReview" /*, startDate, endDate*/ ,user),
								AllChecklistFormData.class, AllChecklistFormData.class)
						.getMappedResults())
				: (mongoTemplate
						.aggregate(getAggregationResults(engineForm.getFormId(), "rejectedData",user),
								AllChecklistFormData.class, AllChecklistFormData.class)
						.getMappedResults().stream().filter(value -> value.isRejected() == true)
						.collect(Collectors.toList()));

		// @formatter:on

		if (entriesList != null && !entriesList.isEmpty()) {
			for (Object entry : entriesList) {
				if (entry instanceof AllChecklistFormData) {
					AllChecklistFormData data = (AllChecklistFormData) entry;
				
					
					Map<String, Object> extraKeys = new HashMap<>();

					if (data.getRejectMessage() != null) {
						
						extraKeys.put("rejectMessage", data.getRejectMessage());
						if (data.getActionByUserName() == null) {
							extraKeys.put("rejectedBy", " : Auto rejected by system");
						} else
							extraKeys.put("rejectedBy", " : Rejected by "+ data.getActionByFullName());
					}
					
					if(data.getRejectedApprovedDate()!=null)
						extraKeys.put("actionDate", data.getRejectedApprovedDate());
					extraKeys.put("submissionId", data.getId());
					extraKeys.put("status", data.getChecklistSubmissionStatus());
					extraKeys.put("syncDate", data.getSyncDate());
					DataModel model = new DataModel();
					model.setId(data.getId());
					model.setData(data.getData());
					model.setFormId(data.getFormId());
					model.setRejected(data.isRejected());
					model.setUniqueId(data.getUniqueId());
					model.setUniqueName(data.getUniqueName());
					model.setUpdatedDate(data.getUpdatedDate());
					model.setUserId(data.getUserId());
					model.setUserName(data.getUserName());
					model.setCreatedDate(data.getCreatedDate());
					model.setFormVersion(engineForm.getVersion());
					model.setExtraKeys(extraKeys);
					model.setAttachments(data.getAttachments());

					submissionDatas.add(model);
				}
			}
		}

		return submissionDatas;
	}

	@Override
	public DataModel getSubmittedData(String submissionId, Integer formId) {
		AllChecklistFormData submittedData = allChecklistFormDataRepository.findByIdAndFormId(submissionId, formId);
		EnginesForm form = engineFormRepository.findByFormId(submittedData.getFormId());
		
		DataModel model = new DataModel();
		
		model.setAttachments(submittedData.getAttachments());
		model.setCreatedDate(submittedData.getCreatedDate());
		model.setData(submittedData.getData());
		model.setFormId(submittedData.getFormId());
		model.setFormVersion(form.getVersion());
		model.setId(submittedData.getId());
		model.setRejected(submittedData.isRejected());
		model.setUniqueId(submittedData.getUniqueId());
		model.setUpdatedDate(submittedData.getUpdatedDate());
		model.setUserId(submittedData.getUserId());
		model.setUserName(submittedData.getUserName());
		
		return model;
	}
}
