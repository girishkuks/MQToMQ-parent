/**
 * 
 */
package com.anz.MQToMQ.compute;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;

import com.anz.MQToMQ.transform.PreTransformBLSample;

import com.anz.common.cache.impl.CacheHandlerFactory;
import com.anz.common.compute.TransformType;
import com.anz.common.compute.impl.CommonJavaCompute;
import com.anz.common.transform.ITransformer;
import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ExecutionGroupProxy;
import com.ibm.broker.config.proxy.MessageFlowProxy;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.config.proxy.AttributeConstants;

/**
 * @author sanketsw & psamon
 *
 */
public class SaveModifyHeader extends CommonJavaCompute {
	
	private static final Logger logger = LogManager.getLogger();

	/* (non-Javadoc)
	 * @see com.anz.common.compute.impl.CommonJsonJsonTransformCompute#getTransformer()
	 */
	@Override
	public void execute(MbMessageAssembly inAssembly,
			MbMessageAssembly outAssembly) throws Exception {
		
		logger.info("SaveModifyHeader:execute()");
		
		// Get message root element
		MbElement root = outAssembly.getMessage().getRootElement();
		
		// Get Message ID
		MbElement msgId = root.getFirstElementByPath("/MQMD/MsgId");
		logger.info("{} = {}", msgId.getName(), msgId.getValueAsString());
		
		// Get Correlation ID
		MbElement correlId = root.getFirstElementByPath("/MQMD/CorrelId");
		logger.info("{} = {}", correlId.getName(), correlId.getValueAsString());
		
		// Get Reply To Queue 
		MbElement replyToQ = root.getFirstElementByPath("/MQMD/ReplyToQ");
		logger.info("Original ReplyToQ = {}", replyToQ.getValueAsString());
				
		// Set value of Correlation ID to the Message ID
		correlId.setValue(msgId.getValue());
		logger.info("New CorrelId = {}", correlId.getValueAsString());
		
		// Store Original Reply To Queue in cache
		CacheHandlerFactory.getInstance().updateCache("MqHeaderCache", correlId.getValueAsString(), replyToQ.getValueAsString());
		logger.info("Orgininal ReplyToQ stored in cache");

		// Create Local Environment Provider Queue element
		MbElement providerQ = outAssembly.getLocalEnvironment().getRootElement()
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Destination","")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "MQ", "")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "DestinationData", "")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "queueName", "");
		
		// Set Provider Queue Name to User Defined Property: providerQueue
		providerQ.setValue((String) getUserDefinedAttribute("providerQueue"));
		logger.info("{} = {}", providerQ.getName(), providerQ.getValue());
		
		// Set Reply To Queue name to user defined property: responseQueue
		replyToQ.setValue((String) getUserDefinedAttribute("responseQueue"));
		logger.info("provider {} = {}", replyToQ.getName(), replyToQ.getValue());		
	}

	@Override
	public TransformType getTransformationType() {
		// TODO Auto-generated method stub
		return TransformType.MQ_MQ;
	}
}
