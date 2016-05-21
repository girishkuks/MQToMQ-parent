/**
 * 
 */
package com.anz.MQToMQ.compute;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;

import com.anz.MQToMQ.transform.PreTransformBLSample;

import com.anz.common.cache.impl.CacheHandlerFactory;
import com.anz.common.compute.ComputeInfo;
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
		
		// Get Reply To Queue Manager
		MbElement replyToQMgr = root.getFirstElementByPath("/MQMD/ReplyToQMgr");
		logger.info("Original ReplyToQMgr = {}", replyToQMgr.getValueAsString());
				
		// Set value of Correlation ID to the Message ID
		correlId.setValue(msgId.getValue());
		logger.info("New CorrelId = {}", correlId.getValueAsString());
		
		// Store Original Reply To Queue in cache
		CacheHandlerFactory.getInstance().updateCache(CacheHandlerFactory.MessageHeaderCache, correlId.getValueAsString(), replyToQ.getValueAsString());
		logger.info("Orgininal ReplyToQ stored in cache");
		
		// Store Original Reply To Queue Manager in cache
		CacheHandlerFactory.getInstance().updateCache(CacheHandlerFactory.MessageHeaderCache, correlId.getValueAsString().concat("Mgr"), replyToQMgr.getValueAsString());
		logger.info("Orgininal ReplyToQMgr stored in cache");
		
		// Create Local Environment Destination Data Element
		MbElement destinationData = outAssembly.getLocalEnvironment().getRootElement()
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Destination","")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "MQ", "")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "DestinationData", "");
				
		// Create Local Environment Provider Queue element
		MbElement providerQ = destinationData.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "queueName", "");
		
		// Create Local Environment Provider Queue Manager element
		MbElement providerQMgr = destinationData.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "queueManagerName", "");
		
		// Set Provider Queue Name to User Defined Property: PROVIDER_QUEUE
		providerQ.setValue((String) getUserDefinedAttribute("PROVIDER_QUEUE"));
		logger.info("{} = {}", providerQ.getName(), providerQ.getValue());
		
		// Set Provider Queue Manager Name to User Defined Property: PROVIDER_QUEUE_MGR
		providerQMgr.setValue((String) getUserDefinedAttribute("PROVIDER_QUEUE_MGR"));
		logger.info("{} = {}", providerQMgr.getName(), providerQMgr.getValue());
		
		// Set Reply To Queue name to user defined property: REPLY_QUEUE
		replyToQ.setValue((String) getUserDefinedAttribute("REPLY_QUEUE"));
		logger.info("provider {} = {}", replyToQ.getName(), replyToQ.getValue());	
		
		// Set Reply To Queue Manager name to user defined property: REPLY_QUEUE_MGR
		replyToQMgr.setValue((String) getUserDefinedAttribute("REPLY_QUEUE_MGR"));
		logger.info("provider {} = {}", replyToQMgr.getName(), replyToQMgr.getValue());
	}

	@Override
	public TransformType getTransformationType() {
		// TODO Auto-generated method stub
		return TransformType.MQ_MQ;
	}

	@Override
	public void prepareForTransformation(ComputeInfo metadata,
			MbMessageAssembly inAssembly, MbMessageAssembly outAssembly) {
		// TODO Auto-generated method stub
		
	}
}
