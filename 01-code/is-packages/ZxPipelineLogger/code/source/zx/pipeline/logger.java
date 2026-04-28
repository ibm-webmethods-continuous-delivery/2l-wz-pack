package zx.pipeline;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class logger

{
	// ---( internal utility methods )---

	final static logger _instance = new logger();

	static logger _newInstance() { return new logger(); }

	static logger _cast(Object o) { return (logger)o; }

	// ---( server methods )---




	public static final void load (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(load)>> ---
		// @sigtype java 3.5
		com.ibm.tel.wm.pipelinelogger.InvokeChainInterceptor i = com.ibm.tel.wm.pipelinelogger.InvokeChainInterceptor.INSTANCE;
		// --- <<IS-END>> ---

                
	}
}

