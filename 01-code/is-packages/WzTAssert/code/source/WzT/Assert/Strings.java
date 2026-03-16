package WzT.Assert;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.util.JournalLogger;
// --- <<IS-END-IMPORTS>> ---

public final class Strings

{
	// ---( internal utility methods )---

	final static Strings _instance = new Strings();

	static Strings _newInstance() { return new Strings(); }

	static Strings _cast(Object o) { return (Strings)o; }

	// ---( server methods )---




	public static final void is (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(is)>> ---
		// @sigtype java 3.5
		// [i] field:0:required assertedString
		// [i] field:0:required assertedValue
		// [i] field:0:optional label
		// [o] field:0:required bAssertionResult
		boolean bResult=false;
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			final boolean bExists = pipelineCursor.first("assertedString");
			java.lang.String assertedString = null;
			if(bExists)
				assertedString = (java.lang.String) IDataUtil.get( pipelineCursor, "assertedString" );
			final boolean bValueExists = pipelineCursor.first("assertedValue");
			java.lang.String assertedValue = null;
			if(bValueExists)
				assertedValue = (java.lang.String) IDataUtil.get( pipelineCursor, "assertedValue" );
			final java.lang.String label = pipelineCursor.first("label")?(java.lang.String)IDataUtil.get( pipelineCursor, "label" ):"is";
		pipelineCursor.destroy();
		
		int errNo = 0;
		
		// First parameter
		if (!bExists){
			JournalLogger.log(LOG_CODE, LOG_FACILITY, JournalLogger.WARNING, LOG_PREFIX+" "+label, 
					"Failed: No input received for mandatory field <<assertedString>>");
			errNo ++;
		}
		else if( null == assertedString ){
			JournalLogger.log(LOG_CODE, LOG_FACILITY, JournalLogger.WARNING, LOG_PREFIX+" "+label, 
					"Failed: Null object received as asserted string!");
			errNo ++;
		}
		
		// Second parameter
		if (!bValueExists){
			JournalLogger.log(LOG_CODE, LOG_FACILITY, JournalLogger.WARNING, LOG_PREFIX+" "+label, 
					"Failed: No input received for mandatory field <<assertedValue>>");
			errNo ++;
		}
		else if( null == assertedValue ){
			JournalLogger.log(LOG_CODE, LOG_FACILITY, JournalLogger.WARNING, LOG_PREFIX+" "+label, 
					"Failed: Null object received as asserted value!");
			errNo ++;
		}
		
		// TODO (Maybe) - absent = absent and null = null should also return true
		if (0 == errNo ){
			if(assertedValue.equals(assertedString)){
				bResult = true;
				JournalLogger.log(LOG_CODE, LOG_FACILITY, JournalLogger.INFO, LOG_PREFIX+" "+label, 
						"OK: Assertion passed!");
			}else{
				JournalLogger.log(LOG_CODE, LOG_FACILITY, JournalLogger.WARNING, LOG_PREFIX+" "+label
						, "Failed: String <<"+assertedString+">> is not <<"+ assertedValue + ">>");
			}
		}else{
			JournalLogger.log(LOG_CODE, LOG_FACILITY, JournalLogger.WARNING, LOG_PREFIX+" "+label
											, "Failed: parameters error ");
		}
		// pipeline
		pipelineCursor = pipeline.getCursor();
		IDataUtil.put( pipelineCursor, "bAssertionResult", "" + bResult );
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private final static int LOG_CODE=4;
	private final static int LOG_FACILITY=90;
	private final static java.lang.String LOG_PREFIX="String Assertions";
		
	// --- <<IS-END-SHARED>> ---
}

