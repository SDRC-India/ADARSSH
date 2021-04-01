package org.sdrc.rmnchadashboard.repository;

import java.util.Date;
import java.util.List;

import org.sdrc.rmnchadashboard.domain.Data;
import org.sdrc.rmnchadashboard.domain.Indicator;
import org.springframework.data.domain.Pageable;

public interface DataDAL {

	public List<Data> findAllData();

	public List<Data> findByParentAreaCodeAndSourceAndTimePeriod(String code, String src, String tp);

	public List<Data> findByIndicatorAndParentAreaCodeAndSourceAndTimePeriod(String indicator, String parentAreaCode, String src,
			String tp);
	
	public Iterable<Data> getInitalDataForIndicator(String iName);
	
	public Iterable<Data> getDataAfterSlugId(Long slugiddata);

	public Iterable<Data> getDataAfterTimestamp(Date timestamp);
	
	public List<Data> findByIndicator(Indicator indicator);

	public Data getDataBySlugId(Long slugiddata);

	public Iterable<Data> getDataAfterTimestamp(List<Long> indicatorSlugIds, List<String> areaCodes, Integer searchType,Pageable request);

}
