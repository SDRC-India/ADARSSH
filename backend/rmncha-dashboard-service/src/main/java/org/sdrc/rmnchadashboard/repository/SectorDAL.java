package org.sdrc.rmnchadashboard.repository;

import java.util.Date;

import org.sdrc.rmnchadashboard.domain.Sector;

public interface SectorDAL {
	
	Sector findBySectorName(String name);
	
	Iterable<Sector> getSectorAfterSlugId(Integer slugidsector);

	Iterable<Sector> getSectorAfterTimestamp(Date timestamp);

	Sector getSectorBySlugId(Integer id);
}
