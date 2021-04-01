package org.sdrc.rmncha.rabbitMQ;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * This service creates a rabbitmq channel to publish new data entry and approval/rejection message to the dashboard service 
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 19-Jul-2019 3:03:50 PM
 */
public interface CollectionChannel {
	
	/**
	 * publishes New Entry
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 19-Jul-2019 3:04:21 PM
	 * @return
	 */
	
	String RMNCHADATAENTRY_OUTPUTCHANNEL="rmnchadataentrychannel-out";
	@Output(CollectionChannel.RMNCHADATAENTRY_OUTPUTCHANNEL)
	MessageChannel dataSubmissionChannel();
	
	/**
	 * publishes submission management action 
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 19-Jul-2019 3:04:23 PM
	 * @return
	 */
	
	String RMNCHASUBMISSION_OUTPUTCHANNEL = "rmnchasubmissionchannel-out";
	@Output(CollectionChannel.RMNCHASUBMISSION_OUTPUTCHANNEL)
	MessageChannel approveRejectChannel();
	

	/**
	 * publishes rejection email
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 19-Jul-2019 3:04:23 PM
	 * @return
	 */
	String RMNCHAEMAIL_OUTPUTCHANNEL = "rmnchaemailchannel-out";
	@Output(CollectionChannel.RMNCHAEMAIL_OUTPUTCHANNEL)
	MessageChannel sendEmailChannel();
	
	/**
	 * consumes rejection email 
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 19-Jul-2019 3:14:42 PM
	 * @return
	 */
	String RMNCHAEMAIL_INPUTCHANNEL = "rmnchaemailchannel-in";
	@Input(CollectionChannel.RMNCHAEMAIL_INPUTCHANNEL)
	SubscribableChannel emailInputChannel();
	
	/**
	 * publishes submission email
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 25-Jul-2019 3:04:23 PM
	 * @return
	 */
	String RMNCHASUBMITEMAIL_OUTPUTCHANNEL = "rmnchasubmitemailchannel-out";
	@Output(CollectionChannel.RMNCHASUBMITEMAIL_OUTPUTCHANNEL)
	MessageChannel sendSubmitEmailChannel();
	
	/**
	 * consumes submission email 
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 25-Jul-2019 3:14:42 PM
	 * @return
	 */
	String RMNCHASUBMITEMAIL_INPUTCHANNEL = "rmnchasubmitemailchannel-in";
	@Input(CollectionChannel.RMNCHASUBMITEMAIL_INPUTCHANNEL)
	SubscribableChannel submitEmailInputChannel();
	
	/**
	 * publishes time period in job call
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 25-Jul-2019 2:22:53 PM
	 * @return
	 */
	String RMNCHATIMEPERIOD_OUTPUTCHANNEL="rmnchatimeperiodchannel-out";
	@Output(CollectionChannel.RMNCHATIMEPERIOD_OUTPUTCHANNEL)
	MessageChannel timeperiodChannel();
	
	
	/**publishes submission's default status-- channel
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 29-Jul-2019 8:10:58 PM
	 * @return
	 */
	/*String RMNCHADEFAULT_OUTPUTCHANNEL = "rmnchadefaultchannel-out";
	@Output(CollectionChannel.RMNCHADEFAULT_OUTPUTCHANNEL)
	MessageChannel defaultChannel();*/
	
	/**publishes submission attachment count-- channel
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 29-Jul-2019 8:10:58 PM
	 * @return
	 */
	String RMNCHADEFAULT_ATTACHMENTCHANNEL = "rmnchaattachmentchannel-out";
	@Output(CollectionChannel.RMNCHADEFAULT_ATTACHMENTCHANNEL)
	MessageChannel attachmentChannel();
	
	/**publishes duplicate status-- channel
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 02-Aug-2019 8:10:58 PM
	 * @return
	 */
	String RMNCHADEFAULT_DUPLICATECHANNEL = "rmnchaduplicatechannel-out";
	@Output(CollectionChannel.RMNCHADEFAULT_DUPLICATECHANNEL)
	MessageChannel duplicateChannel();
}
