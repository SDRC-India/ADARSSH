package org.sdrc.rmnchadashboard.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sdrc.rmnchadashboard.domain.Data;
import org.sdrc.rmnchadashboard.domain.Indicator;
import org.sdrc.rmnchadashboard.model.MasterDataModel;
import org.sdrc.rmnchadashboard.model.MasterDataSyncModel;

public interface ConfigurationService {

	public boolean configureApplication();

	boolean importArea(Date currentDate);

	boolean importData(Date currentDate);

	public Boolean calculateTopButtom();

	public Map<String, List<Data>> calculateRank();

	public Indicator updateIndicator();

	public boolean calculateTrend();

	//Methods Related to SQL Db
//	boolean configureSQLDb();
//
//	boolean importAreaIntoSQL(Date currentDate);
//
//	Boolean calculateRankForSQLDb();
//
//	Boolean calculateTopButtomForSQLDb();
//
//	Boolean calculateTrendForSQLDb();
//
//	public Boolean calculateRankTrendTopButtomSQLDb();
	
	public String createJSON() throws Exception;

	public MasterDataModel getMasterData(MasterDataSyncModel masterDataSyncModel);
}
