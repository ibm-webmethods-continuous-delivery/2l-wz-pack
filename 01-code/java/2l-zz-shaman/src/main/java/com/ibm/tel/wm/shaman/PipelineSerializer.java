package com.ibm.tel.wm.pipelinelogger;

import com.wm.data.IData;

/**
 * Interface for pipeline serialization strategies.
 * Implementations can provide different serialization formats for logging pipeline data.
 */
public interface PipelineSerializer {
    
    /**
     * Serializes the pipeline data into a string format.
     * 
     * @param serviceNS The full namespace of the service being invoked
     * @param duration The duration of the service execution in milliseconds
     * @param inboundPipeline The input pipeline data
     * @param outboundPipeline The output pipeline data
     * @return A string representation of the serialized data
     */
    String serialize(String serviceNS, long duration, IData inboundPipeline, IData outboundPipeline);
    
    /**
     * Checks if this serializer is enabled via configuration.
     * 
     * @return true if the serializer should be used, false otherwise
     */
    boolean isEnabled();
}
