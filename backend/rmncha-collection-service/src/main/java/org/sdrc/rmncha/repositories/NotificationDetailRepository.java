package org.sdrc.rmncha.repositories;

import org.sdrc.rmncha.domain.NotificationDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationDetailRepository extends MongoRepository<NotificationDetail, String> {

	NotificationDetail findByFormId(Integer formId);

}
	