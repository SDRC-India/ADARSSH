package org.sdrc.rmnchadashboard.repository;

import java.util.Date;

import org.sdrc.rmnchadashboard.domain.AreaLevel;

public interface AreaLevelDAL {
	Iterable<AreaLevel> getAreaLevelAfterSlugId(int slugidarealevel);

	Iterable<AreaLevel> getAreaLevelAfterTimestamp(Date timestamp);

	AreaLevel findBySlugId(int i);

	AreaLevel getAreaBySlugId(Integer slugidarealevel);
}
