package org.sdrc.rmnchadashboard.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sdrc.rmnchadashboard.jpadomain.CmsData;
import org.sdrc.rmnchadashboard.jparepository.CMSDataRepository;
import org.sdrc.rmnchadashboard.model.AttachmentModel;
import org.sdrc.rmnchadashboard.model.CMSSubMenuDataModel;
import org.sdrc.rmnchadashboard.model.CmsDataModel;
import org.sdrc.rmnchadashboard.model.CmsDataRequestModel;
import org.sdrc.rmnchadashboard.model.CmsQuestionModel;
import org.sdrc.rmnchadashboard.model.ResponseModel;
import org.sdrc.rmnchadashboard.model.TableResponseModel;
import org.sdrc.rmnchadashboard.model.ViewDataRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class CmsServiceImpl implements CmsService{
	
	@Autowired
	private CMSDataRepository cmsDataRepository;
	
	@Value("${output.path.pdf}")
	private String directoryPath;

	@Value("${cms.upload.path}")
	private String cmsFilePath;
	
	@Override
	@Transactional
	public List<CmsDataModel> getCMSData() {
		List<CmsDataModel> cmsDataModels = new ArrayList<CmsDataModel>();

		Map<String, List<CMSSubMenuDataModel>> cmsSubMenuDataModelMap = new LinkedHashMap<String, List<CMSSubMenuDataModel>>();

		List<CmsData> cmsDatas = cmsDataRepository.findByIsLiveTrueOrderByCmsOrderAsc();

		ObjectMapper mapper = new ObjectMapper();
		for (CmsData cmsData : cmsDatas) {

			CMSSubMenuDataModel cmsSubMenuDataModel = new CMSSubMenuDataModel();

			cmsSubMenuDataModel.setLeftmenuId(cmsData.getId());
			cmsSubMenuDataModel.setLeftsubmenuName(cmsData.getSubMenu());
			List<CmsQuestionModel> questionModels = new ArrayList<CmsQuestionModel>();
			for (JsonNode jsonData : cmsData.getQuestions()) {
				CmsQuestionModel questionModel = mapper.convertValue(jsonData, CmsQuestionModel.class);

				// if control type is chip and value is null then we have to set it to blank
				// array as chip always accepts a array
				if (questionModel.getControlType().equalsIgnoreCase("chips")) {
					List<String> blank = new ArrayList<String>();
					questionModel.setValue(blank);
				}

				// in case of form type value display we will set the pre-filled values in the
				// question model
				if (cmsData.getValueShowType().equalsIgnoreCase("form") && cmsData.getValues() != null
						&& cmsData.getValues().get(0).has(questionModel.getColumnName())) {
					questionModel.setValue(cmsData.getValues().get(0).get(questionModel.getColumnName()));
				}

				// if max limit is reached then we will disable the blank form
//				if (cmsData.getValues() != null && cmsData.getValues().size() == cmsData.getMaxvalues()
//						&& cmsData.getValueShowType().equalsIgnoreCase("table")) {
//					questionModel.setDisabled(true);
//				}

				questionModels.add(questionModel);

			}

			// if value show type is table and user has entered some value then we will set
			// the values in table response model
			if (cmsData.getValueShowType().equalsIgnoreCase("table") && cmsData.getValues() != null) {

				TableResponseModel tableResponseModel = new TableResponseModel();

				// getting all the column keys for which table header will be set
				Set<String> coloumsKeys = new LinkedHashSet<String>();
				coloumsKeys.addAll(Arrays.asList(cmsData.getTableHeader().split(",")));

				List<String> tableColumns = questionModels.stream().filter(d -> coloumsKeys.contains(d.getColumnName()))
						.map(v -> v.getHeaderName()).collect(Collectors.toList());

				// default column will be present in every table in cms edit section
				tableColumns.add("Action");

				List<Map<String, Object>> tableData = new ArrayList<Map<String, Object>>();

				for (JsonNode jsonNode : cmsData.getValues()) {

					Map<String, Object> tableDataMap = mapper.convertValue(jsonNode, Map.class);

					for (String coloumsKey : coloumsKeys) {
						if (questionModels.stream().filter(d -> coloumsKey.equals(d.getColumnName())).count() > 0) {
							CmsQuestionModel questionModel = questionModels.stream()
									.filter(d -> coloumsKey.equals(d.getColumnName())).findFirst().get();
							if (questionModel.getControlType().equals("file")) {
								tableDataMap.put(questionModel.getHeaderName(),
										jsonNode.get(questionModel.getColumnName()).get(0).get("originalName"));

							}

							else if (jsonNode.get(questionModel.getColumnName()).isArray()) {
								List<String> tags = mapper.convertValue(jsonNode.get(questionModel.getColumnName()),
										ArrayList.class);
								tableDataMap.put(questionModel.getHeaderName(), String.join(",", tags));

							} else {
								tableDataMap.put(questionModel.getHeaderName(),
										jsonNode.get(questionModel.getColumnName()));
							}
						}
					}

					tableDataMap.put("Action", getCMSAction(cmsData.isRemovable()));
					tableData.add(tableDataMap);

				}

				tableResponseModel.setTableColumns(tableColumns);
				tableResponseModel.setTableData(tableData);
				cmsSubMenuDataModel.setTableData(tableResponseModel);
			}

			cmsSubMenuDataModel.setValueShowType(cmsData.getValueShowType());
			cmsSubMenuDataModel.setQuestions(questionModels);

			if (cmsSubMenuDataModelMap.containsKey(cmsData.getMainMenu())) {
				cmsSubMenuDataModelMap.get(cmsData.getMainMenu()).add(cmsSubMenuDataModel);
			}

			else {
				List<CMSSubMenuDataModel> cmsSubMenuDataModels = new ArrayList<CMSSubMenuDataModel>();
				cmsSubMenuDataModels.add(cmsSubMenuDataModel);
				cmsSubMenuDataModelMap.put(cmsData.getMainMenu(), cmsSubMenuDataModels);
			}

		}

		cmsSubMenuDataModelMap.forEach((k, v) -> {
			CmsDataModel cmsDataModel = new CmsDataModel();
			cmsDataModel.setLeftmenuName(k);
			cmsDataModel.setLeftsubmenu(v);
			cmsDataModels.add(cmsDataModel);
		});

		return cmsDataModels;
	}

	@Override
	public AttachmentModel uploadFile(MultipartFile file) throws Exception {
		try {
			
			Path filePath = Paths.get(directoryPath+cmsFilePath);
			AttachmentModel attachment = new AttachmentModel();

			attachment.setOriginalName(file.getOriginalFilename());

			String fileName;
			{
				fileName = file.getOriginalFilename().trim()
						.replaceAll("." + file.getOriginalFilename().trim().split("\\.")[1], "") + new Date().getTime()
						+ "." + file.getOriginalFilename().trim().split("\\.")[1];
			}

			Files.copy(file.getInputStream(), filePath.resolve(fileName));

			attachment.setFileSize(getStringSizeLengthFile(file.getSize()));

			attachment.setFilePath(fileName);

			return attachment;// this.rootLocation.resolve(fileName).toAbsolutePath().toString();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to upload");

		}
	}

	private String getStringSizeLengthFile(long size) {

		DecimalFormat df = new DecimalFormat("0.00");

		float sizeKb = 1024.0f;
		float sizeMb = sizeKb * sizeKb;
		float sizeGb = sizeMb * sizeKb;
		float sizeTerra = sizeGb * sizeKb;

		if (size < sizeMb)
			return df.format(size / sizeKb) + " Kb";
		else if (size < sizeGb)
			return df.format(size / sizeMb) + " Mb";
		else if (size < sizeTerra)
			return df.format(size / sizeGb) + " Gb";

		return "";
	}

	@Override
	@Transactional
	public ResponseModel saveUpdateData(CmsDataRequestModel cmsDataRequestModel) {
		ResponseModel responseModel = new ResponseModel();

		try {
			ObjectMapper objectMapper = new ObjectMapper();

			CmsData cmsData = cmsDataRepository.findById(cmsDataRequestModel.getContentId());
			if (cmsData != null) {
				if (cmsDataRequestModel.isUpdate()) {

					if (cmsData.getValues() != null) {
						if (cmsData.getValueShowType().equalsIgnoreCase("table")) {

							JsonNode jsonNode = cmsData.getValues();

							ArrayNode arrayNode = ((ArrayNode) jsonNode);

							JsonNode data = objectMapper.readValue(cmsDataRequestModel.getCmsData(), JsonNode.class);

							int i = 0;
							for (JsonNode arrayNodeSingle : arrayNode) {
								// remove old data at that index and add the updated one
								if (Integer.parseInt(arrayNodeSingle.get("index").toString()) == cmsDataRequestModel
										.getIndex()) {
									arrayNode.remove(i);
									arrayNode.add(data);
									break;
								}

								i++;
							}
							JsonNodeFactory factory = JsonNodeFactory.instance;
							ArrayNode sortedNode = new ArrayNode(factory);

							List<JSONObject> jsonValues = new ArrayList<JSONObject>();
							for (int k = 0; k < arrayNode.size(); k++) {
								jsonValues.add(objectMapper.convertValue(arrayNode.get(k), JSONObject.class));
							}

							// sort the json node in asc order on index
							Collections.sort(jsonValues, new Comparator<JSONObject>() {
								// You can change "Name" with "ID" if you want to sort by ID
								private static final String KEY_NAME = "index";

								@Override
								public int compare(JSONObject a, JSONObject b) {
									Integer valA = 0;
									Integer valB = 0;

									try {
										valA = Integer.parseInt(a.get(KEY_NAME).toString());
										valB = Integer.parseInt(b.get(KEY_NAME).toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
									return valA.compareTo(valB);
									// if you want to change the sort order, simply use the following:
									// return -valA.compareTo(valB);
								}
							});

							for (int k = 0; k < jsonValues.size(); k++) {
								sortedNode.add(objectMapper.convertValue(jsonValues.get(k), JsonNode.class));
							}

							cmsData.setValues(sortedNode);

							responseModel.setMessage("Record updated Successfully");
							responseModel.setStatusCode(HttpStatus.OK.value());

							cmsDataRepository.save(cmsData);
						}

						else if (cmsData.getValueShowType().equalsIgnoreCase("form")) {

							JSONArray jsonArray = new JSONArray();

							jsonArray.add(objectMapper.readValue(cmsDataRequestModel.getCmsData(), JsonNode.class));

							JsonNode jsonNode = objectMapper.convertValue(jsonArray, JsonNode.class);

							// completely replaces old value
							cmsData.setValues(jsonNode);

							cmsDataRepository.save(cmsData);

							responseModel.setMessage("Record Updated Successfully");
							responseModel.setStatusCode(HttpStatus.OK.value());
						}

					} else {

						responseModel.setMessage("No data found to update");
						responseModel.setStatusCode(HttpStatus.NO_CONTENT.value());

					}

				}

				else {
					if (cmsData.getValues() == null) {

						JsonNodeFactory factory = JsonNodeFactory.instance;
						ArrayNode sortedNode = new ArrayNode(factory);

						JsonNode jsonNode = objectMapper.readValue(cmsDataRequestModel.getCmsData(), JsonNode.class);

						ObjectNode node = (ObjectNode) jsonNode;

						node.put("index", 0);
						sortedNode.add(node);

						cmsData.setValues(sortedNode);

						responseModel.setMessage("Record Added Successfully");
						responseModel.setStatusCode(HttpStatus.OK.value());
					} else {

						if ((cmsData.getMaxvalues() > cmsData.getValues().size() || cmsData.getMaxvalues() == -1)
								&& cmsData.getValueShowType().equalsIgnoreCase("table")) {
							JsonNode jsonNode = cmsData.getValues();
							ArrayNode arrayNode = ((ArrayNode) jsonNode);

							JsonNode jsonNode1 = objectMapper.readValue(cmsDataRequestModel.getCmsData(),
									JsonNode.class);

							ObjectNode node = (ObjectNode) jsonNode1;

							// setting new index for new data
							node.put("index",
									Integer.parseInt(jsonNode.get(jsonNode.size() - 1).get("index").toString()) + 1);

							arrayNode.add(node);

							cmsData.setValues(arrayNode);

							cmsDataRepository.save(cmsData);

							responseModel.setMessage("Record Added Successfully");
							responseModel.setStatusCode(HttpStatus.OK.value());

						} else if (cmsData.getValueShowType().equalsIgnoreCase("form")) {
							JSONArray jsonArray = new JSONArray();

							JsonNode jsonNode1 = objectMapper.readValue(cmsDataRequestModel.getCmsData(),
									JsonNode.class);

							ObjectNode node = (ObjectNode) jsonNode1;

							node.put("index", 0);

							jsonArray.add(jsonNode1);
							JsonNode jsonNode = objectMapper.convertValue(jsonArray, JsonNode.class);

							cmsData.setValues(jsonNode);

							cmsDataRepository.save(cmsData);

							responseModel.setMessage("Record updated Successfully");
							responseModel.setStatusCode(HttpStatus.OK.value());
						} else {
							responseModel.setMessage("You cannot add more than " + cmsData.getMaxvalues() + " ");
							responseModel.setStatusCode(HttpStatus.INSUFFICIENT_STORAGE.value());
						}
					}
				}
			} else {
				responseModel.setMessage("No data found");
				responseModel.setStatusCode(HttpStatus.NO_CONTENT.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel.setMessage(e.getLocalizedMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.value());

		}
		return responseModel;
	}

	@Override
	public ResponseModel deleteData(CmsDataRequestModel cmsDataRequestModel) {
		ResponseModel responseModel = new ResponseModel();

		try {
			ObjectMapper objectMapper = new ObjectMapper();

			CmsData cmsData = cmsDataRepository.findById(cmsDataRequestModel.getContentId());
			if (cmsData != null) {
				if (cmsDataRequestModel.isUpdate()) {

					if (cmsData.getValues() != null) {
						if (cmsData.getValueShowType().equalsIgnoreCase("table") && cmsData.isRemovable()) {

							JsonNode jsonNode = cmsData.getValues();

							ArrayNode arrayNode = ((ArrayNode) jsonNode);

							int i = 0;

							// removing the data from array on the index key
							for (JsonNode arrayNodeSingle : arrayNode) {
								if (Integer.parseInt(arrayNodeSingle.get("index").toString()) == cmsDataRequestModel
										.getIndex()) {
									arrayNode.remove(i);
									break;
								}

								i++;
							}
							JsonNodeFactory factory = JsonNodeFactory.instance;
							ArrayNode sortedNode = new ArrayNode(factory);

							List<JSONObject> jsonValues = new ArrayList<JSONObject>();
							for (int k = 0; k < arrayNode.size(); k++) {
								jsonValues.add(objectMapper.convertValue(arrayNode.get(k), JSONObject.class));
							}

							Collections.sort(jsonValues, new Comparator<JSONObject>() {
								// You can change "Name" with "ID" if you want to sort by ID
								private static final String KEY_NAME = "index";

								@Override
								public int compare(JSONObject a, JSONObject b) {
									Integer valA = 0;
									Integer valB = 0;

									try {
										valA = Integer.parseInt(a.get(KEY_NAME).toString());
										valB = Integer.parseInt(b.get(KEY_NAME).toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
									return valA.compareTo(valB);
									// if you want to change the sort order, simply use the following:
									// return -valA.compareTo(valB);
								}
							});

							for (int k = 0; k < jsonValues.size(); k++) {
								sortedNode.add(objectMapper.convertValue(jsonValues.get(k), JsonNode.class));
							}

							if (sortedNode.size() > 0)
								cmsData.setValues(sortedNode);

							else
								cmsData.setValues(null);

							responseModel.setMessage("Record deleted Successfully");
							responseModel.setStatusCode(HttpStatus.OK.value());

							cmsDataRepository.save(cmsData);
						}

						else if (cmsData.getValueShowType().equalsIgnoreCase("form")) {

							responseModel.setMessage("You cannot delete this record");
							responseModel.setStatusCode(HttpStatus.NO_CONTENT.value());
						}

					} else {

						responseModel.setMessage("No data found to delete");
						responseModel.setStatusCode(HttpStatus.NO_CONTENT.value());

					}

				}

			} else {
				responseModel.setMessage("No data found");
				responseModel.setStatusCode(HttpStatus.NO_CONTENT.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel.setMessage(e.getLocalizedMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.value());

		}
		return responseModel;
	}

	@Override
	public Map<String, JsonNode> getCMSRequestData(ViewDataRequestModel viewDataRequestModel) {
		Map<String, JsonNode> jsonMap = new LinkedHashMap<>();

		List<CmsData> cmsDatas = new ArrayList<CmsData>();

		if (viewDataRequestModel.getSubMenu() == null || viewDataRequestModel.getSubMenu().equals(""))
			cmsDatas = cmsDataRepository.findByMainMenuOrderByCmsOrderAsc(viewDataRequestModel.getMainMenu());

		else {
			cmsDatas.add(cmsDataRepository.findBySubMenuAndMainMenuOrderByCmsOrderAsc(viewDataRequestModel.getSubMenu(),
					viewDataRequestModel.getMainMenu()));
		}

		for (CmsData cmsData : cmsDatas) {
			if (cmsData.getValues() != null) {
				jsonMap.put(cmsData.getSubMenu(), cmsData.getValues());
			}
		}

		return jsonMap;
	}

	@Override
	public TableResponseModel getCMSRequestDataInTable(ViewDataRequestModel viewDataRequestModel) {

		TableResponseModel tableResponseModel = new TableResponseModel();

		ObjectMapper mapper = new ObjectMapper();

		List<String> tableColumns = new ArrayList<String>();

		List<Map<String, Object>> tableData = new ArrayList<Map<String, Object>>();

		List<CmsData> cmsDatas = new ArrayList<CmsData>();

		if (viewDataRequestModel.getSubMenu() == null || viewDataRequestModel.getSubMenu().equals(""))
			cmsDatas = cmsDataRepository.findByMainMenuOrderByCmsOrderAsc(viewDataRequestModel.getMainMenu());

		else {
			CmsData cmsData = cmsDataRepository.findBySubMenuAndMainMenuOrderByCmsOrderAsc(
					viewDataRequestModel.getSubMenu(), viewDataRequestModel.getMainMenu());

			if (cmsData != null)
				cmsDatas.add(cmsData);
		}

		for (CmsData cmsData : cmsDatas) {

			List<CmsQuestionModel> questionModels = new ArrayList<CmsQuestionModel>();
			for (JsonNode jsonData : cmsData.getQuestions()) {
				CmsQuestionModel questionModel = mapper.convertValue(jsonData, CmsQuestionModel.class);

				if (questionModel.getControlType().equalsIgnoreCase("chips")) {
					List<String> blank = new ArrayList<String>();
					questionModel.setValue(blank);
				}

				questionModels.add(questionModel);

			}

			Set<String> coloumsKeys = new LinkedHashSet<String>();
			coloumsKeys.addAll(Arrays.asList(cmsData.getTableHeader().split(",")));

			tableColumns = questionModels.stream().filter(d -> coloumsKeys.contains(d.getColumnName()))
					.map(v -> v.getHeaderName()).collect(Collectors.toList());

			if(!viewDataRequestModel.getSubMenu().equals("Tools")) { //tools can have excel also. so,  preview will not work 
				tableColumns.add("Preview");
			}
			
			tableColumns.add("Download");

			if (cmsData.getValues() != null) {
				for (JsonNode jsonNode : cmsData.getValues()) {

					Map<String, Object> tableDataMap = mapper.convertValue(jsonNode, Map.class);
					String fileSize = "";
					for (String coloumsKey : coloumsKeys) {
						if (questionModels.stream().filter(d -> coloumsKey.equals(d.getColumnName())).count() > 0) {
							CmsQuestionModel questionModel = questionModels.stream()
									.filter(d -> coloumsKey.equals(d.getColumnName())).findFirst().get();
							if (questionModel.getControlType().equals("file")) {
								fileSize = String.valueOf(
										jsonNode.get(questionModel.getColumnName()).get(0).get("fileSize").asText());
								tableDataMap.put(questionModel.getHeaderName(),
										jsonNode.get(questionModel.getColumnName()).get(0).get("originalName"));
							}

							else if (jsonNode.get(questionModel.getColumnName()).isArray()) {
								List<String> tags = mapper.convertValue(jsonNode.get(questionModel.getColumnName()),
										ArrayList.class);
								tableDataMap.put(questionModel.getHeaderName(), String.join(",", tags));

							} else {
								tableDataMap.put(questionModel.getHeaderName(),
										jsonNode.get(questionModel.getColumnName()));
							}
						}
					}

					if(!viewDataRequestModel.getSubMenu().equals("Tools")) {
						tableDataMap.put("Preview", getCMSPreViewAction());
					}
					
					tableDataMap.put("Download", getCMSViewAction(fileSize));
					tableData.add(tableDataMap);
				}
			}
		}
		tableResponseModel.setTableColumns(tableColumns);
		tableResponseModel.setTableData(tableData);

		return tableResponseModel;
	}
	private List<Map<String, String>> getCMSAction(boolean check) {
		List<Map<String, String>> actionDetails = new ArrayList<Map<String, String>>();
		Map<String, String> actionDetailsMap = new LinkedHashMap<String, String>();

		actionDetailsMap = new LinkedHashMap<String, String>();
		actionDetailsMap.put("controlType", "button");
		actionDetailsMap.put("value", "");
		actionDetailsMap.put("type", "submit");
		actionDetailsMap.put("class", "btn btn-submit edit");
		actionDetailsMap.put("tooltip", "Edit");
		actionDetailsMap.put("icon", "fa-pencil fa-lg");
		actionDetails.add(actionDetailsMap);

		if (check) {
			actionDetailsMap = new LinkedHashMap<String, String>();
			actionDetailsMap.put("controlType", "button");
			actionDetailsMap.put("value", "");
			actionDetailsMap.put("type", "submit");
			actionDetailsMap.put("class", "btn btn-submit delete");
			actionDetailsMap.put("tooltip", "Delete");
			actionDetailsMap.put("icon", "fa-trash fa-lg");
			actionDetails.add(actionDetailsMap);

		}

		return actionDetails;
	}

	private List<Map<String, String>> getCMSViewAction(String size) {
		List<Map<String, String>> actionDetails = new ArrayList<Map<String, String>>();
		Map<String, String> actionDetailsMap = new LinkedHashMap<String, String>();

		actionDetailsMap = new LinkedHashMap<String, String>();
		actionDetailsMap.put("controlType", "button");
		actionDetailsMap.put("value", "File Size " + size);
		actionDetailsMap.put("type", "submit");
		actionDetailsMap.put("class", "btn btn-submit download");
		actionDetailsMap.put("tooltip", "Download");
		actionDetailsMap.put("icon", "fa-download fa-lg");
		actionDetails.add(actionDetailsMap);

		return actionDetails;
	}

	private List<Map<String, String>> getCMSPreViewAction() {
		List<Map<String, String>> actionDetails = new ArrayList<Map<String, String>>();
		Map<String, String> actionDetailsMap = new LinkedHashMap<String, String>();

		actionDetailsMap = new LinkedHashMap<String, String>();
		actionDetailsMap.put("controlType", "button");
		actionDetailsMap.put("value", "Preview");
		actionDetailsMap.put("type", "submit");
		actionDetailsMap.put("class", "btn btn-submit preview");
		actionDetailsMap.put("tooltip", "Preview");
		actionDetailsMap.put("icon", "fa-eye fa-lg");
		actionDetails.add(actionDetailsMap);

		return actionDetails;
	}

	private List<Map<String, String>> getAction(boolean check) {
		List<Map<String, String>> actionDetails = new ArrayList<Map<String, String>>();
		Map<String, String> actionDetailsMap = new LinkedHashMap<String, String>();

		actionDetailsMap = new LinkedHashMap<String, String>();
		actionDetailsMap.put("controlType", "button");
		actionDetailsMap.put("value", "");
		actionDetailsMap.put("type", "submit");
		actionDetailsMap.put("class", "btn btn-submit reset-pass");
		actionDetailsMap.put("tooltip", "Change Password");
		actionDetailsMap.put("icon", "fa-key");
		actionDetails.add(actionDetailsMap);

		actionDetailsMap = new LinkedHashMap<String, String>();
		actionDetailsMap.put("controlType", "button");
		actionDetailsMap.put("value", "");
		actionDetailsMap.put("type", "submit");
		actionDetailsMap.put("class", "btn btn-submit edit-user");
		actionDetailsMap.put("tooltip", "Edit User");
		actionDetailsMap.put("icon", "fa-edit");
		actionDetails.add(actionDetailsMap);

		if (check) {
			actionDetailsMap = new LinkedHashMap<String, String>();
			actionDetailsMap.put("controlType", "button");
			actionDetailsMap.put("value", "");
			actionDetailsMap.put("type", "submit");
			actionDetailsMap.put("class", "btn btn-submit disable");
			actionDetailsMap.put("tooltip", "Disable User");
			actionDetailsMap.put("icon", "fa-toggle-on");
			actionDetails.add(actionDetailsMap);

		}

		else {
			actionDetailsMap = new LinkedHashMap<String, String>();
			actionDetailsMap.put("controlType", "button");
			actionDetailsMap.put("value", "");
			actionDetailsMap.put("type", "submit");
			actionDetailsMap.put("class", "btn btn-submit enable");
			actionDetailsMap.put("tooltip", "Enable User");
			actionDetailsMap.put("icon", "fa-toggle-off");
			actionDetails.add(actionDetailsMap);

		}

		return actionDetails;
	}

}
