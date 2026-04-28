package com.ibm.tel.wm.pipelinelogger;
import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.invoke.InvokeChainProcessor;
import com.wm.app.b2b.server.invoke.InvokeManager;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
import com.wm.util.JournalLogger;
import com.wm.util.ServerException;
import java.util.Iterator;
import com.wm.app.b2b.server.InvokeState;
// import com.webmethods.g11n.util.iContext;

public class InvokeChainInterceptor implements InvokeChainProcessor {
	public static final InvokeChainInterceptor INSTANCE = new InvokeChainInterceptor();

	static {
    InvokeManager.getDefault().registerProcessor(INSTANCE);

    JournalLogger.logInfo(JournalLogger.LOG_EXCEPTION, JournalLogger.FAC_LICENSE_MGR, "Expert Labs Pipeline logger processor registered");
	}
	
	public InvokeChainInterceptor() {
	}

  private static final String INDENT_SPACE = "+ ";

	private static final void writeKeyPart(String sType, String sKey, int indent, java.io.StringWriter out, boolean bAddl){
		out.write("\n");
		if(bAddl)
			out.write(INDENT_SPACE);
		for(int t=0;t<indent;t++)
			out.write(INDENT_SPACE);
		out.write(sType);
		out.write(" ");
		out.write(sKey);
	}
	
	private static final void writeLogTrace(String myKey, IData in, int indent, java.io.StringWriter out, boolean bRoot) 
  //throws ServiceException
	{
		IDataCursor idc = in.getCursor();
		writeKeyPart("{IData}", myKey, indent, out, false);
		while ( idc.next() )
		{
			String key = (String) idc.getKey();
			Object val = idc.getValue();
			if (val instanceof com.wm.util.coder.IDataCodable)
			{
				val = ((com.wm.util.coder.IDataCodable)val).getIData();
			}
			if (val instanceof String[][])
			{
				writeKeyPart("{java.lang.String[][]}", key, indent, out, true);
				String[][] st = (String[][])val;
				for (int k=0; k<st.length; k++)
				{
					for (int j=0; j<st[0].length; j++)
					{
						if(key.equalsIgnoreCase("password"))
							st[k][j]="*";
						out.write("\n");
						out.write(INDENT_SPACE);
						out.write(INDENT_SPACE);
						for(int t=0;t<indent;t++)
							out.write(INDENT_SPACE);
						out.write("["+k+"]["+j+"] = " + st[k][j]);
					}
				}
			}
			else if (val instanceof String[])
			{
				writeKeyPart("{java.lang.String[]}", key, indent, out, true);
	
				String[] sa = (String[])val;
				for (int k=0; k<sa.length; k++)
				{
					if(key.equalsIgnoreCase("password"))
						sa[k]="*";				
					out.write("\n");
					out.write(INDENT_SPACE);
					out.write(INDENT_SPACE);
					for(int t=0;t<indent;t++)
						out.write(INDENT_SPACE);
					out.write("["+k+"] = " + sa[k]);
				}
			}
			else if (val instanceof IData[])
			{
				writeKeyPart("{IData[]}", key, indent, out, true);
	
				IData[] ida = (IData[])val;
				for (int l=0; l<ida.length; l++)
				{
					writeLogTrace("[" + l + "]", ida[l], indent+2, out, false);
				}
			}
			else if (val instanceof IData)
			{
				writeLogTrace(key, (IData)val, indent+1, out, false);
			}
			else if (val instanceof com.wm.util.coder.IDataCodable[])
			{
				com.wm.util.coder.IDataCodable[] ida = (com.wm.util.coder.IDataCodable[])val;
				for (int l=0; l<ida.length; l++)
				{
					writeLogTrace(key, ida[l].getIData(), indent+1, out, false);
				}
			}
			else if(null == val){
				out.write("\n");
				out.write(INDENT_SPACE);
				for(int t=0;t<indent;t++)
					out.write(INDENT_SPACE);
				out.write("(null) " + key);
			}
			else if (val instanceof byte[]){
				writeKeyPart("{byte[]}", key, indent, out, true);
				out.write(" = *");
			}
			else if (val.getClass().isArray()){
				writeKeyPart("{java.lang.Object[]}", key, indent, out, true);
	
				Object[] oa = (Object[]) val;
				for (int k = 0; k < oa.length; k++) {
					out.write("\n");
					out.write(INDENT_SPACE);
					out.write(INDENT_SPACE);
					for(int t=0;t<indent;t++)
						out.write(INDENT_SPACE);
					if (key.equalsIgnoreCase("password"))
						oa[k] = "*";
					if(null == oa[k]){
						out.write("[" + k +"](null)");
					}else{
						out.write("{" + oa[k].getClass().getCanonicalName()
								+ "}[" + k + "] = "
								+ oa[k].toString());
					}
				}
			}
			else
			{
				out.write("\n");
				out.write(INDENT_SPACE);
				for(int t=0;t<indent;t++)
					out.write(INDENT_SPACE);
				if(key.equalsIgnoreCase("password"))
					val="*";
				out.write("{"+val.getClass().getName()+"} " + key + " = " + val);
			}
		}
		idc.destroy();
	}

	@Override
	public void process(@SuppressWarnings("rawtypes") Iterator chain
                        , BaseService svc, IData pipeline, ServiceStatus status) 
                        throws ServerException {

    if (!Config.INSTANCE.getToggle() || !status.isTopService()) {
			if (chain.hasNext()) {
				((InvokeChainProcessor) chain.next()).process(chain, svc, pipeline, status);
			}
      return; // If not a top level service, just pass the ball down the chain
    }

    // keep a bit of info at start time
    long startTime = System.currentTimeMillis();
		String serviceNS = svc.getNSName().getFullName();
    IData inboundPipeline = null;
    try{
        inboundPipeline = IDataUtil.deepClone(pipeline);
    }catch(Throwable t){
        System.err.print("xxYYzz ERROR caught" + t.getMessage());
    }
    
    try {
        if (chain.hasNext()) {
            ((InvokeChainProcessor) chain.next()).process(chain, svc, pipeline, status);
        }
    } finally {
		long duration = System.currentTimeMillis() - startTime;

		java.io.StringWriter sw = new java.io.StringWriter();
		sw.write("Service call");
		sw.write("\nService         : " + serviceNS);
		sw.write("\nDuration millis : " + (System.currentTimeMillis() - startTime));
		sw.write("\n== Input  Pipeline ==============================");
		writeLogTrace("inputPipeline", inboundPipeline, 0, sw, true);
		sw.write("\n== Output Pipeline ==============================");
		writeLogTrace("outputPipeline", pipeline, 0, sw, true);
		JournalLogger.logInfo(JournalLogger.LOG_EXCEPTION, JournalLogger.FAC_LICENSE_MGR, sw.toString());
    }
	}
}
