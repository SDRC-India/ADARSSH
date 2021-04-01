package org.sdrc.rmncha.datacollector.implhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import javax.servlet.http.HttpSession;

import org.sdrc.rmncha.domain.Area;
import org.sdrc.rmncha.domain.AreaLevel;
import org.sdrc.rmncha.model.UserModel;
import org.sdrc.rmncha.repositories.AreaLevelRepository;
import org.sdrc.rmncha.repositories.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import in.co.sdrc.sdrcdatacollector.document.Question;
import in.co.sdrc.sdrcdatacollector.document.TypeDetail;
import in.co.sdrc.sdrcdatacollector.handlers.IDbQueryHandler;
import in.co.sdrc.sdrcdatacollector.models.OptionModel;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 *
 */
@Component
public class IDbQueryHandlerImpl implements IDbQueryHandler {
	
	@Autowired
	private AreaRepository mongoAreaRepository;
	
	@Autowired
	private AreaLevelRepository mongoAreaLevelRepository;

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;
	
	@Override
	public List<OptionModel> getOptions(QuestionModel questionModel, Map<Integer, TypeDetail> typeDetailsMap,
			Question question, String checkedValue, Object user1, Map<String,Object> paramKeyValueMap) {

//		System.out.println("form id:"+questionModel.getFormId() +"ques id="+questionModel.getColumnName());
		
		List<OptionModel> listOfOptions = new ArrayList<>();
		String tableName = questionModel.getTableName().split("\\$\\$")[0].trim();
		String areaLevel = "";
		if(tableName.equals("area"))
			areaLevel = questionModel.getTableName().split("\\$\\$")[1].trim().split("=")[1];
		List<Area> areas = null;

		UserModel user = (UserModel) user1;

		switch (tableName) {

		case "area":{
//			listOfOptions = new ArrayList<>();
			switch (areaLevel) {

			case "state": {
				List<Area> area = user.getAreas();
				// not to store duplicate district as combination of multiple
				// blocks,village might belong to same district
				Set<Area> district = new LinkedHashSet<>();

				for (Area a : area) {

					switch (a.getAreaLevel().getAreaLevelId()) {

					case 1:// get all state associate with national user
						areas = mongoAreaRepository.findByParentAreaId(a.getAreaId());
						break;
					case 2:// get state belongs to state user
						areas = mongoAreaRepository.findByAreaIdOrderByAreaName(a.getAreaId());
						break;
					case 3:// get state belongs to district user
						areas = mongoAreaRepository.findByAreaLevelAreaLevelIdAndAreaId(2, a.getStateId());
						break;
					case 4:// get state belongs to block level user
						areas = mongoAreaRepository.findByAreaLevelAreaLevelIdAndAreaId(2, a.getStateId());
						break;
					case 5:// get state belongs to facility user
						areas = mongoAreaRepository.findByAreaLevelAreaLevelIdAndAreaId(2, a.getStateId());
						break;
					}

					if(user.getAreaLevel().equals(a.getAreaLevel().getAreaLevelName()))
						district.addAll(areas);
				}
				// convert set to list
				areas = new ArrayList<>(district);
			}
				break;

			case "district":
			// @formatter:off
			{
				List<Area> area = user.getAreas();
				// not to store duplicate district as combination of multiple
				// blocks,village might belong to same district
				Set<Area> district = new LinkedHashSet<>();

				for (Area a : area) {

					switch (a.getAreaLevel().getAreaLevelId()) {

					case 1:// get all district associate with national user
						areas = mongoAreaRepository.findByAreaLevelAreaLevelId(3);
						break;
					case 2:
						areas = mongoAreaRepository.findByParentAreaId(a.getAreaId());
						break;
					case 3:// district
						areas = mongoAreaRepository.findByAreaIdOrderByAreaName(a.getAreaId());
						break;
					case 4:// get all district associated to block
						areas = mongoAreaRepository.findByAreaLevelAreaLevelIdAndAreaId(3, a.getDistrictId());
						break;
					case 5: // get district associated to facility
						areas = mongoAreaRepository.findByAreaLevelAreaLevelIdAndAreaId(3, a.getDistrictId());
						break;

					}
					if(user.getAreaLevel().equals(a.getAreaLevel().getAreaLevelName()))
						district.addAll(areas);
				}

				// convert set to list
				areas = new ArrayList<>(district);
			}
				// @formatter:on

				break;

			case "block":
			// @formatter:off
			{

				List<Area> area = user.getAreas();
				Set<Area> block = new LinkedHashSet<>();

				for (Area a : area) {

					switch (a.getAreaLevel().getAreaLevelId()) {
					case 1:
						areas = mongoAreaRepository.findByAreaLevelAreaLevelId(4);
						break;
					case 2:
						areas = mongoAreaRepository.findByAreaLevelAreaLevelIdAndStateId(4, a.getAreaId());
						break;
					case 3:
						areas = mongoAreaRepository.findByParentAreaId(a.getAreaId());
						break;
					case 4:
						areas = mongoAreaRepository.findByAreaIdOrderByAreaName(a.getAreaId());
						break;
					case 5:// get block associated with the village
						areas = mongoAreaRepository.findByAreaLevelAreaLevelIdAndAreaId(4, a.getBlockId());
						break;
					}
					if(user.getAreaLevel().equals(a.getAreaLevel().getAreaLevelName()))
						block.addAll(areas);
				}
				// convert set to list
				areas = new ArrayList<>(block);
			}
				// @formatter:on
				break;
			case "facility":
			// @formatter:off
			{

				List<Area> area = user.getAreas();
				Set<Area> block = new LinkedHashSet<>();

				for (Area a : area) {

					switch (a.getAreaLevel().getAreaLevelId()) {
					case 1:
						areas = mongoAreaRepository.findByAreaLevelAreaLevelId(5);
						break;
					case 2:
						areas = mongoAreaRepository.findByAreaLevelAreaLevelIdAndStateId(5, a.getAreaId());
						break;
					case 3:
						areas = mongoAreaRepository.findByAreaLevelAreaLevelIdAndDistrictId(5, a.getAreaId());
						break;
					case 4:
						areas = mongoAreaRepository.findByParentAreaId(a.getAreaId());
						break;
					case 5:// get facilty associated with the block
						areas = mongoAreaRepository.findByAreaIdOrderByAreaName(a.getAreaId());
						break;
					}
					if(user.getAreaLevel().equals(a.getAreaLevel().getAreaLevelName()))
						block.addAll(areas);
				}
				// convert set to list
				areas = new ArrayList<>(block);
			}
				// @formatter:on

				break;
			}

			if (areas != null) {

				int order = 0;
				for (Area area : areas) {
					Map<String, Object> extraKeyMap = new HashMap<>();
					OptionModel optionModel = new OptionModel();
					optionModel.setKey(area.getAreaId());
					optionModel.setValue(area.getAreaName());
					optionModel.setOrder(order++);
					optionModel.setParentId(area.getParentAreaId());
					optionModel.setLevel(area.getAreaLevel().getAreaLevelId());
					optionModel.setVisible(true);
					extraKeyMap.put("district_id", area.getDistrictId());
					extraKeyMap.put("block_id", area.getBlockId());
					
					
					if( area.getAreaLevel().getAreaLevelId() == 5) {
//						optionModel.setParentId2(area.getFacilityType().getSlugId());
//						System.out.println(area.getAreaName() + "+++" + area.getAreaId() + "+++" + area.getAreaCode());
						if(area.getFormIdTypeDetails().containsKey(question.getFormId().toString())) {
//							optionModel.setParentId2(((TypeDetail) area.getFormIdTypeDetails().get(question.getFormId().toString())).getSlugId() );
							extraKeyMap.put("facility_type_id", ((TypeDetail) area.getFormIdTypeDetails().get(question.getFormId().toString())).getSlugId() );
							
							
						}
						
					}
//						
					optionModel.setExtraKeyMap(extraKeyMap);
					listOfOptions.add(optionModel);
				}
				questionModel.setOptions(listOfOptions);
			}
		
		}
		break;
		case "areaLevel":{
			List<AreaLevel> areaLevels = mongoAreaLevelRepository.findAll();
			areaLevels.removeIf(findFacilityLevel(configurableEnvironment.getProperty("facility.area.level.name"))); // remove
		
			for (AreaLevel areaLvl : areaLevels) {
				OptionModel optionModel = new OptionModel();
				optionModel.setKey(areaLvl.getAreaLevelId());
				optionModel.setValue(areaLvl.getAreaLevelName());
				listOfOptions.add(optionModel);
			}
			questionModel.setOptions(listOfOptions);
		}
		break;
		}

		return listOfOptions;

	}


	public Predicate<AreaLevel> findFacilityLevel(String facility) {
		return f -> f.getAreaLevelName().equalsIgnoreCase(facility);
	}

	
	@Override
	public String getDropDownValueForRawData(String tableName, Integer dropdownId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuestionModel setValueForTextBoxFromExternal(QuestionModel questionModel, Question question,
			Map<String, Object> paramKeyValMap, HttpSession session, Object user) {
		
		String featureName = questionModel.getFeatures();
		UserModel userModel = (UserModel) user;
		if (featureName != null && featureName.contains("fetch_from_external")) {
			for (String feature : featureName.split("@AND")) {
				switch (feature.split(":")[0]) {
				case "fetch_from_external": {
					switch (feature.split(":")[1]) {
					case "supervisor_name":
						questionModel.setValue(userModel.getFirstName()+" "+userModel.getLastName());
						break;
					case "organization":
						questionModel.setValue(userModel.getOrgName());
						break;
					case "designation":
						questionModel.setValue(userModel.getDesgnName());
						break;
					case "level":
						questionModel.setValue(userModel.getAreaLevel());
						break;
					case "N/A":
						questionModel.setValue("N/A");
					}
					
				}
					break;

				}
			}
		}
		
		return questionModel;
	}

}
