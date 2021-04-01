package org.sdrc.rmncha.repositories;

import java.util.Date;
import java.util.List;

import org.sdrc.rmncha.domain.TimePeriod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TimePeriodRepository extends MongoRepository<TimePeriod, String> {

	List<TimePeriod> findByPeriodicity(String i);

//	@Query("{'startDate':{$gte:?0},'endDate':{$lte:?0},'periodicity':?1}")
	@Query("{'startDate':{$lte:?0},'endDate':{$gte:?0},periodicity:?1}")
	TimePeriod getCurrentTimePeriod(Date createdDate, String periodicity);
	
	TimePeriod findByTimePeriodId(Integer timePeriodId);
	
	List<TimePeriod> findTop12ByPeriodicityOrderByTimePeriodIdDesc(String periodicity);
	
	TimePeriod findTop1ByPeriodicityOrderByTimePeriodIdDesc(String periodicity);
	
	List<TimePeriod> findAllByOrderByStartDateDesc();
	
	List<TimePeriod> findTop13ByPeriodicityOrderByStartDateDesc(String periodicity);

}
