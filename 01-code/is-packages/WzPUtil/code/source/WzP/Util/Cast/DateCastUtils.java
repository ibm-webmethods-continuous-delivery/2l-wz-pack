package WzP.Util.Cast;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class DateCastUtils

{
	// ---( internal utility methods )---

	final static DateCastUtils _instance = new DateCastUtils();

	static DateCastUtils _newInstance() { return new DateCastUtils(); }

	static DateCastUtils _cast(Object o) { return (DateCastUtils)o; }

	// ---( server methods )---




	public static final void longEpochToDate (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(longEpochToDate)>> ---
		// @sigtype java 3.5
		// [i] object:0:optional epochMillis
		// [o] object:0:optional date
		Long receivedEpochMillis = null;
		boolean bPresent = false;
		java.util.Date d = null;
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		if (bPresent = pipelineCursor.first("epochMillis")) {
			receivedEpochMillis = (Long) IDataUtil.get(pipelineCursor, "epochMillis");
		}
		pipelineCursor.destroy();
		if ( ! bPresent) return;
		
		if (null != receivedEpochMillis){
			d = new java.util.Date(receivedEpochMillis.longValue());
		}
		pipelineCursor = pipeline.getCursor();
		IDataUtil.put(pipelineCursor, "date", d);
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}
}

