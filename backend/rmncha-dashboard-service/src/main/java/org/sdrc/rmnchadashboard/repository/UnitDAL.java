package org.sdrc.rmnchadashboard.repository;

import java.util.Date;

import org.sdrc.rmnchadashboard.domain.Unit;

public interface UnitDAL {

	Unit findByUnitName(String name);

	Iterable<Unit> getUnitAfterSlugId(Integer slugidunit);

	Iterable<Unit> getUnitAfterTimestamp(Date timestamp);

	Unit getUnitBySlugId(Integer slugidunit);

}
