package org.sdrc.rmnchadashboard.service;

import java.util.Date;

public interface SqlConfigurationService {

	// Methods Related to SQL Db
	boolean configureSQLDb();

	boolean importAreaIntoSQL(Date currentDate);

	Boolean calculateRankForSQLDb();

	Boolean calculateTopButtomForSQLDb();

	Boolean calculateTrendForSQLDb();

	public Boolean calculateRankTrendTopButtomSQLDb();
}
