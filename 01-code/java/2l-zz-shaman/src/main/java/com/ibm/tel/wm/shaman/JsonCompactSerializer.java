package com.ibm.tel.wm.pipelinelogger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wm.data.IData;
import com.wm.data.IDataCursor;

/**
 * JSON compact serializer for production environments.
 * Produces single-line JSON output with full type information.
 * Includes key, object type, and object value for all fields.
 */
public class JsonCompactSerializer implements PipelineSerializer {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public String serialize(String serviceNS, long duration, IData inboundPipeline, IData outboundPipeline) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("service", serviceNS);
            root.put("durationMillis", duration);
            root.set("inputPipeline", serializeIData(inboundPipeline));
            root.set("outputPipeline", serializeIData(outboundPipeline));
            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            return "{\"error\":\"Failed to serialize pipeline: " + e.getMessage() + "\"}";
        }
    }
    
    @Override
    public boolean isEnabled() {
        return Config.INSTANCE.isJsonCompactSerializerEnabled();
    }
    
    /**
     * Enhanced password protection: checks if key contains "password" (case-insensitive)
     */
    private static boolean isPasswordField(String key) {
        return key != null && key.toLowerCase().contains("password");
    }
    
    private ObjectNode serializeIData(IData data) {
        ObjectNode node = mapper.createObjectNode();
        if (data == null) {
            return node;
        }
        
        IDataCursor cursor = data.getCursor();
        for (boolean ok = cursor.first(); ok; ok = cursor.next()) {
            String key = (String) cursor.getKey();
            Object val = cursor.getValue();
            
            if (val instanceof com.wm.util.coder.IDataCodable) {
                val = ((com.wm.util.coder.IDataCodable) val).getIData();
            }
            
            node.set(key, serializeValue(key, val));
        }
        cursor.destroy();
        return node;
    }
    
    private com.fasterxml.jackson.databind.JsonNode serializeValue(String key, Object val) {
        ObjectNode valueNode = mapper.createObjectNode();
        
        if (val == null) {
            valueNode.put("type", "null");
            valueNode.putNull("value");
        } else if (val instanceof java.util.Date) {
            valueNode.put("type", val.getClass().getName());
            valueNode.put("value", ((java.util.Date) val).toInstant().toString());
        } else if (val instanceof String[][]) {
            valueNode.put("type", "java.lang.String[][]");
            ArrayNode arrayNode = mapper.createArrayNode();
            String[][] st = (String[][]) val;
            for (int k = 0; k < st.length; k++) {
                ArrayNode innerArray = mapper.createArrayNode();
                for (int j = 0; j < st[k].length; j++) {
                    String value = isPasswordField(key) ? "*" : st[k][j];
                    innerArray.add(value);
                }
                arrayNode.add(innerArray);
            }
            valueNode.set("value", arrayNode);
        } else if (val instanceof String[]) {
            valueNode.put("type", "java.lang.String[]");
            ArrayNode arrayNode = mapper.createArrayNode();
            String[] sa = (String[]) val;
            for (String s : sa) {
                String value = isPasswordField(key) ? "*" : s;
                arrayNode.add(value);
            }
            valueNode.set("value", arrayNode);
        } else if (val instanceof IData[]) {
            valueNode.put("type", "IData[]");
            ArrayNode arrayNode = mapper.createArrayNode();
            IData[] ida = (IData[]) val;
            for (IData iData : ida) {
                arrayNode.add(serializeIData(iData));
            }
            valueNode.set("value", arrayNode);
        } else if (val instanceof IData) {
            valueNode.put("type", "IData");
            valueNode.set("value", serializeIData((IData) val));
        } else if (val instanceof com.wm.util.coder.IDataCodable[]) {
            valueNode.put("type", "IDataCodable[]");
            ArrayNode arrayNode = mapper.createArrayNode();
            com.wm.util.coder.IDataCodable[] ida = (com.wm.util.coder.IDataCodable[]) val;
            for (com.wm.util.coder.IDataCodable codable : ida) {
                arrayNode.add(serializeIData(codable.getIData()));
            }
            valueNode.set("value", arrayNode);
        } else if (val instanceof byte[]) {
            valueNode.put("type", "byte[]");
            valueNode.put("value", "*");
        } else if (val.getClass().isArray()) {
            valueNode.put("type", "java.lang.Object[]");
            ArrayNode arrayNode = mapper.createArrayNode();
            Object[] oa = (Object[]) val;
            for (Object o : oa) {
                if (o == null) {
                    arrayNode.addNull();
                } else {
                    ObjectNode itemNode = mapper.createObjectNode();
                    itemNode.put("type", o.getClass().getCanonicalName());
                    String value = isPasswordField(key) ? "*" : o.toString();
                    itemNode.put("value", value);
                    arrayNode.add(itemNode);
                }
            }
            valueNode.set("value", arrayNode);
        } else {
            valueNode.put("type", val.getClass().getName());
            String value;
            if (isPasswordField(key)) {
                value = "*";
            } else {
                try {
                    value = val.toString();
                } catch (Throwable t) {
                    value = "Serialization error: " + t.getMessage();
                }
            }
            valueNode.put("value", value);
        }
        
        return valueNode;
    }
}
