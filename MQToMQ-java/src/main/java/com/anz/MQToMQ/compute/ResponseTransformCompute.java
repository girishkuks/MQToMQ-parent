/**
 * 
 */
package com.anz.MQToMQ.compute;

import com.anz.MQToMQ.transform.PostTransformBLSample;
import com.anz.common.compute.impl.CommonBlobTransformCompute;
import com.anz.common.transform.ITransformer;
import com.ibm.broker.plugin.MbMessageAssembly;

/**
 * @author sanketsw
 *
 */
public class ResponseTransformCompute extends CommonBlobTransformCompute {

	/* (non-Javadoc)
	 * @see com.anz.common.compute.impl.CommonJsonJsonTransformCompute#getTransformer()
	 */
	@Override
	public ITransformer<String, String> getTransformer() {
		return new PostTransformBLSample();
	}

	@Override
	public void saveUserProvidedProperties(MbMessageAssembly outAssembly) {
		// TODO Auto-generated method stub
		
	}

}
