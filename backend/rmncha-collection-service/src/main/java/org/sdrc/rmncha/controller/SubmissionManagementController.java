package org.sdrc.rmncha.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.sdrc.rmncha.model.FormModel;
import org.sdrc.rmncha.model.UserModel;
import org.sdrc.rmncha.model.ValueObject;
import org.sdrc.rmncha.service.SubmissionManagementService;
import org.sdrc.rmncha.util.TokenInfoExtracter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.co.sdrc.sdrcdatacollector.engine.FormsService;
import in.co.sdrc.sdrcdatacollector.models.DataObject;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;
import in.co.sdrc.sdrcdatacollector.models.ReviewPageModel;

/**
 * @author Sarita Panigrahi
 * This controller handles all the approval/ rejection mechanism of checklist submission
 *
 */
@RestController
@RequestMapping("/api")
public class SubmissionManagementController {
	
	@Autowired
	private SubmissionManagementService submissionManagementService;

	@Autowired
	private TokenInfoExtracter tokenInfoExtracter;
	
	@Autowired
	private FormsService dataEntryService;
	
	@RequestMapping(value = "/rejectMultipleSubmission", method = RequestMethod.POST)
	public ResponseEntity<String> rejectSubmissions(@RequestBody ValueObject valueObject, OAuth2Authentication auth) {

		return submissionManagementService.rejectSubmissions(valueObject, auth);

	}
	
	@GetMapping("/getDataForReview")
	public ReviewPageModel getDataForReview(@RequestParam("formId") Integer formId,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam("pageNo") Integer pageNo,HttpSession session,
			OAuth2Authentication auth) {

		UserModel user = tokenInfoExtracter.getUserModelInfo(auth);
		Map<String, Object> paramKeyValMap = new HashMap<>();
		paramKeyValMap.put("pageNo", pageNo);
		paramKeyValMap.put("review", "reviewData");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date date = new Date();
		startDate = sdf.format(date);
		endDate = sdf.format(date);

		ReviewPageModel model = dataEntryService.getDataForReview(formId, startDate, endDate, paramKeyValMap, session,
				user, (Integer) user.getDesgSlugIds().toArray()[0]);

		/**
		 * if the date between 1 to 25 inclusive last month data would be enable
		 * for rejection, current month data would be available for
		 * view(rejection/approve button would be disabled)
		 */
		return submissionManagementService.getReviewData(model);

	}
	
	@GetMapping("/getAllReviewForms")
//	@PreAuthorize("hasAnyAuthority('Submission Management')")	
	public List<FormModel> getAllForms(OAuth2Authentication auth) {
		return submissionManagementService.getAllForms(auth);
	}
	
	@GetMapping("/getAllFormDataHead")
	public List<DataObject> getDataForReview(@RequestParam("formId") Integer formId,OAuth2Authentication auth) {

		UserModel user = tokenInfoExtracter.getUserModelInfo(auth);
		Map<String, Object> paramKeyValMap = new HashMap<>();
		paramKeyValMap.put("review", "reviewData");
		paramKeyValMap.put("pageNo", 1);
		return submissionManagementService.getReiewData(formId,user,paramKeyValMap);

	}
	
	@GetMapping("/reviewViewMoreData")
	public Map<String, List<Map<String, List<QuestionModel>>>> getViewMoreDataForReview(@RequestParam("formId") Integer formId,@RequestParam("submissionId") String submissionId,OAuth2Authentication auth,HttpSession session) {

		UserModel user = tokenInfoExtracter.getUserModelInfo(auth);
		Map<String, Object> paramKeyValMap = new HashMap<>();
		paramKeyValMap.put("review", "reviewData");
		return submissionManagementService.getViewMoreDataForReview(formId,user,submissionId,paramKeyValMap,session);

	}
	
	@GetMapping("/saveNotificationDetails")
	public ResponseEntity<String> saveNotificationDetails(@RequestParam("formId") Integer formId,
			@RequestParam("deadlineDays") Integer deadlineDays, @RequestParam("notifyByDays") Integer notifyByDays) {
		return submissionManagementService.saveNotificationDetail(formId, deadlineDays, notifyByDays);
	}
	

}
