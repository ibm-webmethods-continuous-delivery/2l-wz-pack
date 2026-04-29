package com.ibm.tel.wm.pipelinelogger;

import com.wm.util.JournalLogger;

/**
 * Configuration class for pipeline logger.
 * Supports multiple serializer toggles that can be controlled via environment variables.
 * Toggles are non-exclusive - multiple serializers can be enabled simultaneously.
 */
public class Config {
    public static final Config INSTANCE = new Config();

    private static boolean bToggle;
    private static boolean bVerboseSerializerEnabled;
    private static boolean bJsonCompactSerializerEnabled;
    private static boolean bJsonSimpleSerializerEnabled;

    public boolean getToggle() {
        return bToggle;
    }

    public boolean isVerboseSerializerEnabled() {
        return bVerboseSerializerEnabled;
    }

    public boolean isJsonCompactSerializerEnabled() {
        return bJsonCompactSerializerEnabled;
    }

    public boolean isJsonSimpleSerializerEnabled() {
        return bJsonSimpleSerializerEnabled;
    }

    public Config() {
        // Master toggle - must be true for any logging to occur
        String masterToggle = System.getenv("ZX_PIPELINE_LOGGER_ENABLED");
        if (masterToggle != null) {
            bToggle = "true".equalsIgnoreCase(masterToggle);
        }else{
            bToggle = false;
        }

        // Individual serializer toggles
        String verboseEnv = System.getenv("ZX_PIPELINE_LOGGER_VERBOSE_ENABLED");
        if (verboseEnv != null) {
            bVerboseSerializerEnabled = "true".equalsIgnoreCase(verboseEnv);
        }else{
            bVerboseSerializerEnabled = false;
        }

        String jsonCompactEnv = System.getenv("ZX_PIPELINE_LOGGER_JSON_COMPACT_ENABLED");
        if (jsonCompactEnv != null) {
            bJsonCompactSerializerEnabled = "true".equalsIgnoreCase(jsonCompactEnv);
        }else{
            bJsonCompactSerializerEnabled = false;
        }

        String jsonSimpleEnv = System.getenv("ZX_PIPELINE_LOGGER_JSON_SIMPLE_ENABLED");
        if (jsonSimpleEnv != null) {
            bJsonSimpleSerializerEnabled = "true".equalsIgnoreCase(jsonSimpleEnv);
        }else{
            bJsonSimpleSerializerEnabled = true;
        }

        JournalLogger.logInfo(JournalLogger.LOG_EXCEPTION, JournalLogger.FAC_LICENSE_MGR,
            "Pipeline Logger Initialized: masterToggle=" + bToggle + 
            "; verboseToggle=" + bVerboseSerializerEnabled + 
            "; simpleJsonToggle=" + bJsonSimpleSerializerEnabled + 
            "; compactJsonToggle=" + bJsonCompactSerializerEnabled);
    }

    public synchronized void setToggle(boolean toggle) {
        JournalLogger.logInfo(JournalLogger.LOG_EXCEPTION, JournalLogger.FAC_LICENSE_MGR,
            "Setting master toggle to " + toggle);
        bToggle = toggle;
    }

    public synchronized void setVerboseSerializerEnabled(boolean enabled) {
        JournalLogger.logInfo(JournalLogger.LOG_EXCEPTION, JournalLogger.FAC_LICENSE_MGR,
            "Setting bVerboseSerializerEnabled to " + enabled);
        bVerboseSerializerEnabled = enabled;
    }

    public synchronized void setJsonCompactSerializerEnabled(boolean enabled) {
        JournalLogger.logInfo(JournalLogger.LOG_EXCEPTION, JournalLogger.FAC_LICENSE_MGR,
            "Setting bJsonCompactSerializerEnabled to " + enabled);
        bJsonCompactSerializerEnabled = enabled;
    }

    public synchronized void setJsonSimpleSerializerEnabled(boolean enabled) {
        JournalLogger.logInfo(JournalLogger.LOG_EXCEPTION, JournalLogger.FAC_LICENSE_MGR,
            "Setting bJsonSimpleSerializerEnabled to " + enabled);
        bJsonSimpleSerializerEnabled = enabled;
    }

    public boolean hasWorkToDo(){
        if (false == bToggle) return false;
        return bJsonCompactSerializerEnabled || bJsonSimpleSerializerEnabled || bVerboseSerializerEnabled;
    }
}
