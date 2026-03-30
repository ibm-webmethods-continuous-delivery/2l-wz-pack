package zz.util.transform;

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

public final class ToStringObject

{
	// ---( internal utility methods )---

	final static ToStringObject _instance = new ToStringObject();

	static ToStringObject _newInstance() { return new ToStringObject(); }

	static ToStringObject _cast(Object o) { return (ToStringObject)o; }

	// ---( server methods )---




	public static final void booleanToString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(booleanToString)>> ---
		// @sigtype java 3.5
		// [i] object:0:optional bJava
		// [o] field:0:optional bString
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		boolean bPresent = false;
		String bString = null;
		Object bJava = null;
		if (bPresent = pipelineCursor.first("bJava")) {
			bJava = IDataUtil.get(pipelineCursor, "bJava");
		}
		pipelineCursor.destroy();
		
		// pipeline
		if (bPresent) {
			if (null != bJava)
				if (bJava instanceof java.lang.Boolean)
					bString = bJava.toString();
				else
					throw new ServiceException("Received object is not a java.lang.Boolean!");
			IDataCursor pipelineCursor_1 = pipeline.getCursor();
			IDataUtil.put(pipelineCursor_1, "bString", bString);
			pipelineCursor_1.destroy();
		}else{
			throw new ServiceException("Pipeline MUST contain a key <<bJava>>");
		}
		// --- <<IS-END>> ---

                
	}
}

