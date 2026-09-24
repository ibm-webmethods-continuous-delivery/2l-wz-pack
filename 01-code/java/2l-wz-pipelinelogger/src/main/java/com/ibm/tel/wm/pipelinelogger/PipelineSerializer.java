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
     * @param serviceNS        The full namespace of the service being invoked
     * @param duration         The duration of the service execution in milliseconds
     * @param inboundPipeline  The input pipeline data
     * @param outboundPipeline The output pipeline data
     * @return A string representation of the serialized data
     */
    String serialize(String serviceNS, long duration, IData inboundPipeline, IData outboundPipeline);

    /**
     * Serializes the pipeline data together with an exception that was thrown during service
     * execution. The default implementation delegates to the no-exception overload and appends a
     * plain-text exception block; implementations may override to embed richer detail.
     *
     * @param serviceNS        The full namespace of the service being invoked
     * @param duration         The duration of the service execution in milliseconds
     * @param inboundPipeline  The input pipeline data
     * @param outboundPipeline The output pipeline data (may be partial or null on failure)
     * @param thrown           The exception that was thrown
     * @return A string representation of the serialized data including exception details
     */
    default String serialize(String serviceNS, long duration, IData inboundPipeline,
                             IData outboundPipeline, Throwable thrown) {
        StringBuilder sb = new StringBuilder(serialize(serviceNS, duration, inboundPipeline, outboundPipeline));
        sb.append("\nEXCEPTION: ").append(thrown.getClass().getName())
          .append(": ").append(thrown.getMessage());
        for (Throwable cause = thrown.getCause(); cause != null; cause = cause.getCause()) {
            sb.append("\nCAUSED BY: ").append(cause.getClass().getName())
              .append(": ").append(cause.getMessage());
        }
        java.io.StringWriter sw = new java.io.StringWriter();
        thrown.printStackTrace(new java.io.PrintWriter(sw));
        sb.append("\nSTACK TRACE:\n").append(sw);
        return sb.toString();
    }

    /**
     * Checks if this serializer is enabled via configuration.
     *
     * @return true if the serializer should be used, false otherwise
     */
    boolean isEnabled();
}
