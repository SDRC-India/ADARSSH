package org.sdrc.rmnchadashboard.rabbitMQ;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 *  This service has two queues for new data entry and approval/rejection message
 * @author Sarita Panigrahi
 * email-sari.panigrahi@gmail.com
 * 19-Jul-2019 3:11:36 PM
 */
public interface CollectionQueryChannel {

	/**
	 * consumes New Entry
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 19-Jul-2019 3:14:39 PM
	 * @return
	 */
	String RMNCHADATAENTRY_INPUTCHANNEL = "rmnchadataentrychannel-in";
	@Input(CollectionQueryChannel.RMNCHADATAENTRY_INPUTCHANNEL)
	SubscribableChannel inputChannel();

	/**
	 * consumes submission management action 
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 19-Jul-2019 3:14:42 PM
	 * @return
	 */
	String RMNCHASUBMISSION_INPUTCHANNEL = "rmnchasubmissionchannel-in";
	@Input(CollectionQueryChannel.RMNCHASUBMISSION_INPUTCHANNEL)
	SubscribableChannel submissionInputChannel();
	
	/**
	 * consumes timperiod when created by cron job
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 25-Jul-2019 2:43:44 PM
	 * @return
	 */
	String RMNCHATIMEPERIOD_INPUTCHANNEL = "rmnchatimeperiodchannel-in";
	@Input(CollectionQueryChannel.RMNCHATIMEPERIOD_INPUTCHANNEL)
	SubscribableChannel timeperiodInputChannel();

	/**
	 * consumes submission's default status when updated by cron job
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 25-Jul-2019 2:43:44 PM
	 * @return
	 */
	
//	String RMNCHADEFAULT_INPUTCHANNEL = "rmnchadefaultchannel-in";
//	@Input(CollectionQueryChannel.RMNCHADEFAULT_INPUTCHANNEL)
//	SubscribableChannel defaultInputChannel();
	
	/**
	 * consumes submission's attachments
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 25-Jul-2019 2:43:44 PM
	 * @return
	 */
	
	String RMNCHAATTACHMENT_INPUTCHANNEL = "rmnchaattachmentchannel-in";
	@Input(CollectionQueryChannel.RMNCHAATTACHMENT_INPUTCHANNEL)
	SubscribableChannel attachmentInputChannel();
	
	/**
	 * consumes submission's duplicate status
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 25-Jul-2019 2:43:44 PM
	 * @return
	 */
	
	String RMNCHADUPLICATE_INPUTCHANNEL = "rmnchaduplicatechannel-in";
	@Input(CollectionQueryChannel.RMNCHADUPLICATE_INPUTCHANNEL)
	SubscribableChannel duplicateInputChannel();
	
	/**
	 * produces aggregation channel
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 20-Sep-2019 3:15:42 PM
	 * @return
	 */
	String RMNCHAAGGREGATE_OUTPUTCHANNEL = "rmnchaaggregatechannel-out";
	@Output(CollectionQueryChannel.RMNCHAAGGREGATE_OUTPUTCHANNEL)
	MessageChannel aggregateOutChannel();
	
	/**
	 * consumes aggregation channel
	 * @author Sarita Panigrahi
	 * email-sari.panigrahi@gmail.com
	 * 20-Sep-2019 3:14:42 PM
	 * @return
	 */
	String RMNCHAAGGREGATE_INPUTCHANNEL = "rmnchaaggregatechannel-in";
	@Input(CollectionQueryChannel.RMNCHAAGGREGATE_INPUTCHANNEL)
	SubscribableChannel aggregateInputChannel();
}
