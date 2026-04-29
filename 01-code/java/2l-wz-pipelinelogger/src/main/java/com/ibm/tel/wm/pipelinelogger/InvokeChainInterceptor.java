package com.ibm.tel.wm.pipelinelogger;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.invoke.InvokeChainProcessor;
import com.wm.app.b2b.server.invoke.InvokeManager;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import com.wm.data.IData;
import com.wm.data.IDataUtil;
import com.wm.util.JournalLogger;
import com.wm.util.ServerException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Invoke chain interceptor for pipeline logging.
 * Supports multiple serialization formats that can be enabled/disabled via configuration.
 */
public class InvokeChainInterceptor implements InvokeChainProcessor {
    public static final InvokeChainInterceptor INSTANCE = new InvokeChainInterceptor();

    private final List<PipelineSerializer> serializers;

    static {
        InvokeManager.getDefault().registerProcessor(INSTANCE);
        JournalLogger.logInfo(JournalLogger.LOG_EXCEPTION, JournalLogger.FAC_LICENSE_MGR,
            "Expert Labs Pipeline logger processor registered");
    }

    public InvokeChainInterceptor() {
        // Initialize all available serializers
        serializers = new ArrayList<>();
        serializers.add(new VerboseSerializer());
        JournalLogger.logInfo(JournalLogger.LOG_EXCEPTION, JournalLogger.FAC_LICENSE_MGR, "Verbose serializer added");
        serializers.add(new JsonSimpleSerializer());
        JournalLogger.logInfo(JournalLogger.LOG_EXCEPTION, JournalLogger.FAC_LICENSE_MGR, "Simple JSON serializer added");
        serializers.add(new JsonCompactSerializer());
        JournalLogger.logInfo(JournalLogger.LOG_EXCEPTION, JournalLogger.FAC_LICENSE_MGR, "Compact JSON serializer added");
    }

    @Override
    public void process(@SuppressWarnings("rawtypes") Iterator chain,
                       BaseService svc, IData pipeline, ServiceStatus status)
                       throws ServerException {

        if (!Config.INSTANCE.hasWorkToDo() || !status.isTopService()) {
            if (chain.hasNext()) {
                ((InvokeChainProcessor) chain.next()).process(chain, svc, pipeline, status);
            }
            return; // If not enabled OR a top level service, just pass the ball down the chain
        }

        // Keep a bit of info at start time
        long startTime = System.currentTimeMillis();
        String serviceNS = svc.getNSName().getFullName();
        IData inboundPipeline = null;
        try {
            inboundPipeline = IDataUtil.deepClone(pipeline);
        } catch (Throwable t) {
			JournalLogger.logInfo(JournalLogger.LOG_MSG, JournalLogger.FAC_LICENSE_MGR,
                "Pipeline Logger ERROR caught: " + t.getMessage());
        }

        try {
            if (chain.hasNext()) {
                ((InvokeChainProcessor) chain.next()).process(chain, svc, pipeline, status);
            }
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // Execute all enabled serializers
            for (PipelineSerializer serializer : serializers) {
                if (serializer.isEnabled()) {
                    try {
                        String output = serializer.serialize(serviceNS, duration, inboundPipeline, pipeline);
                        JournalLogger.logInfo(JournalLogger.LOG_EXCEPTION, JournalLogger.FAC_LICENSE_MGR, output);
                    } catch (Throwable t) {
                        JournalLogger.logError(JournalLogger.LOG_MSG, JournalLogger.FAC_LICENSE_MGR,
                            "Error in serializer " + serializer.getClass().getSimpleName() + ": " + t.getMessage());
                            t.printStackTrace();
                    }
                }
            }
        }
    }
}
