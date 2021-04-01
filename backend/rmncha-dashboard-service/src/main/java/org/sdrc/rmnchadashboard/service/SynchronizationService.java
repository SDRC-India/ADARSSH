package org.sdrc.rmnchadashboard.service;

import java.util.Date;

public interface SynchronizationService {

	public boolean startSynchronizationOfUnit(Date date);
	
	public boolean startSynchronizationOfIndicator(Date date);

	public boolean startSynchronizationOfSubgroup(Date date);

	public boolean startSynchronizationOfArea(Date date);

	public boolean startSynchronizationOfAreaLevel(Date date);
	
	public boolean startSynchronizationOfSector(Date date);

	public boolean startSynchronizationOfSource(Date date);

	public boolean startSynchronizationOfData(Date date);
	
	
	public boolean startSyncProcess();
	
}
