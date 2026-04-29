package com.ibm.tel.wm.pipelinelogger;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

/**
 * Verbose serializer for local development and debugging.
 * Produces multi-line, indented output with full type information.
 */
public class VerboseSerializer implements PipelineSerializer {

    private static final String INDENT_SPACE = "+ ";
    /* Example for advanced debugging using reflection
    private static final String REFLECTION_DEBUG_SERVICE = "wm.server:ping";
    private static final int REFLECTION_MAX_DEPTH = 8;
    */

    @Override
    public String serialize(String serviceNS, long duration, IData inboundPipeline, IData outboundPipeline) {
        java.io.StringWriter sw = new java.io.StringWriter();
        sw.write("Service call");
        sw.write("\nService         : " + serviceNS);
        sw.write("\nDuration millis : " + duration);
        sw.write("\n== Input  Pipeline ==============================");
        writeLogTrace("inputPipeline", inboundPipeline, 0, sw, true);
        sw.write("\n== Output Pipeline ==============================");
        writeLogTrace("outputPipeline", outboundPipeline, 0, sw, true);
        /* Example for advanced debugging using reflection
        if (REFLECTION_DEBUG_SERVICE.equals(serviceNS)) {
            sw.write("\n== Output Pipeline Reflection Debug =============");
            writeReflectionDump("outputPipeline", outboundPipeline, 0, sw, new IdentityHashMap<Object, Boolean>());
            IDataCursor c  = outboundPipeline.getCursor();
            writeReflectionDump("outputPipelinecursor", c, 0, sw, new IdentityHashMap<Object, Boolean>());
            c.destroy();
        }
        */
        return sw.toString();
    }

    @Override
    public boolean isEnabled() {
        return Config.INSTANCE.isVerboseSerializerEnabled();
    }

    /**
     * Enhanced password protection: checks if key contains "password" (case-insensitive)
     */
    private static boolean isPasswordField(String key) {
        return key != null && key.toLowerCase().contains("password");
    }

    private static void writeKeyPart(String sType, String sKey, int indent, java.io.StringWriter out, boolean bAddl) {
        out.write("\n");
        if (bAddl)
            out.write(INDENT_SPACE);
        for (int t = 0; t < indent; t++)
            out.write(INDENT_SPACE);
        out.write("{" + sType + "}");
        out.write(" ");
        out.write(sKey);
    }

    /* in case of deep debugging with reflection
    private static void writeIndentedLine(java.io.StringWriter out, int indent, String line) {
        out.write("\n");
        out.write(INDENT_SPACE);
        for (int t = 0; t < indent; t++)
            out.write(INDENT_SPACE);
        out.write(line);
    }

    private static boolean isLeafType(Class<?> type) {
        return type.isPrimitive()
                || Number.class.isAssignableFrom(type)
                || CharSequence.class.isAssignableFrom(type)
                || Boolean.class == type
                || Character.class == type
                || java.util.Date.class.isAssignableFrom(type)
                || Enum.class.isAssignableFrom(type);
    }

    private static String safeToString(Object value) {
        if (value == null) {
            return "null";
        }
        try {
            return String.valueOf(value);
        } catch (Throwable t) {
            return "<toString() failed: " + t.getClass().getName() + ": " + t.getMessage() + ">";
        }
    }
    */

