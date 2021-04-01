package org.sdrc.rmnchadashboard.repository;

import java.util.Date;
import java.util.List;

import org.sdrc.rmnchadashboard.domain.Indicator;

public interface IndicatorDAL {

	public List<Indicator> findAll();
	
	public Indicator findByName(String name);

	Iterable<Indicator> getIndicatorsAfterSlugId(Integer slugidindicator);

	public Iterable<Indicator> getIndicatorsAfterTimestamp(Date timestamp);

	public Indicator getIndicatorBySlugId(Integer slugidindicator);
}
