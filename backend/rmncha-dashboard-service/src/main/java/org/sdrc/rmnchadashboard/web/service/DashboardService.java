package org.sdrc.rmnchadashboard.web.service;

import java.util.List;
import java.util.Map;

import org.sdrc.rmnchadashboard.web.model.AreaLevelModel;
import org.sdrc.rmnchadashboard.web.model.AreaModel;
import org.sdrc.rmnchadashboard.web.model.FormSectorModel;
import org.sdrc.rmnchadashboard.web.model.SectorModel;
import org.sdrc.rmnchadashboard.web.model.TimeperiodModel;
import org.sdrc.rmnchadashboard.web.model.TypeDetailModel;
import org.springframework.http.ResponseEntity;

/**
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 19-Jul-2019 3:38:37 PM
 */
public interface DashboardService {
	
	
	public String pushIndicatorGroupData();
	
	Map<String, List<FormSectorModel>> getAllChecklistSectors();

	List<SectorModel> getDashboardData(Integer areaLevel, Integer areaId, Integer sectorId, Integer tpId,
			Integer formId, String dashboardType, Integer typeId ,Integer levelId);

	/**
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 29-Jul-2019 6:43:43 PM
	 * @return
	 */
	ResponseEntity<List<AreaLevelModel>> getAreaLevels();

	/**
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 29-Jul-2019 6:44:25 PM
	 * @param typeName
	 * @return
	 */
	ResponseEntity<List<TypeDetailModel>> getTypeDetailsByType(String typeName);

	/**
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 29-Jul-2019 6:46:29 PM
	 * @return
	 */
	Map<String, List<AreaModel>> getAllAreaList();

	/**
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 31-Jul-2019 2:18:09 PM
	 * @return
	 */
	String aggregate();

	ResponseEntity<List<TimeperiodModel>> getAllTimePeriod(Boolean isAggregate);

	ResponseEntity<List<TypeDetailModel>> getTypeDetailsByFormAndType(Integer formId, String typeName);

	String aggregateLegacyData(Integer tp,  String tpName,String periodicity);

	List<Map<String, String>> getLegacyData();

	boolean isDataAvailable();

	
//	Map<String, String> getAllTimeperiodAndFinancialYrDetails();

}
