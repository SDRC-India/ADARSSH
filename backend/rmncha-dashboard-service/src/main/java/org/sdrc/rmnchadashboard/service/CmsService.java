package org.sdrc.rmnchadashboard.service;

import java.util.List;
import java.util.Map;

import org.sdrc.rmnchadashboard.model.AttachmentModel;
import org.sdrc.rmnchadashboard.model.CmsDataModel;
import org.sdrc.rmnchadashboard.model.CmsDataRequestModel;
import org.sdrc.rmnchadashboard.model.ResponseModel;
import org.sdrc.rmnchadashboard.model.TableResponseModel;
import org.sdrc.rmnchadashboard.model.ViewDataRequestModel;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;

public interface CmsService {

	List<CmsDataModel> getCMSData();

	AttachmentModel uploadFile(MultipartFile file) throws Exception;

	ResponseModel saveUpdateData(CmsDataRequestModel cmsDataRequestModel);

	ResponseModel deleteData(CmsDataRequestModel cmsDataRequestModel);

	Map<String, JsonNode> getCMSRequestData(ViewDataRequestModel viewDataRequestModel);

	TableResponseModel getCMSRequestDataInTable(ViewDataRequestModel viewDataRequestModel);

}
