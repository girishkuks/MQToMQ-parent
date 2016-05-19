/**
 * 
 */
package com.anz.MQToMQ.compute;

import com.anz.common.compute.TransformType;
import com.anz.common.compute.impl.CommonErrorTransformCompute;

/**
 * @author root
 *
 */
public class FailureTransformCompute extends CommonErrorTransformCompute {

	@Override
	public TransformType getTransformationType() {
		return TransformType.MQ_MQ;
	}



}
