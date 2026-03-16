package WzP.Util.Advanced;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.Arrays;
import com.wm.passman.PasswordManagerException;
// --- <<IS-END-IMPORTS>> ---

public final class PasswordManagement

{
	// ---( internal utility methods )---

	final static PasswordManagement _instance = new PasswordManagement();

	static PasswordManagement _newInstance() { return new PasswordManagement(); }

	static PasswordManagement _cast(Object o) { return (PasswordManagement)o; }

	// ---( server methods )---




	public static final void getOutboundPassword (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getOutboundPassword)>> ---
		// @sigtype java 3.5
		// [i] field:0:required entryKey
		// [o] field:0:required password
		// pipeline
		boolean bPresent = false;
		String	entryKey = null;
		IDataCursor pipelineCursor = pipeline.getCursor();
		if ( bPresent = pipelineCursor.first("entryKey")){
			entryKey = (String) IDataUtil.get( pipelineCursor, "entryKey" );
		}		
		pipelineCursor.destroy();
		
		if (!bPresent) return; // absent -> absent
		
		// TODO: address key absent case
		String p = null;
		
		if ( null != entryKey){
			try {
				com.wm.util.security.WmSecureString ps = com.wm.app.b2b.server.OutboundPasswordManager.retrievePassword(entryKey);
				if (null == ps) throw new ServiceException("No such key: " + entryKey);
				p = ps.toString();
			} catch (PasswordManagerException e) {
				throw new ServiceException(e);
			}
		}
		
		// pipeline
		pipelineCursor = pipeline.getCursor();
		IDataUtil.put( pipelineCursor, "password", p );
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void listOutboundKeys (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(listOutboundKeys)>> ---
		// @sigtype java 3.5
		// [o] field:1:required keysList
		String[] keysList;
		String[] adminKeysList;	
		
		java.util.List<String> list = null;
		
		try {
			keysList = com.wm.app.b2b.server.OutboundPasswordAdministrator.listHandles();
			adminKeysList = com.wm.app.b2b.server.OutboundPasswordAdministrator.listAdminHandles();
		} catch (PasswordManagerException e) {
			throw new ServiceException(e);
		}
		
		list = new java.util.ArrayList<>(Arrays.asList(keysList));
		list.addAll(Arrays.asList(adminKeysList));
		String[] mergedArray = list.toArray(new String[0]);
		Arrays.sort(mergedArray);
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put( pipelineCursor, "keysList", mergedArray );
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---

	
	// --- <<IS-END-SHARED>> ---
}

