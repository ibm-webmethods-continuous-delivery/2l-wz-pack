package wz.log;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.data.IData;
// --- <<IS-END-IMPORTS>> ---

public final class manager

{
	// ---( internal utility methods )---

	final static manager _instance = new manager();

	static manager _newInstance() { return new manager(); }

	static manager _cast(Object o) { return (manager)o; }

	// ---( server methods )---




	public static final void getAppenderDetails (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getAppenderDetails)>> ---
		// @sigtype java 3.5
		// [i] field:0:required appenderName
		// --- <<IS-END>> ---

                
	}



	public static final void getCurrentLog4jAppenders (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getCurrentLog4jAppenders)>> ---
		// @sigtype java 3.5
		// [o] record:1:required appenders
		// [o] - field:0:required name
		try{
		Class.forName("org.apache.logging.log4j.LogManager");
		Class.forName("org.apache.logging.log4j.core.LoggerContext");
		}
		catch(ClassNotFoundException e){
		throw new ServiceException(e);
		}
		org.apache.logging.log4j.core.LoggerContext lc = (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
		if (null == lc) throw new ServiceException("Could not get the log4j2 logger context. Are log4j2 libraries loaded?");
		
		java.util.Map<String, org.apache.logging.log4j.core.Appender> appendersMap = lc.getConfiguration().getAppenders();
		java.util.ArrayList<IData> ali = new java.util.ArrayList<IData>();
		appendersMap.forEach((name, appender) -> {
		IData a = IDataFactory.create();
		IDataCursor appendersCursor = a.getCursor();
		IDataUtil.put( appendersCursor, "name", name);
		IDataUtil.put( appendersCursor, "type", appender.getClass().getName());
		appendersCursor.destroy();
		ali.add(a);
		System.out.println("Appender Name: " + name);
		System.out.println("Appender Type: " + appender.getClass().getName());
			});
		IData[]	appendersList = ali.toArray(new IData[appendersMap.size()]);
		
		///////////////////
		
		// pipeline
		final IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put( pipelineCursor, "currentAppenders", appendersList );
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void getCurrentLog4jLoggers (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getCurrentLog4jLoggers)>> ---
		// @sigtype java 3.5
		// [o] field:1:optional currentLoggers
		try {
		Class.forName("org.apache.logging.log4j.LogManager");
		Class.forName("org.apache.logging.log4j.core.LoggerContext");
		} catch (ClassNotFoundException e) {
		throw new ServiceException(e);
		}
		org.apache.logging.log4j.core.LoggerContext lc = (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
		if (null == lc)
		throw new ServiceException("Could not get the log4j2 logger context. Are log4j2 libraries loaded?");
		
		java.util.List<String> loggerNames = new java.util.ArrayList<>();
		java.util.Collection<org.apache.logging.log4j.core.Logger> cl = lc.getLoggers();
		if (0 == cl.size()) {
		System.out.println("getCurrentlog4jLoggers: No loggers found");
		return;
		}
		
		cl.forEach(logger -> {
		loggerNames.add(logger.getName());
		});
		
		// pipeline
		final IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put(pipelineCursor, "currentLoggers", loggerNames.toArray(new String[cl.size()]));
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void getLoggerDetails (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getLoggerDetails)>> ---
		// @sigtype java 3.5
		// [i] field:0:required loggerName
		// [o] field:0:required logLevel
		// [o] record:1:required appenders
		// [o] - field:0:required name
		try {
		Class.forName("org.apache.logging.log4j.LogManager");
		Class.forName("org.apache.logging.log4j.core.LoggerContext");
		} catch (ClassNotFoundException e) {
		throw new ServiceException(e);
		}
		
		// pipeline
		final IDataCursor pipelineCursor = pipeline.getCursor();
		String loggerName = IDataUtil.getString(pipelineCursor, "loggerName");
		pipelineCursor.destroy();
		
		org.apache.logging.log4j.core.LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
		
		org.apache.logging.log4j.core.Logger logger = context.getLogger(loggerName);
		
		// pipeline
		
		java.util.Map<String, org.apache.logging.log4j.core.Appender> appenders = logger.getAppenders();
		
		// appenders
		java.util.ArrayList<IData> ali = new java.util.ArrayList<IData>();
		
		appenders.forEach((name, appender) -> {
		IData a = IDataFactory.create();
		IDataCursor appendersCursor = a.getCursor();
		IDataUtil.put(appendersCursor, "name", name);
		IDataUtil.put(appendersCursor, "type", appender.getClass().getName());
		appendersCursor.destroy();
		ali.add(a);
		System.out.println("Appender Name: " + name);
		System.out.println("Appender Type: " + appender.getClass().getName());
		});
		IData[] appendersList = ali.toArray(new IData[appenders.size()]);
		
		final IDataCursor pipelineCursor_out = pipeline.getCursor();
		IDataUtil.put(pipelineCursor_out, "logLevel", logger.getLevel().toString());
		IDataUtil.put(pipelineCursor_out, "appenders", appendersList);
		pipelineCursor_out.destroy();
		// --- <<IS-END>> ---

                
	}
}

