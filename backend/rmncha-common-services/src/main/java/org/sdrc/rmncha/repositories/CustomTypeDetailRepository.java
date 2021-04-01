package org.sdrc.rmncha.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.co.sdrc.sdrcdatacollector.document.TypeDetail;


@Repository
public interface CustomTypeDetailRepository extends MongoRepository<TypeDetail, String>{

	TypeDetail findById(String devPartner);

	List<TypeDetail> findByNameInAndTypeTypeName(List<String> names, String typeName);
	
	TypeDetail findByFormIdAndSlugId(Integer formId, Integer slugId);
	
	List<TypeDetail> findByFormIdAndTypeTypeName(Integer formId, String typeName);
	
	List<TypeDetail> findByTypeTypeName(String typeName);
	
}
