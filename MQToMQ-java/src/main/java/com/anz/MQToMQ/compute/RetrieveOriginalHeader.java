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
		
		// Create Local Environment Output Queue element
		MbElement outputQ = outAssembly.getLocalEnvironment().getRootElement()
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Destination","")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "MQ", "")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "DestinationData", "")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "queueName", "");
		
		// Set Output Queue
		outputQ.setValue((String) getUserDefinedAttribute("OUTPUT_QUEUE"));
		logger.info("output {} = {}", outputQ.getName(), outputQ.getValue());
		
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
	}

	@Override
	public TransformType getTransformationType() {
		// TODO Auto-generated method stub
		return TransformType.MQ_MQ;
	}
}