    /* Example for advanced debugging using reflection
    private static void writeReflectionDump(String name, Object value, int indent, java.io.StringWriter out,
            IdentityHashMap<Object, Boolean> visited) {
        if (value == null) {
            writeIndentedLine(out, indent, name + " = null");
            return;
        }

        Class<?> type = value.getClass();
        writeIndentedLine(out, indent, name + " {" + type.getCanonicalName() + "}");

        if (isLeafType(type)) {
            writeIndentedLine(out, indent + 1, "= " + safeToString(value));
            return;
        }

        if (visited.containsKey(value)) {
            writeIndentedLine(out, indent + 1, "<cycle detected>");
            return;
        }

        visited.put(value, Boolean.TRUE);

        if (type.isArray()) {
            int length = Array.getLength(value);
            writeIndentedLine(out, indent + 1, "length = " + length);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(value, i);
                writeReflectionDump("[" + i + "]", element, indent + 1, out, visited);
            }
            return;
        }

        if (value instanceof java.util.Map<?, ?>) {
            java.util.Map<?, ?> map = (java.util.Map<?, ?>) value;
            writeIndentedLine(out, indent + 1, "size = " + map.size());
            for (java.util.Map.Entry<?, ?> entry : map.entrySet()) {
                writeReflectionDump("key", entry.getKey(), indent + 2, out, visited);
                writeReflectionDump("value", entry.getValue(), indent + 2, out, visited);
            }
            return;
        }

        if (value instanceof java.lang.Iterable<?>) {
            int idx = 0;
            for (Object element : (java.lang.Iterable<?>) value) {
                writeReflectionDump("[" + idx + "]", element, indent + 1, out, visited);
                idx++;
            }
            if (idx == 0) {
                writeIndentedLine(out, indent + 1, "<empty iterable>");
            }
            return;
        }

        if (indent >= REFLECTION_MAX_DEPTH) {
            writeIndentedLine(out, indent + 1, "<max depth reached> " + safeToString(value));
            return;
        }

        boolean anyField = false;
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            Field[] fields = current.getDeclaredFields();
            if (fields.length > 0) {
                writeIndentedLine(out, indent + 1, "declared in " + current.getCanonicalName());
            }
            for (Field field : fields) {
                anyField = true;
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(value);
                    String modifiers = Modifier.toString(field.getModifiers());
                    String typeName = field.getType().getCanonicalName();
                    String fieldName = ((modifiers == null || modifiers.length() == 0) ? "" : modifiers + " ")
                            + typeName + " " + field.getName();

                    if (isPasswordField(field.getName())) {
                        writeIndentedLine(out, indent + 2, fieldName + " = *");
                    } else if (fieldValue == null || isLeafType(field.getType())) {
                        writeIndentedLine(out, indent + 2, fieldName + " = " + safeToString(fieldValue));
                    } else {
                        writeIndentedLine(out, indent + 2, fieldName);
                        writeReflectionDump(field.getName(), fieldValue, indent + 3, out, visited);
                    }
                } catch (Throwable t) {
                    writeIndentedLine(out, indent + 2,
                            field.getName() + " = <inaccessible: " + t.getClass().getName() + ": " + t.getMessage() + ">");
                }
            }
        }

        if (!anyField) {
            writeIndentedLine(out, indent + 1, "<no declared fields> " + safeToString(value));
        }
    }
    */

    // There are a few exceptions to the ground rules
    private static IData unpackIDataObject(Object in, int indent, java.io.StringWriter out){
        // unfortunately the usual expectations from an interface object are not met, dealing with some exceptions here
        if(in instanceof com.wm.util.coder.IDataCodable){
            out.write("\n");
            for (int t = 0; t < indent; t++)
                out.write(INDENT_SPACE);
            out.write("{ {com.wm.util.coder.IDataCodable Particular Case} " + in.getClass().getCanonicalName() + "} ->");
            out.write(" ");
            return unpackIDataObject(((com.wm.util.coder.IDataCodable) in).getIData(),indent, out);
        }else if (in instanceof IData){
            return (IData) in;
        }
        return null;
    }

