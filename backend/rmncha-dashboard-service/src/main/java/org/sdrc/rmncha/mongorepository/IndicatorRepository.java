package org.sdrc.rmncha.mongorepository;

import java.util.List;

import org.sdrc.rmncha.mongodomain.Indicator;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("mongoIndicatorRepository")
public interface IndicatorRepository extends MongoRepository<Indicator, String> {
	@Query("{'indicatorDataMap.periodicity':?0}")
	List<Indicator> getIndicatorByPeriodicity(String periodcity);

	@Query("{'indicatorDataMap.periodicity':?0, 'indicatorDataMap.parentType':?1}")
	List<Indicator> getPercentageIndicators(String periodicity, String indicatorType);
	
	@Query("{'indicatorDataMap.sectorId' :{$in:?0 }}")
	List<Indicator> getIndicatorBySectorIds(List<String> sectorIds);
	

	@Query("{'indicatorDataMap.sectorId' :{$in:?0 }, 'indicatorDataMap.parentType':?1}")
	List<Indicator> getPercentageSectorWiseIndicators(List<String> sectorIds, String indicatorType);
	
	@Query("{'indicatorDataMap.formId':?0}")
	List<Indicator> getIndicatorByFormId(String formId);
}
