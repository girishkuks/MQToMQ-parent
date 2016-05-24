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
import com.anz.common.compute.impl.ComputeUtils;
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
		
		// Retrieve Original Reply To Queue from cache
		String originalReplyToQ = CacheHandlerFactory.getInstance().lookupCache(CacheHandlerFactory.MessageHeaderCache, correlId.getValueAsString());	
		
		// If Original Reply To Queue found in cache, set as Reply To Queue
		if(originalReplyToQ != null){
			
			replyToQ.setValue(originalReplyToQ);
			logger.info("Original ReplyToQ found in cache: Reply To Queue = {}", replyToQ.getValueAsString());
			
		} else {
			
			replyToQ.setValue(getUserDefinedAttribute("OUTPUT_QUEUE"));
			logger.warn("ERROR: original Reply To Q not found in cache. Output queue set to default OUTPUT_QUEUE");
			
		}
		
		// Retrieve Original Reply To Queue Manager from cache
		String originalReplyToQMgr = CacheHandlerFactory.getInstance().lookupCache(CacheHandlerFactory.MessageHeaderCache, correlId.getValueAsString().concat("Mgr"));	
		
		// If Original Reply To Queue found in cache, set as Reply To Queue
		if(originalReplyToQMgr != null){
			
			replyToQMgr.setValue(originalReplyToQMgr);
			//ComputeUtils.setElementInTree(replyToQMgr.getValueAsString(), outAssembly.getLocalEnvironment(), "Destination", "MQ", "DestinationData", "queueManagerName");
			logger.info("Original Reply To Queue Manager = {}", replyToQMgr.getValueAsString());
			
		} else {
			
			replyToQMgr.setValue(getUserDefinedAttribute("OUTPUT_QUEUE_MGR"));
			logger.warn("ERROR: original Reply To Q Mgr not found in cache. Output queue mgr set to default OUTPUT_QUEUE_MGR");
			
		}
		
		
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
