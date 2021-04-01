package org.sdrc.rmncha.datacollector.implhandlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;

import in.co.sdrc.sdrcdatacollector.handlers.ICameraAndAttachmentsDataHandler;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.FormAttachmentsModel;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ICameraDataHandlerImpl implements ICameraAndAttachmentsDataHandler {
	
	@Autowired
	private ConfigurableEnvironment configurableEnvironment;
	
	public QuestionModel readExternal(QuestionModel model, DataModel dataModel, Map<String, Object> paramKeyValMap) {

		try {
			Map<String, List<FormAttachmentsModel>> attachmentsMap = dataModel.getAttachments();

			List<String> base64Values = null;

//			List<FileModel> fileModelList = null;

			List<String> localDevicePaths = null;

			if (attachmentsMap != null) {

				List<FormAttachmentsModel> attachments = attachmentsMap.get(model.getColumnName());

				if (attachments != null && !attachments.isEmpty()) {

					base64Values = new ArrayList<>();
					localDevicePaths = new ArrayList<>();
//					fileModelList = new ArrayList<>();

					if (paramKeyValMap.get("file") == null && !paramKeyValMap.containsKey("review")) {
						for (FormAttachmentsModel faModel : attachments) {

							String filePath = faModel.getFilePath();
							localDevicePaths.add(faModel.getLocalDevicePath());
							String linkAddress = configurableEnvironment.getProperty("image.download.path");
							linkAddress = linkAddress
									.concat("?path=" + Base64.getUrlEncoder().encodeToString(filePath.getBytes()));
							base64Values.add(linkAddress);

						}
						model.setValue(localDevicePaths);
						model.setAttachmentsInBase64(base64Values);
					} else if( paramKeyValMap.containsKey("review") ){ //for review data

						for (FormAttachmentsModel faModel : attachments) {
							base64Values.add(faModel.getFilePath());
							
							/*String filePath = faModel.getFilePath();
							localDevicePaths.add(faModel.getLocalDevicePath());
							FileModel fileModel = new FileModel();
							String linkAddress = configurableEnvironment.getProperty("image.download.path");
							linkAddress = linkAddress
									.concat("?path=" + Base64.getUrlEncoder().encodeToString(filePath.getBytes()));
							fileModel.setBase64(linkAddress);
							fileModel.setColumnName(faModel.getColumnName());
							fileModel.setFileName(
									faModel.getOriginalName().concat(".").concat(faModel.getFileExtension()));
							fileModel.setFileSize(faModel.getFileSize());
							fileModel.setFileType(faModel.getFileExtension());
							fileModelList.add(fileModel);*/
						}
//						model.setAttachmentsInBase64(fileModelList);
						model.setValue(localDevicePaths);
						model.setAttachmentsInBase64(base64Values);
					}

				}

			}

			return model;

		} catch (Exception e) {
			log.error("Action while generating base 64 value for camera {}", e);
			throw new RuntimeException(e);
		}

	}

	/*public QuestionModel readExternal(QuestionModel model, DataModel dataModel,Map<String,Object> paramKeyValMap) {

		try {
			Map<String, List<FormAttachmentsModel>> attachmentsMap = dataModel.getAttachments();

			List<String> base64Values =null;

			// List<String> files = new ArrayList<>();
			List<String> localDevicePaths = null;

			if (attachmentsMap != null) {
				
				List<FormAttachmentsModel> attachments = attachmentsMap.get(model.getColumnName());

				if (attachments!=null && !attachments.isEmpty()) {

					base64Values = new ArrayList<>();
					localDevicePaths = new ArrayList<>();
					
					for (FormAttachmentsModel faModel : attachments) {

						String filePath = faModel.getFilePath();
						localDevicePaths.add(faModel.getLocalDevicePath());
						if(!paramKeyValMap.containsKey("review")){
							String encodstring = encodeFileToBase64Binary(filePath, faModel.getFileExtension());
							base64Values.add(encodstring);
						}else{
							base64Values.add(filePath);
						}

					}

				}

			}

			model.setValue(localDevicePaths);
			model.setAttachmentsInBase64(base64Values);

			return model;

		} catch (Exception e) {
			log.error("Action while generating base 64 value for camera {}",e);
			throw new RuntimeException(e);
		}

	}*/

	/**
	 * 
	 * @param file-
	 *            convert file to base64 string
	 * @return - base64 converted value
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private String encodeFileToBase64Binary(String filePath, String extension) throws IOException {

		String encodedString = "data:image/".concat(extension).concat(";base64,");
		byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
		encodedString = encodedString.concat(java.util.Base64.getEncoder().encodeToString(fileContent));
		return encodedString;
		}
		
}
