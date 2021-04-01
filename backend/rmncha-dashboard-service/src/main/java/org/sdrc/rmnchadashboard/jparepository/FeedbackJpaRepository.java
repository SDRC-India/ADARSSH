/**
 * 
 */
package org.sdrc.rmnchadashboard.jparepository;

import org.sdrc.rmnchadashboard.jpadomain.Feedback;
import org.springframework.stereotype.Repository;

/**
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 *
 */
@Repository
public interface FeedbackJpaRepository extends  org.springframework.data.repository.Repository<Feedback,Integer>{
	
	public Feedback save(Feedback feedback);

}
