package org.sdrc.rmnchadashboard.jparepository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.sdrc.rmnchadashboard.jpadomain.Data;
import org.sdrc.rmnchadashboard.jpadomain.Indicator;
import org.sdrc.rmnchadashboard.jpadomain.Source;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DataJpaRepository extends org.springframework.data.repository.Repository<Data, Long> {

	<S extends Data> List<S> save(Iterable<S> datas);

	public Data save(Data data);

//	@Cacheable("datas")
	public List<Data> findAll();

	public List<Data> findByIndicatorAndAreaParentAreaCodeAndSrcAndTp(Indicator indicator, String code, Source src,
			String tp);

	Iterable<Data> findByIndicator(Indicator indicator);

	List<org.sdrc.rmnchadashboard.jpadomain.Data> findAllByLastModifiedGreaterThan(Date date);

	List<Data> findAllByLastModifiedGreaterThanOrderBySlugiddata(Date lastSynchronizedDate);
	
	@Query("SELECT MAX(dt.slugiddata) FROM Data dt ")
	long findMaxSlugId();

	List<Data> findAllByIndicatorKpiTrueOrIndicatorNitiaayogTrueOrIndicatorHmisTrueOrIndicatorSsvTrueOrIndicatorThematicKpiTrue();

	Iterable<Data> findByIndicatorAndTpIn(Indicator indicator, Set<String> timeperiod);

	List<Data> findByIndicatorInAndTpIn(List<Indicator> updatedIndicator, Set<String> values);

	List<Data> findByIndicatorIn(List<Indicator> updatedIndicator);

	List<Data> findByIndicatorInOrderBySlugiddataAsc(List<Indicator> updatedIndicator);

	List<Data> findByOrderBySlugiddataAsc();
	
	
	@Query(value="SELECT dt.id, dt.created_date, dkpirsrs, dnitirsrs, dthematicrsrs, ius, dt.last_modified, \r\n" + 
			"       periodicity, rank, slugiddata, tp, tps, trend, value, ar.code, \r\n" + 
			"       ind.slugidindicator, src.slugidsource, sub.slugidsubgroup , cast (array_to_json(array_agg(topArea.code))as varchar) as top,cast (array_to_json(\r\n" + 
			"       array_agg(btmArea.code))as varchar) as bottom\r\n" + 
			"  FROM public.data dt inner join area ar  on dt.area_id_fk = ar.id inner join indicator ind on ind.id= dt.indicator_id_fk \r\n" + 
			"  inner join source src on src.id=dt.source_id_fk inner join subgroup sub on sub.id =dt.subgroup_id_fk \r\n" + 
			"  left join data_top_mapping dtp on dtp.data_id_fk = dt.id \r\n" + 
			" left join data_below_mapping dtb on dtb.data_id_fk = dt.id \r\n" + 
			" left join area topArea on topArea.id=dtp.area_id_fk\r\n" + 
			"  left join area btmArea on btmArea.id=dtb.area_id_fk\r\n" + 
			"  where ind.kpi = true OR ind.nitiaayog = true OR  ind.thematic_kpi=true OR ind.ssv = true OR ind.hmis = true\r\n" + 
			" group by \r\n" + 
			"dt.id, dt.created_date, dkpirsrs, dnitirsrs, dthematicrsrs, ius, dt.last_modified, \r\n" + 
			"       periodicity, rank, slugiddata, tp, tps, trend, value, ar.code, \r\n" + 
			"       ind.slugidindicator, src.slugidsource, sub.slugidsubgroup ",nativeQuery=true)
	List<Object []> findJsonData();

	List<Data> findByAreaCodeInAndIndicatorSlugidindicatorIn(List<String> areaCodes, List<Integer> indicatorSlugIds,
			Pageable pageable);

}
