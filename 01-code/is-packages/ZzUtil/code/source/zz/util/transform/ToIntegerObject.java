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

public final class ToIntegerObject

{
	// ---( internal utility methods )---

	final static ToIntegerObject _instance = new ToIntegerObject();

	static ToIntegerObject _newInstance() { return new ToIntegerObject(); }

	static ToIntegerObject _cast(Object o) { return (ToIntegerObject)o; }

	// ---( server methods )---




	public static final void stringToInteger (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(stringToInteger)>> ---
		// @sigtype java 3.5
		// [i] field:0:optional integerString
		// [o] object:0:optional integerObject
		String integerString = null;
		boolean present = false;
		Integer iObj = null;
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		present=pipelineCursor.first("integerString");
		if (present)
		{
			integerString = (String) pipelineCursor.getValue();
			if (null != integerString)
				iObj = Integer.parseInt(integerString);
		}
		pipelineCursor.destroy();
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "integerObject", iObj );
		pipelineCursor_1.destroy();
		// --- <<IS-END>> ---

                
	}
}

