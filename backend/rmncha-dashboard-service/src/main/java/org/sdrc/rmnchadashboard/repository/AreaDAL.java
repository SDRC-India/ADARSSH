package org.sdrc.rmnchadashboard.repository;

import java.util.Date;

import org.sdrc.rmnchadashboard.domain.Area;

public interface AreaDAL {

	Area findByCode(String code);

	Iterable<Area> getAreaAfterSlugId(Integer slugidarea);

	Iterable<Area> getAreaAfterTimestamp(Date timestamp);

	Area getAreaBySlugId(Integer slugidarea);

	
}
