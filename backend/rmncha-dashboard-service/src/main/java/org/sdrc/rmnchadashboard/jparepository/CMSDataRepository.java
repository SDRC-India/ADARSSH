package org.sdrc.rmnchadashboard.jparepository;

import java.util.List;

import org.sdrc.rmnchadashboard.jpadomain.CmsData;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.transaction.annotation.Transactional;

@RepositoryDefinition(domainClass = CmsData.class, idClass = Integer.class)
public interface CMSDataRepository {

	List<CmsData> findByIsLiveTrueOrderByCmsOrderAsc();
	
	CmsData findById(long id);

	@Transactional
	CmsData save(CmsData cmsData);

	List<CmsData> findByMainMenuOrderByCmsOrderAsc(String mainMenu);
	
	CmsData findBySubMenuOrderByCmsOrderAsc(String subMenu);

	CmsData findBySubMenuAndMainMenuOrderByCmsOrderAsc(String subMenu, String mainMenu);
}