package org.sdrc.rmnchadashboard.jparepository;

import java.util.Date;
import java.util.List;

import org.sdrc.rmnchadashboard.jpadomain.Unit;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitJpaRepository extends org.springframework.data.repository.Repository<Unit, Long> {

	public Unit save(Unit unit);

	public Unit findByUnitName(String unitC);

	public List<Unit> findAll();

	public List<Unit> findAllByLastModifiedGreaterThanOrderBySlugidunit(Date date);
}
