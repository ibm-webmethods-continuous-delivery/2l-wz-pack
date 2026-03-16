package WzT;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class DataGen

{
	// ---( internal utility methods )---

	final static DataGen _instance = new DataGen();

	static DataGen _newInstance() { return new DataGen(); }

	static DataGen _cast(Object o) { return (DataGen)o; }

	// ---( server methods )---




	public static final void getFalseBoolean (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getFalseBoolean)>> ---
		// @sigtype java 3.5
		// [o] object:0:required nullObject
		final IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put( pipelineCursor, "bObjFalse", Boolean.valueOf(false) );
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



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

