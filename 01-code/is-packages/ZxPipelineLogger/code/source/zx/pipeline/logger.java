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




	public static final void getToggles (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getToggles)>> ---
		// @sigtype java 3.5
		// [o] field:0:required bMasterToggle
		// [o] field:0:required bVerboseToggle
		// [o] field:0:required bSimpleJsonToggle
		// [o] field:0:required bFullJsonToggle
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put( pipelineCursor, "bMasterToggle", ""+com.ibm.tel.wm.pipelinelogger.Config.INSTANCE.getToggle() );
		IDataUtil.put( pipelineCursor, "bVerboseToggle", ""+com.ibm.tel.wm.pipelinelogger.Config.INSTANCE.isVerboseSerializerEnabled() );
		IDataUtil.put( pipelineCursor, "bSimpleJsonToggle", ""+com.ibm.tel.wm.pipelinelogger.Config.INSTANCE.isJsonSimpleSerializerEnabled() );
		IDataUtil.put( pipelineCursor, "bFullJsonToggle", ""+com.ibm.tel.wm.pipelinelogger.Config.INSTANCE.isJsonCompactSerializerEnabled() );
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void load (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(load)>> ---
		// @sigtype java 3.5
		com.ibm.tel.wm.pipelinelogger.InvokeChainInterceptor i = com.ibm.tel.wm.pipelinelogger.InvokeChainInterceptor.INSTANCE;
		// --- <<IS-END>> ---

                
	}



	public static final void setCompactJsonToggle (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(setCompactJsonToggle)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional bToggle {"true","TRUE"}
		// 
		
		IDataCursor pipelineCursor = pipeline.getCursor();
			String	bToggle = IDataUtil.getString( pipelineCursor, "bToggle" );
		pipelineCursor.destroy();
		
		com.ibm.tel.wm.pipelinelogger.Config.INSTANCE.setJsonCompactSerializerEnabled(
				(bToggle != null && bToggle.equalsIgnoreCase("true"))
				);
		
		// pipeline
		// --- <<IS-END>> ---

                
	}



	public static final void setMasterToggle (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(setMasterToggle)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional bToggle {"true","TRUE"}
		// 
		
		IDataCursor pipelineCursor = pipeline.getCursor();
			String	bToggle = IDataUtil.getString( pipelineCursor, "bToggle" );
		pipelineCursor.destroy();
		
		com.ibm.tel.wm.pipelinelogger.Config.INSTANCE.setToggle(
				(bToggle != null && bToggle.equalsIgnoreCase("true"))
				);
		
		// pipeline
		// --- <<IS-END>> ---

                
	}



	public static final void setSimpleJsonToggle (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(setSimpleJsonToggle)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional bToggle {"true","TRUE"}
		// 
		
		IDataCursor pipelineCursor = pipeline.getCursor();
			String	bToggle = IDataUtil.getString( pipelineCursor, "bToggle" );
		pipelineCursor.destroy();
		
		com.ibm.tel.wm.pipelinelogger.Config.INSTANCE.setJsonSimpleSerializerEnabled(
				(bToggle != null && bToggle.equalsIgnoreCase("true"))
				);
		
		// pipeline
		// --- <<IS-END>> ---

                
	}



	public static final void setVerboseToggle (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(setVerboseToggle)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional bToggle {"true","TRUE"}
		// 
		
		IDataCursor pipelineCursor = pipeline.getCursor();
			String	bToggle = IDataUtil.getString( pipelineCursor, "bToggle" );
		pipelineCursor.destroy();
		
		com.ibm.tel.wm.pipelinelogger.Config.INSTANCE.setVerboseSerializerEnabled(
				(bToggle != null && bToggle.equalsIgnoreCase("true"))
				);
		
		// pipeline
		// --- <<IS-END>> ---

                
	}
}

