package org.sdrc.rmncha.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.sdrc.rmncha.model.FormModel;
import org.sdrc.rmncha.model.UserModel;
import org.sdrc.rmncha.model.ValueObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import in.co.sdrc.sdrcdatacollector.models.DataObject;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;
import in.co.sdrc.sdrcdatacollector.models.ReviewPageModel;

public interface SubmissionManagementService {

	List<FormModel> getAllForms(OAuth2Authentication auth);

	ResponseEntity<String> rejectSubmissions(ValueObject valueObject, OAuth2Authentication auth);

	ReviewPageModel getReviewData(ReviewPageModel model);

	List<DataObject> getReiewData(Integer formId, UserModel user, Map<String, Object> paramKeyValMap);

	Map<String, List<Map<String, List<QuestionModel>>>> getViewMoreDataForReview(Integer formId, UserModel user,
			String submissionId, Map<String, Object> paramKeyValMap, HttpSession session);
	
	ResponseEntity<String> saveNotificationDetail(Integer formId, Integer deadlineDays, Integer notifyByDays);

}
