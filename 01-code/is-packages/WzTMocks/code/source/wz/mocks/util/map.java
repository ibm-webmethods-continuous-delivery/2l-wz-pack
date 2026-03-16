package wz.mocks.util;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;
import java.util.Map;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
// --- <<IS-END-IMPORTS>> ---

public final class map

{
	// ---( internal utility methods )---

	final static map _instance = new map();

	static map _newInstance() { return new map(); }

	static map _cast(Object o) { return (map)o; }

	// ---( server methods )---




	public static final void floatToString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(floatToString)>> ---
		// @sigtype java 3.5
		// [i] object:0:required floatValue
		// [i] field:0:required fractionDigits
		// [o] field:0:required stringConstrainedDecimal
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		Float	floatValue = (Float) IDataUtil.get( pipelineCursor, "floatValue" );
		String	fractionDigits = IDataUtil.getString( pipelineCursor, "fractionDigits" );
		pipelineCursor.destroy();
		
		String formattedString=null;
		if(null==fractionDigits){
			formattedString=""+floatValue;
		}else{
			int iFractionDigits=2;
			if(null != fractionDigits){
				iFractionDigits=Integer.parseInt(fractionDigits);
			}
			final String lFormat="#0."+"0".repeat(iFractionDigits);
			java.text.DecimalFormat df = new java.text.DecimalFormat(lFormat);
			formattedString=df.format(floatValue);
			//df.setMaximumFractionDigits(iFractionDigits);
		}
		
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "stringConstrainedDecimal", formattedString );
		pipelineCursor_1.destroy();
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	static private String getCrtHostName(){
		String s = null;
		try {
			s = java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	static final private String crtHostName=getCrtHostName();
	// --- <<IS-END-SHARED>> ---
}

