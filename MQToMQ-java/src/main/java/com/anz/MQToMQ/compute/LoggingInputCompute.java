package com.anz.MQToMQ.compute;

import com.anz.common.compute.LoggingTerminal;
import com.anz.common.compute.impl.CommonLoggingCompute;

public class LoggingInputCompute extends CommonLoggingCompute {

	@Override
	public LoggingTerminal getLogTerminal() {
		return LoggingTerminal.INPUT;
	}

}
