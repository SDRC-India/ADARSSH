package org.sdrc.rmncha.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.sdrc.rmncha.model.NotificationModel;
import org.sdrc.rmncha.model.UserModel;
import org.sdrc.rmncha.service.SubmissionService;
import org.sdrc.rmncha.util.TokenInfoExtracter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.co.sdrc.sdrcdatacollector.engine.FormsService;
import in.co.sdrc.sdrcdatacollector.models.DataObject;
import in.co.sdrc.sdrcdatacollector.models.FormAttachmentsModel;
import in.co.sdrc.sdrcdatacollector.models.QuestionUpdateModel;
import in.co.sdrc.sdrcdatacollector.models.ReceiveEventModel;
import in.co.sdrc.sdrcdatacollector.models.ReviewPageModel;

/**
 * @author Azar
 * 
 * @author Subham Ashish(subham@sdrc.co.in)
 * 
 * @author Sarita
 */

@RestController
@RequestMapping("/api")
public class DataEntryController {

	@Autowired
	private FormsService dataEntryService;

	@Autowired
	private SubmissionService submissionService;

	@Autowired
	private TokenInfoExtracter tokenInfoExtracter;

	@GetMapping("/getQuestion")
	@PreAuthorize("hasAnyAuthority('Data Entry', 'dataentry_HAVING_write','Data Entry & Visualization')")
	public QuestionUpdateModel getQuestions(
			@RequestParam(value = "lastUpdatedDate", required = false) String lastUpdatedDate, HttpSession session,
			OAuth2Authentication auth) {

		UserModel user = tokenInfoExtracter.getUserModelInfo(auth);

		Map<String, Object> map = new HashMap<>();
//		map.put("DISTRICT_ID", user.getAreas().get(0).getParentAreaId());
//		map.put("BLOCK_ID", user.getAreas().get(0).getAreaId());

//		return dataEntryService.getQuestions(map, session, user, (Integer) user.getDesgSlugIds().toArray()[0]);
		return dataEntryService.getQuestions(map, session, user, 2, lastUpdatedDate);
	}

	@RequestMapping(value = "/saveData", method = { RequestMethod.POST, RequestMethod.OPTIONS })
	public ResponseEntity<String> sendNewSubmissionCommand(@RequestBody ReceiveEventModel receiveEventModel,
			OAuth2Authentication oauth) throws Exception {
		return submissionService.saveSubmission(receiveEventModel, oauth);
	}

	@RequestMapping(value = "uploadFile", method = { RequestMethod.POST, RequestMethod.OPTIONS }, consumes = {
			"multipart/form-data" })
	public String uploadFiles(@RequestParam("file") MultipartFile file, @RequestParam("fileModel") String fileModel)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		FormAttachmentsModel readValue = objectMapper.readValue(fileModel, FormAttachmentsModel.class);

		int retryCount = 0;
		boolean uploaded = false;

		while (uploaded == false && retryCount < 1) {
			try {
				String response = submissionService.uploadFiles(file, readValue);
				uploaded = true;
				return response;
			} catch (OptimisticLockingFailureException e) {
				retryCount++;
				continue;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		throw new RuntimeException("Error while uploading files with payload {} " + fileModel);

	}

	@GetMapping("/getRejectedData")
	public Map<String, List<DataObject>> getRejectedData(HttpSession session, OAuth2Authentication auth) {
		UserModel user = tokenInfoExtracter.getUserModelInfo(auth);

		Map<String, Object> map = new HashMap<>();
		map.put("DISTRICT_ID", user.getAreas().get(0).getParentAreaId());
		map.put("BLOCK_ID", user.getAreas().get(0).getAreaId());

		return dataEntryService.getRejectedData(map, session, user, user.getDesgSlugIds().get(0));

	}

//	@GetMapping("/getDataForReview")
//	public ReviewPageModel  getDataForReview(@RequestParam("formId") Integer formId, String startDate, String endDate,HttpSession session,OAuth2Authentication auth) {
//		
//		UserModel user = tokenInfoExtracter.getUserModelInfo(auth);
//		Map<String, Object> paramKeyValMap = new HashMap<>();
//		paramKeyValMap.put("DISTRICT_ID", user.getAreas().get(0).getParentAreaId());
//		paramKeyValMap.put("BLOCK_ID", user.getAreas().get(0).getAreaId());
//		
//		return dataEntryService.getDataForReview(formId, startDate, endDate, paramKeyValMap, session, user,  user.getDesgSlugIds().get(0));
//	}
//	

	@GetMapping("/getAllForms")
	public ReviewPageModel getAllForms(HttpSession session, OAuth2Authentication auth) {
		UserModel user = tokenInfoExtracter.getUserModelInfo(auth);
		Map<String, Object> map = new HashMap<>();
		map.put("DISTRICT_ID", user.getAreas().get(0).getParentAreaId());
		map.put("BLOCK_ID", user.getAreas().get(0).getAreaId());

		return dataEntryService.getAllForms(map, session, user, user.getDesgSlugIds().get(0));
	}

	@GetMapping("/getNotificationDetail")
	@PreAuthorize("hasAnyAuthority('Data Entry', 'dataentry_HAVING_write','Data Entry & Visualization')")
	public Map<Integer, NotificationModel> getNotificationDetail() {

		return submissionService.findAllNotificationDetail();
	}
	
	@GetMapping("/updateTpxxxxx")
	public String updateTp() {
		return submissionService.updateTpInSubmission();
	}
	
	@GetMapping("/updateSubmissionStatus")
	public String updateSubmissionStatus() {
		return submissionService.updateSubmissionStatusToApprove();
	}
}
