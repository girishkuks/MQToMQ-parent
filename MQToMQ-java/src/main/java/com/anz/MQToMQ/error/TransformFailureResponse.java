/**
 * 
 */
package com.anz.MQToMQ.error;

import java.util.Calendar;

import org.apache.logging.log4j.Logger;

import com.anz.common.compute.ComputeInfo;
import com.anz.common.compute.impl.ComputeUtils;
import com.anz.common.dataaccess.models.iib.ErrorStatusCode;
import com.anz.common.domain.ErrorStatusCodeDomain;
import com.anz.common.error.ExceptionMessage;
import com.anz.common.transform.ITransformer;
import com.anz.common.transform.TransformUtils;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;

/**
 * @author sanketsw
 * 
 */
public class TransformFailureResponse implements
		ITransformer<MbMessageAssembly, String> {


	@Override
	public String execute(MbMessageAssembly outAssembly, Logger logger,
			ComputeInfo metadata) throws Exception {
		
		logger.entry();
		
		String out = null;

		String exceptionText = ComputeUtils.getExceptionText(outAssembly);
		logger.error("exceptionText {} ", exceptionText);

		// This could be the business or HTTP Request exception
		MbMessage outMessage = outAssembly.getMessage();
		String messageString = ComputeUtils.getStringFromBlob(outMessage);
		
		//TODO ANZ
		// Mapping the exception to error status code
		
		String errorString = null;
		if(exceptionText == null && messageString== null) {
			// This is a timeout on MQ
			errorString = ErrorStatusCode.TimeoutException;
		} else {	
			errorString = ErrorStatusCode.InternalException;
		}

		// Log the input blob
		logger.error("inputString {} ", messageString);

		ExceptionMessage exceptionMessage = new ExceptionMessage();
		exceptionMessage.setTimestamp(Calendar.getInstance().getTime());
		exceptionMessage.setShortException(exceptionText);
		exceptionMessage.setMessage(messageString);
		exceptionMessage.setBrokerAndServiceDetails(metadata);
		exceptionMessage.setStaticProperties();	
		
		ErrorStatusCode errorCode = null;
		try {
			// Return the error after mapping errorCode from cache/database
			errorCode = ErrorStatusCodeDomain.getInstance().getErrorCode(errorString);
		} catch(Exception e ) {
			logger.throwing(e);
		}
		// If error code cannot be mapped, then return the original error
		if (errorCode != null) {
			exceptionMessage.setStatus(errorCode);
		}

		out = TransformUtils.toJSON(exceptionMessage);
		logger.info("Error Status Code object {}", out);
		return out;
	}


}
