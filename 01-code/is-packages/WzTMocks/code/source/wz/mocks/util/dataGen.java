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

public final class dataGen

{
	// ---( internal utility methods )---

	final static dataGen _instance = new dataGen();

	static dataGen _newInstance() { return new dataGen(); }

	static dataGen _cast(Object o) { return (dataGen)o; }

	// ---( server methods )---




	public static final void getPiWith7FractionDecimals (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getPiWith7FractionDecimals)>> ---
		// @sigtype java 3.5
		// [o] object:0:required piWith7FractionDecimals
		IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put( pipelineCursor, "piWith7FractionDecimals", 3.1415926F );
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void getRandomFloat (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getRandomFloat)>> ---
		// @sigtype java 3.5
		// [o] object:0:required randomFloat
		IDataCursor pipelineCursor = pipeline.getCursor();
		java.util.Random r = new java.util.Random();
		Object randomFloat = r.nextFloat();
		IDataUtil.put( pipelineCursor, "randomFloat", randomFloat );
		pipelineCursor.destroy();
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