    private static void writeLogTrace(String myKey, Object in, int indent, java.io.StringWriter out, boolean bRoot) {
        IData inIData = unpackIDataObject(in, indent, out);
        if (null==inIData){
            out.write("Unexpected object type, trying toString() -> ");
            try {
                out.write(in.toString());
            } catch (Throwable t) {
                out.write("Unexpected object type, toString() failed -> " + t.getMessage());
            }
            return;
        }

        writeKeyPart(inIData.getClass().getCanonicalName(), myKey, indent, out, false);

        IDataCursor idc = inIData.getCursor();
        for (boolean ok = idc.first(); ok; ok = idc.next()) {
            String key = (String) idc.getKey();
            Object val = idc.getValue();
            if (null == val) {
                out.write("\n");
                out.write(INDENT_SPACE);
                for (int t = 0; t < indent; t++)
                    out.write(INDENT_SPACE);
                out.write("(null) " + key);
                continue;
            }
            if (val instanceof java.util.Date){
                out.write("\n");
                out.write(INDENT_SPACE);
                for (int t = 0; t < indent; t++)
                    out.write(INDENT_SPACE);
                out.write("{" + val.getClass().getCanonicalName() + "} " + key + " = ");
                out.write(((java.util.Date) val).toInstant().toString());
            } else if (val instanceof String[][]) {
                writeKeyPart("{java.lang.String[][]}", key, indent, out, true);
                String[][] st = (String[][]) val;
                for (int k = 0; k < st.length; k++) {
                    for (int j = 0; j < st[0].length; j++) {
                        if (isPasswordField(key))
                            st[k][j] = "*";
                        out.write("\n");
                        out.write(INDENT_SPACE);
                        out.write(INDENT_SPACE);
                        for (int t = 0; t < indent; t++)
                            out.write(INDENT_SPACE);
                        out.write("[" + k + "][" + j + "] = " + st[k][j]);
                    }
                }
            } else if (val instanceof String[]) {
                writeKeyPart("{java.lang.String[]}", key, indent, out, true);

                String[] sa = (String[]) val;
                for (int k = 0; k < sa.length; k++) {
                    if (isPasswordField(key))
                        sa[k] = "*";
                    out.write("\n");
                    out.write(INDENT_SPACE);
                    out.write(INDENT_SPACE);
                    for (int t = 0; t < indent; t++)
                        out.write(INDENT_SPACE);
                    out.write("[" + k + "] = " + sa[k]);
                }
            } else if (val instanceof IData[]) {
                writeKeyPart("{"+val.getClass().getCanonicalName()+"[]}", key, indent, out, true);

                IData[] ida = (IData[]) val;
                for (int l = 0; l < ida.length; l++) {
                    writeLogTrace("[" + l + "]", ida[l], indent + 2, out, false);
                }
            } else if ( val instanceof IData) {
                writeLogTrace(key, val, indent + 1, out, false);
            } else if (val instanceof com.wm.util.coder.IDataCodable[]) {
                com.wm.util.coder.IDataCodable[] ida = (com.wm.util.coder.IDataCodable[]) val;
                for (int l = 0; l < ida.length; l++) {
                    writeLogTrace(key, ida[l].getIData(), indent + 1, out, false);
                }
            } else if (val instanceof byte[]) {
                writeKeyPart("{byte[]}", key, indent, out, true);
                out.write(" = *");
            } else if (val.getClass().isArray()) {
                writeKeyPart("{"+val.getClass().getCanonicalName()+"[]}", key, indent, out, true);

                Object[] oa = (Object[]) val;
                for (int k = 0; k < oa.length; k++) {
                    out.write("\n");
                    out.write(INDENT_SPACE);
                    out.write(INDENT_SPACE);
                    for (int t = 0; t < indent; t++)
                        out.write(INDENT_SPACE);
                    if (isPasswordField(key))
                        oa[k] = "*";
                    if (null == oa[k]) {
                        out.write("[" + k + "](null)");
                    } else {
                        out.write("{" + oa[k].getClass().getCanonicalName()
                                + "}[" + k + "] = "
                                + oa[k].toString());
                    }
                }
            } else {
                out.write("\n");
                out.write(INDENT_SPACE);
                for (int t = 0; t < indent; t++)
                    out.write(INDENT_SPACE);
                if (isPasswordField(key))
                    val = "*";
                out.write("{" + val.getClass().getCanonicalName() + "} " + key + " = ");
                String sVal = "";
                try {
                    sVal = val.toString();
                } catch(Throwable t) {
                    sVal = "Serialization error: " + t.getMessage();
                }
                out.write(sVal);
            }
        }
        idc.destroy();
    }
}
