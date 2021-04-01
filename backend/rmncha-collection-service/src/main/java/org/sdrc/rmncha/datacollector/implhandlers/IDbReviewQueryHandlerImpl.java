package org.sdrc.rmncha.datacollector.implhandlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sdrc.rmncha.repositories.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.co.sdrc.sdrcdatacollector.document.Question;
import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import in.co.sdrc.sdrcdatacollector.handlers.IDbReviewQueryHandler;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.DataObject;
import in.co.sdrc.sdrcdatacollector.models.ReviewHeader;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 * @author Azaruddin (azaruddin@sdrc.co.in)
 *
 */
@Component
public class IDbReviewQueryHandlerImpl implements IDbReviewQueryHandler {

	@Autowired
	private AreaRepository mongoAreaRepository;
	
	private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	@Override
	public DataObject setReviewHeaders(DataObject dataObject, Question question,
			Map<Integer, TypeDetail> typeDetailsMap, DataModel submissionData, String type) {

		if(question.getReviewHeader()!=null && question.getReviewHeader().trim().length() > 0){
			ReviewHeader header=new ReviewHeader();
			switch(question.getReviewHeader().split("_")[0]){
			case "L1":
			case "L2":
			case "L3":
			case "L4":
			case "L5":
				switch(question.getControllerType()) {
				case "dropdown":
					
					if (question.getControllerType().equals("dropdown")
							&& (question.getTableName() == null || question.getTableName().equals(""))) {

						if (question.getFieldType().equals("option")) {
							header = new ReviewHeader();
							header.setName(question.getReviewHeader());
							header.setValue(typeDetailsMap.get(submissionData.getData().get(question.getColumnName())).getName()
									.toString());

							dataObject.getFormDataHead().put(header.getName(), header.getValue());
						} else {

							// checkbox- case ie multiple option selection
							List<Integer> ids = (List<Integer>) submissionData.getData().get(question.getColumnName());
							String values = null;
							for (Integer id : ids) {

								String val = typeDetailsMap.get(id).getName().toString();
								if (values == null) {
									values = val;
								} else {
									values = values.concat(",") + val;
								}

							}
							
							header = new ReviewHeader();
							header.setName(question.getReviewHeader());
							header.setValue(values);

							dataObject.getFormDataHead().put(header.getName(), header.getValue());

						}

					} else if (question.getTableName() != null && question.getTableName().trim().length() > 0) {

						if (type.contains("dataReview") && !question.getReviewHeader().equals("L1_State")) {
							switch (question.getTableName().split("\\$")[0]) {

							case "area":
								header = new ReviewHeader();
								header.setName(question.getReviewHeader());
								if (submissionData.getData().get(question.getColumnName()) != null) {
									header.setValue(mongoAreaRepository
											.findByAreaId(Integer.parseInt(
													submissionData.getData().get(question.getColumnName()).toString()))
											.getAreaName());
								}

								dataObject.getFormDataHead().put(header.getName(), header.getValue());
							}
						}

					}
					break;
				case "textbox":{
					header=new ReviewHeader();
					header.setName(question.getReviewHeader());
					header.setValue(submissionData.getData().get(question.getColumnName()).toString() );

					dataObject.getFormDataHead().put(header.getName(), header.getValue());

					break;
				}
				case "Date Widget":
					header=new ReviewHeader();
					header.setName(question.getReviewHeader());
					header.setValue(submissionData.getData().get(question.getColumnName()) != null
							?dateFormat.format(submissionData.getData().get(question.getColumnName()))
							: null);

					dataObject.getFormDataHead().put(header.getName(), header.getValue());

					
				}
				break;
			}
			
			
		}
		if(type.contains("dataReview")) {
			ReviewHeader header = new ReviewHeader();
			header.setName("L6_Submitted by");
			header.setValue(type.split("_")[1]);
			dataObject.getFormDataHead().put(header.getName(), header.getValue());
			
			header = new ReviewHeader();
			header.setName("L7_Submitted on");
			header.setValue(dateFormat.format((Date) dataObject.getExtraKeys().get("syncDate")));
			dataObject.getFormDataHead().put(header.getName(), header.getValue());

//			if(dataObject.getExtraKeys().containsKey("actionDate")) {

//				if(dataObject.getExtraKeys().get("rejectMessage")!=null) {
			header = new ReviewHeader();
			header.setName("L8_Rejected on");
			if (dataObject.getExtraKeys().containsKey("actionDate"))
				header.setValue(dateFormat.format((Date) dataObject.getExtraKeys().get("actionDate")));
			dataObject.getFormDataHead().put(header.getName(), header.getValue());

			header = new ReviewHeader();
			header.setName("L9_Remark");
			if (dataObject.getExtraKeys().get("rejectMessage") != null) {
				header.setValue(dataObject.getExtraKeys().get("rejectMessage").toString());

			}
			dataObject.getFormDataHead().put(header.getName(), header.getValue());

//				if(dataObject.getExtraKeys().get("status").toString().equals(ChecklistSubmissionStatus.APPROVED.toString())) {
			header = new ReviewHeader();
			header.setName("L99_Approved on");
			if (dataObject.getExtraKeys().containsKey("actionDate"))
				header.setValue(dateFormat.format((Date) dataObject.getExtraKeys().get("actionDate")));
			dataObject.getFormDataHead().put(header.getName(), header.getValue());
//				}
		}

//		}
		
		return dataObject;
	}
}
