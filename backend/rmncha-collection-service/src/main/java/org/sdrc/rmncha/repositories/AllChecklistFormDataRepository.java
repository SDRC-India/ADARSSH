package org.sdrc.rmncha.repositories;

import java.util.Date;
import java.util.List;

import org.sdrc.rmncha.domain.AllChecklistFormData;
import org.sdrc.rmncha.model.ChecklistSubmissionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author subham
 *
 */
public interface AllChecklistFormDataRepository extends MongoRepository<AllChecklistFormData, String> {

	@Transactional
	AllChecklistFormData findById(String submissionId);
	
	@Query(value = "{ $and: [ { 'formId' : ?0}, {'data.f1_facility_name' : ?1 }]}")
	List<AllChecklistFormData> findByFormIdAndDataColumnId(Integer formId, Integer qColumnId);

	List<AllChecklistFormData> findByFormId(Integer formId);

	List<AllChecklistFormData> findByFormIdAndCreatedDateBetween(int formId, Date sDate, Date eDate);

	@Query(value = "{ $and: [ { 'formId' : ?0}, {'data.f2qblock' :{$in:?1 }},{'data.f2qdateofvisit':{ $gte:?2}},{'data.f2qdateofvisit':{$lte: ?3 }}]}")
	List<AllChecklistFormData> findByFormIdAndBlockId(Integer formId, List<Integer> list,Date sdate,Date eDate);

	@Query(value = "{ $and: [ { 'formId' : ?0}, {'data.f3q6_district' :{$in:?1 }},{'data.f3q7_dov':{ $gte:?2}},{'data.f3q7_dov':{$lte: ?3 }}]}")
	List<AllChecklistFormData> findByFormIdAndDistrictId(Integer formId, List<Integer> asList,Date sdate,Date eDate);

	@Query(value = "{ $and: [ { 'formId' : ?0}, {'data.f1_facility_name' :{$in:?1 }},{'data.f1FacilityType':?2},{'data.f1q_date_of_visit':{ $gte:?3}},{'data.f1q_date_of_visit':{$lte: ?4 }}]}")
	List<AllChecklistFormData> findByFormIdAndFacilityId(Integer formId, List<Integer> asList, Integer facilityTypeId,Date sdate,Date eDate);

	@Query(value = "{ $and: [ { 'formId' : ?0}, {'data.f1_facility_name' :{$in:?1 }},{'data.f1q_date_of_visit':{ $gte:?2}},{'data.f1q_date_of_visit':{$lte: ?3 }}]}")
	List<AllChecklistFormData> findByFormIdAndAreaId(Integer formId, List<Integer> asList,Date sdate,Date eDate);
	
	@Query(value = "{ $and: [ { 'formId' : ?0}, {'data.hwc8_facility_name' :{$in:?1 }},{'data.hwc9Facilitytype':?2},{'data.hwc10_dateOfVisit':{ $gte:?3}},{'data.hwc10_dateOfVisit':{$lte: ?4 }}]}")
	List<AllChecklistFormData> findByFormIdAndFacilityIdFacilityType(Integer formId, List<Integer> areaIds,
			Integer facilityTypeId,Date sdate,Date eDate);
	
	@Query(value = "{ $and: [ { 'formId' : ?0}, {'data.hwc8_facility_name' :{$in:?1 }},{'data.hwc10_dateOfVisit':{ $gte:?2}},{'data.hwc10_dateOfVisit':{$lte: ?3 }}]}")
	List<AllChecklistFormData> findByFormIdAndFacility(Integer formId, List<Integer> areaIds, Date sdate,Date eDate);
	
//	@Query(value = "{ $and: [ { 'formId' : ?0, 'data.f1_facility_name' : ?1 }]}")
//	List<AllChecklistFormData> findByFormIdAndTimperiodAndFacilityColumnId(Integer formId, String qColumnId);
//	
//	
//	@Query(value = "{ $and: [ { 'formId' : ?0, 'data.f1_facility_name' : ?1 }]}")
//	List<AllChecklistFormData> findByFormIdAndTimperiodAndCommunityColumnId(Integer formId, String qColumnId);
//	
//	
//	@Query(value = "{ $and: [ { 'formId' : ?0, 'data.f1_facility_name' : ?1 }]}")
//	List<AllChecklistFormData> findByFormIdAndTimperiodAndHWCColumnId(Integer formId, String qColumnId);
//	
//	
//	@Query(value = "{ $and: [ { 'formId' : ?0, 'data.f1_facility_name' : ?1 }]}")
//	List<AllChecklistFormData> findByFormIdAndTimperiodAndDistrictColumnId(Integer formId, String qColumnId);
	
	List<AllChecklistFormData> findByIdIn(List<String> rejectionList);

	AllChecklistFormData findByIdAndFormId(String submissionId, Integer formId);

	List<AllChecklistFormData> findByChecklistSubmissionStatus(
			ChecklistSubmissionStatus pending);

	List<AllChecklistFormData> findByChecklistSubmissionStatusAndTimePeriodTimePeriodId(ChecklistSubmissionStatus pending,
			Integer timePeriodId);
	
	AllChecklistFormData findByFormIdAndAreaAreaIdAndTimePeriodTimePeriodIdAndLatestTrue(Integer formId, Integer areaId, Integer timePeriodId);
	
	List<AllChecklistFormData> findByFormIdAndUniqueIdAndDuplicateFalse(Integer formId, String uniqueId);
}
