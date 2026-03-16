package wz.advanced;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.data.IData;
// --- <<IS-END-IMPORTS>> ---

public final class microservices

{
	// ---( internal utility methods )---

	final static microservices _instance = new microservices();

	static microservices _newInstance() { return new microservices(); }

	static microservices _cast(Object o) { return (microservices)o; }

	// ---( server methods )---




	public static final void force (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(force)>> ---
		// @sigtype java 3.5
		com.wm.app.b2b.server.invoke.InvokeManager im = com.wm.app.b2b.server.invoke.InvokeManager.getDefault();
		im.setMicroservicesLicensed(true);
		// --- <<IS-END>> ---

                
	}
}

