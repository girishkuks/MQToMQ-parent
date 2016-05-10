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
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbMessageAssembly;

/**
 * @author sanketsw & psamon
 *
 */
public class RetrieveOriginalHeader extends CommonJavaCompute {
	
	private static final Logger logger = LogManager.getLogger();

	/* (non-Javadoc)
	 * @see com.anz.common.compute.impl.CommonJsonJsonTransformCompute#getTransformer()
	 */

	@Override
	public void execute(MbMessageAssembly inAssembly,
			MbMessageAssembly outAssembly) throws Exception {
		
		//RETRIEVE ORIGINAL REPLY TO QUEUE AND SET OUTPUT QUEUE
		
		logger.info("RetrieveOriginalHeader:execute()");
		
		// Get message root element
		MbElement root = outAssembly.getMessage().getRootElement();
		
		// Get Correlation ID
		MbElement correlId = root.getFirstElementByPath("/MQMD/CorrelId");
		logger.info("{} = {}", correlId.getName(), correlId.getValue());
		
		// Get Reply To Queue
		MbElement replyToQ = root.getFirstElementByPath("/MQMD/ReplyToQ");
		logger.info("provider {} = {}", replyToQ.getName(), replyToQ.getValue());
		
		// Get Reply To Queue Manager
		MbElement replyToQMgr = root.getFirstElementByPath("/MQMD/ReplyToQMgr");
		logger.info("provider {} = {}", replyToQMgr.getName(), replyToQMgr.getValue());	
		
		// Create Local Environment Destination Data Element
		MbElement destinationData = outAssembly.getLocalEnvironment().getRootElement()
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Destination","")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "MQ", "")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "DestinationData", "");
				
		// Create Local Environment Output Queue element
		MbElement outputQ = destinationData.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "queueName", "");
		
		// Create Local Environment Output Queue Manager element
		MbElement outputQMgr = destinationData.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "queueManagerName", "");
		
		// Set Provider Queue Name to User Defined Property: OUTPUT_QUEUE
		outputQ.setValue((String) getUserDefinedAttribute("OUTPUT_QUEUE"));
		logger.info("{} = {}", outputQ.getName(), outputQ.getValue());
		
		// Set Provider Queue Manager Name to User Defined Property: OUTPUT_QUEUE_MGR
		outputQMgr.setValue((String) getUserDefinedAttribute("OUTPUT_QUEUE_MGR"));
		logger.info("{} = {}", outputQMgr.getName(), outputQMgr.getValue());
		
		// Retrieve Original Reply To Queue from cache
		String originalReplyToQ = CacheHandlerFactory.getInstance().lookupCache("MQHeaderCache", correlId.getValueAsString());
		
		// If Original Reply To Queue found in cache, set as Reply To Queue
		if(originalReplyToQ != null){
			logger.info("Original Reply To Queue retrieved from cache");
			replyToQ.setValue(originalReplyToQ);
			logger.info("Original Reply To Queue = {}", replyToQ.getValueAsString());
		} else {
			//TODO: Error statements
			logger.info("ERROR: original Reply To Q not found in cache");
		}
		
		// Retrieve Original Reply To Queue Manager from cache
		String originalReplyToQMgr = CacheHandlerFactory.getInstance().lookupCache("MQHeaderCache", correlId.getValueAsString().concat("Mgr"));
		
		// If Original Reply To Queue found in cache, set as Reply To Queue
		if(originalReplyToQMgr != null){
			logger.info("Original Reply To Queue Manager retrieved from cache");
			replyToQMgr.setValue(originalReplyToQMgr);
			logger.info("Original Reply To Queue Manager = {}", replyToQMgr.getValueAsString());
		} else {
			//TODO: Error statements
			logger.info("ERROR: original Reply To Q Mgr not found in cache");
		}
		
		
	}

	@Override
	public TransformType getTransformationType() {
		// TODO Auto-generated method stub
		return TransformType.MQ_MQ;
	}
}
