package org.sdrc.rmnchadashboard.jparepository;

import java.util.Date;
import java.util.List;

import org.sdrc.rmnchadashboard.jpadomain.Area;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaJpaRepository extends  org.springframework.data.repository.Repository<Area,Long>{

	public Area save(Area area);

//	public Area findByCode(String areaCode);
	
	public List<Area> findAll();

	public Area findByCode(String areaCode);

	public List<Area> findAllByLastModifiedGreaterThanOrderBySlugidarea(Date lastSynchronizedDate);

	public List<Area> findAllByOrderByActAreaLevelIdAsc();

	@Query(value="select a1.id,a1.code, a1.areaname,a2.areaname as \"parent_area_name\",al.area_level_name from area a1 Left JOIN "
			+ "area a2 ON a1.parent_area_code = a2.code INNER JOIN "
			+ "arealevel al ON a1.act_area_level_id_fk =al.id", nativeQuery=true)
	public List<Object[]> getAllAreaNames();

}