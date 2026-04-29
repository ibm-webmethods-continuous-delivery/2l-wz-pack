package com.ibm.tel.wm.pipelinelogger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wm.data.IData;
import com.wm.data.IDataCursor;

/**
 * JSON simple serializer for production environments.
 * Produces single-line JSON output with key-value pairs only (no type information).
 * Simplified format for easier parsing and analysis.
 */
public class JsonSimpleSerializer implements PipelineSerializer {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String serialize(String serviceNS, long duration, IData inboundPipeline, IData outboundPipeline) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("service", serviceNS);
            root.put("durationMillis", duration);
            root.set("inputPipeline", serializeIDataSimple(inboundPipeline));
            root.set("outputPipeline", serializeIDataSimple(outboundPipeline));
            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            return "{\"error\":\"Failed to serialize pipeline: " + e.getMessage() + "\"}";
        }
    }

    @Override
    public boolean isEnabled() {
        return Config.INSTANCE.isJsonSimpleSerializerEnabled();
    }

    /**
     * Enhanced password protection: checks if key contains "password" (case-insensitive)
     */
    private static boolean isPasswordField(String key) {
        return key != null && key.toLowerCase().contains("password");
    }

    private ObjectNode serializeIDataSimple(IData data) {
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

            node.set(key, serializeValueSimple(key, val));
        }
        cursor.destroy();
        return node;
    }

    private com.fasterxml.jackson.databind.JsonNode serializeValueSimple(String key, Object val) {
        if (val == null) {
            return mapper.nullNode();
        } else if (val instanceof java.util.Date) {
            return mapper.getNodeFactory().textNode(((java.util.Date) val).toInstant().toString());
        } else if (val instanceof String[][]) {
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
            return arrayNode;
        } else if (val instanceof String[]) {
            ArrayNode arrayNode = mapper.createArrayNode();
            String[] sa = (String[]) val;
            for (String s : sa) {
                String value = isPasswordField(key) ? "*" : s;
                arrayNode.add(value);
            }
            return arrayNode;
        } else if (val instanceof IData[]) {
            ArrayNode arrayNode = mapper.createArrayNode();
            IData[] ida = (IData[]) val;
            for (IData iData : ida) {
                arrayNode.add(serializeIDataSimple(iData));
            }
            return arrayNode;
        } else if (val instanceof IData) {
            return serializeIDataSimple((IData) val);
        } else if (val instanceof com.wm.util.coder.IDataCodable[]) {
            ArrayNode arrayNode = mapper.createArrayNode();
            com.wm.util.coder.IDataCodable[] ida = (com.wm.util.coder.IDataCodable[]) val;
            for (com.wm.util.coder.IDataCodable codable : ida) {
                arrayNode.add(serializeIDataSimple(codable.getIData()));
            }
            return arrayNode;
        } else if (val instanceof byte[]) {
            return mapper.getNodeFactory().textNode("*");
        } else if (val.getClass().isArray()) {
            ArrayNode arrayNode = mapper.createArrayNode();
            Object[] oa = (Object[]) val;
            for (Object o : oa) {
                if (o == null) {
                    arrayNode.addNull();
                } else {
                    String value = isPasswordField(key) ? "*" : o.toString();
                    arrayNode.add(value);
                }
            }
            return arrayNode;
        } else {
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
            return mapper.getNodeFactory().textNode(value);
        }
    }
}
