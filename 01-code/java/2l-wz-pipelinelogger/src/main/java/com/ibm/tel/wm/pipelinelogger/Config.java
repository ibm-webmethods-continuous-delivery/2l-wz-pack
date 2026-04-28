package com.ibm.tel.wm.pipelinelogger;

public class Config {
    public static final Config INSTANCE = new Config();
    private static boolean bToggle = false;

    public boolean getToggle(){
        return bToggle;
    }

    public Config (){
        bToggle = "true".equalsIgnoreCase(System.getenv("ZX_PIPELINE_LOGGER_ACTIVE"));
    }

    public synchronized void setToggle(boolean toggle){
        bToggle = toggle;
    }
}
