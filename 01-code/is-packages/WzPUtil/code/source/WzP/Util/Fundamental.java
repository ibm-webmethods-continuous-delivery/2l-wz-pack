package WzP.Util;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class Fundamental

{
	// ---( internal utility methods )---

	final static Fundamental _instance = new Fundamental();

	static Fundamental _newInstance() { return new Fundamental(); }

	static Fundamental _cast(Object o) { return (Fundamental)o; }

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

