/**
 * 
 */
package com.anz.MQToMQ.transform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.anz.MQToMQ.transform.pojo.Result;
import com.anz.common.compute.ComputeInfo;
import com.anz.common.dataaccess.models.iib.Operation;
import com.anz.common.domain.OperationDomain;
import com.anz.common.transform.ITransformer;
import com.anz.common.transform.TransformUtils;

/**
 * @author sanketsw
 * 
 */
public class TransformBLSampleWithCache implements ITransformer<String, String> {

	private static final Logger logger = LogManager.getLogger();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.anz.common.transform.IJsonJsonTransformer#execute(java.lang.String)
	 */
	public String execute(String inputJson, Logger logger, ComputeInfo metadata) throws Exception {
		String out = inputJson;
		try {
			Result json = (Result) TransformUtils.fromJSON(inputJson,
					Result.class);
			logger.info("inputJson= {}", inputJson);
			if (json.getResult() == null)
				throw new Exception("invalid response data detected");

			// Read data from Cache
			String objectKey = Operation.ADD;
			Operation op = OperationDomain.getInstance().getOperation(objectKey);
			logger.info("value set from cache/DB= {}", TransformUtils.toJSON(op));
			json.setOperation(op.getOperation());
			json.setImeplementation(op.getImeplementation());			
			json.setComment("read from jdbc type4 connection and cached:" + op);

			out = TransformUtils.toJSON(json);
		} catch (Exception e) {
			logger.throwing(e);
			throw e;
		}
		return out;
	}

}
