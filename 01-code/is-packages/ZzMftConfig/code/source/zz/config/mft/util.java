package zz.config.mft;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.app.b2b.server.PackageManager;
import com.wm.app.b2b.server.cluster.CMException;
import com.wm.app.b2b.server.cluster.ClusterManager;
import com.wm.util.EncUtil;
import com.wm.app.log.impl.sc.LevelTranslator;
import com.wm.lang.flow.IDataWmPathProcessor;
import com.wm.util.JournalLogger;
import com.entrust.toolkit.util.ByteArray;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.lang.reflect.*;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.*;
// --- <<IS-END-IMPORTS>> ---

public final class util

{
	// ---( internal utility methods )---

	final static util _instance = new util();

	static util _newInstance() { return new util(); }

	static util _cast(Object o) { return (util)o; }

	// ---( server methods )---




	public static final void mapBoolToString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(mapBoolToString)>> ---
		// @sigtype java 3.5
		// [i] object:0:optional boolIn
		// [o] field:0:optional boolOut
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		java.lang.Boolean	boolIn = null;
		boolean bFound = pipelineCursor.first("boolIn");
		if ( bFound ) boolIn = (java.lang.Boolean) pipelineCursor.getValue();
		pipelineCursor.destroy();
		
		// pipeline
		if ( bFound ){
			IDataCursor pipelineCursor_1 = pipeline.getCursor();
			if ( null == boolIn )
				IDataUtil.put( pipelineCursor_1, "boolOut", null );
			else
				IDataUtil.put( pipelineCursor_1, "boolOut", ""+boolIn );
			pipelineCursor_1.destroy();
		}
		// --- <<IS-END>> ---

                
	}



	public static final void readFileAsBytes (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(readFileAsBytes)>> ---
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [o] object:0:required fileBytes
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			String	filename = IDataUtil.getString( pipelineCursor, "filename" );
		pipelineCursor.destroy();
		
		byte[] array = null;
		try {
			array = Files.readAllBytes(Paths.get(filename));
		} catch (IOException e) {
			throw new ServiceException(e);
		}			
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "fileBytes", array );
		pipelineCursor_1.destroy();
		// --- <<IS-END>> ---

                
	}
}

