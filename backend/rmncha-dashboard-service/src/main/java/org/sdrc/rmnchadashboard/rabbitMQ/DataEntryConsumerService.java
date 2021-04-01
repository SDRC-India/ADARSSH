package org.sdrc.rmnchadashboard.rabbitMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmncha.domain.TimePeriod;
import org.sdrc.rmncha.mongodomain.AllChecklistFormData;
import org.sdrc.rmncha.mongorepository.AllChecklistFormDataRepository;
import org.sdrc.rmncha.repositories.AreaRepository;
import org.sdrc.rmncha.repositories.TimePeriodRepository;
import org.sdrc.rmnchadashboard.web.model.SubmissionCompleteStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import in.co.sdrc.sdrcdatacollector.models.FormAttachmentsModel;
import lombok.extern.slf4j.Slf4j;

/**
 *  This service consumes new data entry and approval/rejection message from the collection service 
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 19-Jul-2019 3:17:34 PM
 */
@Service
@Slf4j
public class DataEntryConsumerService {

	@Autowired
	private AllChecklistFormDataRepository allChecklistFormDataRepository;

	@Autowired
	private AreaRepository mongoAreaRepository;
	
	@Autowired
	private TimePeriodRepository timePeriodRepository;
	
	/**
	 * saves New Entry in db
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 19-Jul-2019 3:17:37 PM
	 * @param queuingAllChecklistFormData
	 */
	@StreamListener(value = CollectionQueryChannel.RMNCHADATAENTRY_INPUTCHANNEL)
	public void saveFormData(AllChecklistFormDataEvent queuingAllChecklistFormData) {

		AllChecklistFormData allChecklistFormData = new AllChecklistFormData();
		Area area = mongoAreaRepository.findByAreaCode(queuingAllChecklistFormData.getArea().getAreaCode());
		
		BeanUtils.copyProperties(queuingAllChecklistFormData.getArea(), area);
		Area savedArea = mongoAreaRepository.save(area); //update area as per submission

		TimePeriod tergetTp = new TimePeriod();
		BeanUtils.copyProperties(queuingAllChecklistFormData.getTimePeriod(), tergetTp);
		
		BeanUtils.copyProperties(queuingAllChecklistFormData, allChecklistFormData);
		
		allChecklistFormData.setTimePeriod(tergetTp);
		allChecklistFormData.setArea(savedArea);
		
		log.info("Dashboard service with payload : {}", allChecklistFormData);
		
		allChecklistFormDataRepository.save(allChecklistFormData);
	}

	/**
	 * updates status of submission
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 19-Jul-2019 3:17:39 PM
	 * @param queuingAllChecklistFormData
	 */
	
	@StreamListener(value = CollectionQueryChannel.RMNCHASUBMISSION_INPUTCHANNEL)
	public void approveRejectData(SubmissionActionEvent submittedActionEvent) {
		
		AllChecklistFormData data = allChecklistFormDataRepository.findById(submittedActionEvent.getSubmissionId());
		if(submittedActionEvent.getRejected()!=null)
			data.setRejected(submittedActionEvent.getRejected());
		data.setRejectedApprovedDate(submittedActionEvent.getRejectedApprovedDate());
		data.setActionByUserName(submittedActionEvent.getActionByUserName());
		data.setActionByFullName(submittedActionEvent.getActionByFullName());
		data.setRejectMessage(submittedActionEvent.getRejectMessage());
		data.setChecklistSubmissionStatus(submittedActionEvent.getChecklistSubmissionStatus());
		data.setIsValid(submittedActionEvent.getIsValid());

		allChecklistFormDataRepository.save(data);
	}
	
	/**
	 * creates new timperiod
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 25-Jul-2019 2:51:50 PM
	 * @param tpActionEvent
	 */
	@StreamListener(value = CollectionQueryChannel.RMNCHATIMEPERIOD_INPUTCHANNEL)
	public void createTimePeriod(TimePeriodEvent tpActionEvent) {
		TimePeriod timePeriod = new TimePeriod();
		BeanUtils.copyProperties(tpActionEvent, timePeriod);
		timePeriodRepository.save(timePeriod);
	}
	
	/**
	 * updates submission's default status
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 29-Jul-2019 8:15:50 PM
	 * @param submissionIds
	 */
	/*@StreamListener(value = CollectionQueryChannel.RMNCHADEFAULT_INPUTCHANNEL)
	public void updateDefaultStatus(List<String> submissionIds) {
		
		List<AllChecklistFormData> formDatas = new ArrayList<>();

		for (AllChecklistFormData allChecklistFormData : allChecklistFormDataRepository.findByIdIn(submissionIds)) {
			allChecklistFormData.setChecklistSubmissionStatus(ChecklistSubmissionStatus.DEFAULT);
			
			formDatas.add(allChecklistFormData);
		}
		
		allChecklistFormDataRepository.save(formDatas);
	}*/
	
	/**
	 * consumes submission's attachments
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 31-Jul-2019 2:45:44 PM
	 * @param submissionIdFormIdMap
	 */

	@StreamListener(value = CollectionQueryChannel.RMNCHAATTACHMENT_INPUTCHANNEL)
	
	public void updateAttachments(Map<String, Map<String, List<FormAttachmentsModel>> > submissionIdFormIdMap) {
		
		Entry<String, Map<String, List<FormAttachmentsModel>>> entry = submissionIdFormIdMap.entrySet().iterator().next();
		
		AllChecklistFormData allChecklistFormData = allChecklistFormDataRepository.findById(entry.getKey());
		allChecklistFormData.setAttachments(entry.getValue());
		allChecklistFormData.setSubmissionCompleteStatus(SubmissionCompleteStatus.C);
		allChecklistFormDataRepository.save(allChecklistFormData);
	}
	
	/**
	 * updates duplicate record isDuplicate=true
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 29-Jul-2019 8:15:50 PM
	 * @param submissionIds
	 */
	@StreamListener(value = CollectionQueryChannel.RMNCHADUPLICATE_INPUTCHANNEL)
	public void updateDuplicateStatus(List<String> submissionIds) {
		
		List<AllChecklistFormData> formDatas = new ArrayList<>();

		for (AllChecklistFormData allChecklistFormData : allChecklistFormDataRepository.findByIdIn(submissionIds)) {
			allChecklistFormData.setDuplicate(true);
			
			formDatas.add(allChecklistFormData);
		}
		
		allChecklistFormDataRepository.save(formDatas);
	}
}
