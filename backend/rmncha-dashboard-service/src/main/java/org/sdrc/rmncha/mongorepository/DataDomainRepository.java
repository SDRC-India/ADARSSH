package org.sdrc.rmncha.mongorepository;

import java.util.List;

import org.sdrc.rmncha.mongodomain.DataValue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataDomainRepository extends MongoRepository<DataValue, String> {

	List<DataValue> findByAreaIdAndTpIn(Integer areaId, List<Integer> asList);

	List<DataValue> findByAreaIdAndInidAndTpIn(Integer areaId, Integer indicatorId, List<Integer> asList);
	
	List<DataValue> findByAreaIdAndTpAndInidIn(Integer areaId, Integer tp, List<Integer> indicatorIds);

	List<DataValue> findTop4ByAreaIdAndInidOrderByTpDesc(Integer areaId, Integer indicatorId);

	List<DataValue> findTop12ByAreaIdAndInidInOrderByTpDesc(Integer areaId, List<Integer> indicatorIds);
	
	List<DataValue> findByAreaIdAndTpAndInidInAndF1FacilityTypeIsNullAndF1FacilityLevelIsNull(Integer areaId, Integer tp, List<Integer> indicatorId);
	
	List<DataValue> findByAreaIdAndTpAndF1FacilityTypeAndF1FacilityLevelAndInidIn(Integer areaId, Integer tp, Integer typeId, Integer levelId,
			List<Integer> indicatorId);
	
	List<DataValue> findByAreaIdAndTpAndF1FacilityTypeAndInidInAndF1FacilityLevelIsNull(Integer areaId, Integer tp, Integer typeId,
			List<Integer> indicatorId);
	
	List<DataValue> findByAreaIdAndTpAndF1FacilityLevelAndInidInAndF1FacilityTypeIsNull(Integer areaId, Integer tp, Integer levelId,
			List<Integer> indicatorId);
	
	List<DataValue> findByTp(Integer tp);
	
	List<DataValue> findByAreaIdAndTpAndInidInAndF1FacilityTypeIsNull(Integer areaId, Integer tp, List<Integer> indicatorId);
}
