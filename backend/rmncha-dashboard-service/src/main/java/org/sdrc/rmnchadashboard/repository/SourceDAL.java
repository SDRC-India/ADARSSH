package org.sdrc.rmnchadashboard.repository;

import java.util.Date;

import org.sdrc.rmnchadashboard.domain.Source;

public interface SourceDAL {

	Source findBySourceName(String source);

	Iterable<Source> getSourceAfterSlugId(Integer slugidsource);

	Iterable<Source> getSourceAfterTimestamp(Date timestamp);

	Source getSourceBySlugId(Integer slugidsource);
}
