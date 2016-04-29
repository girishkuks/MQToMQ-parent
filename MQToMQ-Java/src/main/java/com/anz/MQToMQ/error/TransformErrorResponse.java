/**
 * 
 */
package com.anz.MQToMQ.error;

import org.apache.logging.log4j.Logger;

import com.anz.common.compute.ComputeInfo;
import com.anz.common.compute.impl.ComputeUtils;
import com.anz.common.dataaccess.models.iib.IFXCode;
import com.anz.common.domain.IFXCodeDomain;
import com.anz.common.transform.ITransformer;
import com.anz.common.transform.TransformUtils;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;

/**
 * @author sanketsw
 * 
 */
public class TransformErrorResponse implements
		ITransformer<MbMessageAssembly, String> {

	

	@Override
	public String execute(MbMessageAssembly outAssembly, Logger logger, ComputeInfo metadata) throws Exception {
		String out = null;
		MbMessage inMessage = outAssembly.getMessage();
		String inputString = ComputeUtils.getStringFromBlob(inMessage);

		// Log the error
		logger.error(inputString);

		// Return the error after mapping errorCode from cache/database
		IFXCode errorCode = IFXCodeDomain.getInstance().getErrorCode("305");

		// If error code cannot be mapped, then return the original error
		if (errorCode == null) {
			// out = inputString;
			logger.info("passing the error over as it is {} ", out);
		} else {
			errorCode.setDescr(inputString);
			out = TransformUtils.toJSON(errorCode);
			logger.info("got the error code object from static data: {}", out);
		}
		return out;
	}

}
