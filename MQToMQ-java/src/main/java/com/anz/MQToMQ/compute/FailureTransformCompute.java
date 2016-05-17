/**
 * 
 */
package com.anz.MQToMQ.compute;

import com.anz.MQToMQ.error.TransformFailureResponse;

import com.anz.common.compute.ComputeInfo;
import com.anz.common.compute.OutputTarget;
import com.anz.common.compute.TransformType;
import com.anz.common.compute.impl.CommonErrorTransformCompute;
import com.anz.common.compute.impl.ComputeUtils;
import com.anz.common.transform.ITransformer;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessageAssembly;

/**
 * @author root
 *
 */
public class FailureTransformCompute extends CommonErrorTransformCompute {

	@Override
	public ITransformer<MbMessageAssembly, String> getTransformer() {
		return new TransformFailureResponse();
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
	
	
	@Override
	public void executeAfterTransformation(ComputeInfo metadata,
			MbMessageAssembly inAssembly, MbMessageAssembly outAssembly) {
		
		if(OutputTarget.ALTERNATE == metadata.getOutputTarget()) {
		// Set Output queue name to:
			try {

				ComputeUtils.setElementInTree((String) getUserDefinedAttribute("ERROR_QUEUE_MGR"), outAssembly.getLocalEnvironment(), "Destination", "MQ", "DestinationData", "queueManagerName");
				ComputeUtils.setElementInTree((String) getUserDefinedAttribute("ERROR_QUEUE"), outAssembly.getLocalEnvironment(), "Destination","MQ","DestinationData", "queueName" );

			} catch (MbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
