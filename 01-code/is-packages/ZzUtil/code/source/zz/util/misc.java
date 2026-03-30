package zz.util;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class misc

{
	// ---( internal utility methods )---

	final static misc _instance = new misc();

	static misc _newInstance() { return new misc(); }

	static misc _cast(Object o) { return (misc)o; }

	// ---( server methods )---




	public static final void getNull (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getNull)>> ---
		// @sigtype java 3.5
		// [o] object:0:required nullObject
		final IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put( pipelineCursor, "nullObject", null );
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}
}

