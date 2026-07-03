package com.ibm.tel.wm.pipelinelogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wm.data.IData;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JsonCompactSerializer.
 * Tests JSON serialization with type information, password masking, and error handling.
 */
public class JsonCompactSerializerTest {
    
    private JsonCompactSerializer serializer;
    private IData testPipeline;
    private ObjectMapper mapper;
    
    @BeforeEach
    public void setUp() {
        serializer = new JsonCompactSerializer();
        testPipeline = IDataFactory.create();
        mapper = new ObjectMapper();
    }
    
    @Test
    public void testIsEnabled_WhenConfigEnabled_ReturnsTrue() {
        Config.INSTANCE.setJsonCompactSerializerEnabled(true);
        assertTrue(serializer.isEnabled());
    }
    
    @Test
    public void testIsEnabled_WhenConfigDisabled_ReturnsFalse() {
        Config.INSTANCE.setJsonCompactSerializerEnabled(false);
        assertFalse(serializer.isEnabled());
    }
    
    @Test
    public void testSerialize_ProducesValidJson() throws Exception {
        IDataUtil.put(testPipeline.getCursor(), "field", "value");
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        // Should be valid JSON
        JsonNode json = mapper.readTree(result);
        assertNotNull(json);
        assertTrue(json.isObject());
    }
    
    @Test
    public void testSerialize_ContainsServiceAndDuration() throws Exception {
        String result = serializer.serialize("com.example.MyService", 12345L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        assertEquals("com.example.MyService", json.get("service").asText());
        assertEquals(12345L, json.get("durationMillis").asLong());
    }
    
    @Test
    public void testSerialize_WithNullValue_IncludesTypeAndNull() throws Exception {
        IDataUtil.put(testPipeline.getCursor(), "nullField", null);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        JsonNode nullField = inputPipeline.get("nullField");
        
        assertNotNull(nullField);
        assertEquals("null", nullField.get("type").asText());
        assertTrue(nullField.get("value").isNull());
    }
    
    @Test
    public void testSerialize_WithStringValue_IncludesTypeAndValue() throws Exception {
        IDataUtil.put(testPipeline.getCursor(), "username", "john.doe");
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        JsonNode username = inputPipeline.get("username");
        
        assertNotNull(username);
        assertEquals("java.lang.String", username.get("type").asText());
        assertEquals("john.doe", username.get("value").asText());
    }
    
    @Test
    public void testSerialize_WithPasswordField_MasksValue() throws Exception {
        IDataUtil.put(testPipeline.getCursor(), "password", "secret123");
        IDataUtil.put(testPipeline.getCursor(), "userPassword", "secret456");
        IDataUtil.put(testPipeline.getCursor(), "dbPassword", "secret789");
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        
        assertEquals("*", inputPipeline.get("password").get("value").asText());
        assertEquals("*", inputPipeline.get("userPassword").get("value").asText());
        assertEquals("*", inputPipeline.get("dbPassword").get("value").asText());
        
        assertFalse(result.contains("secret123"));
        assertFalse(result.contains("secret456"));
        assertFalse(result.contains("secret789"));
    }
    
    @Test
    public void testSerialize_WithStringArray_SerializesAsArray() throws Exception {
        String[] values = {"value1", "value2", "value3"};
        IDataUtil.put(testPipeline.getCursor(), "stringArray", values);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        JsonNode stringArray = inputPipeline.get("stringArray");
        
        assertNotNull(stringArray);
        assertEquals("java.lang.String[]", stringArray.get("type").asText());
        
        JsonNode arrayValue = stringArray.get("value");
        assertTrue(arrayValue.isArray());
        assertEquals(3, arrayValue.size());
        assertEquals("value1", arrayValue.get(0).asText());
        assertEquals("value2", arrayValue.get(1).asText());
        assertEquals("value3", arrayValue.get(2).asText());
    }
    
    @Test
    public void testSerialize_WithPasswordArray_MasksAllValues() throws Exception {
        String[] passwords = {"pass1", "pass2", "pass3"};
        IDataUtil.put(testPipeline.getCursor(), "passwords", passwords);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        JsonNode passwordsArray = inputPipeline.get("passwords");
        
        JsonNode arrayValue = passwordsArray.get("value");
        assertTrue(arrayValue.isArray());
        assertEquals(3, arrayValue.size());
        
        for (int i = 0; i < 3; i++) {
            assertEquals("*", arrayValue.get(i).asText());
        }
        
        assertFalse(result.contains("pass1"));
        assertFalse(result.contains("pass2"));
        assertFalse(result.contains("pass3"));
    }
    
    @Test
    public void testSerialize_With2DStringArray_SerializesCorrectly() throws Exception {
        String[][] matrix = {
            {"a1", "a2"},
            {"b1", "b2"}
        };
        IDataUtil.put(testPipeline.getCursor(), "matrix", matrix);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        JsonNode matrixNode = inputPipeline.get("matrix");
        
        assertEquals("java.lang.String[][]", matrixNode.get("type").asText());
        
        JsonNode arrayValue = matrixNode.get("value");
        assertTrue(arrayValue.isArray());
        assertEquals(2, arrayValue.size());
        
        JsonNode row0 = arrayValue.get(0);
        assertTrue(row0.isArray());
        assertEquals("a1", row0.get(0).asText());
        assertEquals("a2", row0.get(1).asText());
        
        JsonNode row1 = arrayValue.get(1);
        assertTrue(row1.isArray());
        assertEquals("b1", row1.get(0).asText());
        assertEquals("b2", row1.get(1).asText());
    }
    
    @Test
    public void testSerialize_WithNestedIData_SerializesRecursively() throws Exception {
        IData nested = IDataFactory.create();
        IDataUtil.put(nested.getCursor(), "nestedField", "nestedValue");
        IDataUtil.put(testPipeline.getCursor(), "parent", nested);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        JsonNode parent = inputPipeline.get("parent");
        
        assertEquals("IData", parent.get("type").asText());
        
        JsonNode parentValue = parent.get("value");
        assertTrue(parentValue.isObject());
        
        JsonNode nestedField = parentValue.get("nestedField");
        assertEquals("java.lang.String", nestedField.get("type").asText());
        assertEquals("nestedValue", nestedField.get("value").asText());
    }
    
    @Test
    public void testSerialize_WithIDataArray_SerializesArray() throws Exception {
        IData item1 = IDataFactory.create();
        IDataUtil.put(item1.getCursor(), "id", "1");
        
        IData item2 = IDataFactory.create();
        IDataUtil.put(item2.getCursor(), "id", "2");
        
        IData[] array = {item1, item2};
        IDataUtil.put(testPipeline.getCursor(), "items", array);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        JsonNode items = inputPipeline.get("items");
        
        assertEquals("IData[]", items.get("type").asText());
        
        JsonNode arrayValue = items.get("value");
        assertTrue(arrayValue.isArray());
        assertEquals(2, arrayValue.size());
    }
    
    @Test
    public void testSerialize_WithByteArray_MasksValue() throws Exception {
        byte[] bytes = {1, 2, 3, 4, 5};
        IDataUtil.put(testPipeline.getCursor(), "binaryData", bytes);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        JsonNode binaryData = inputPipeline.get("binaryData");
        
        assertEquals("byte[]", binaryData.get("type").asText());
        assertEquals("*", binaryData.get("value").asText());
    }
    
    @Test
    public void testSerialize_WithDateValue_FormatsAsISO8601() throws Exception {
        Date now = new Date();
        IDataUtil.put(testPipeline.getCursor(), "timestamp", now);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        JsonNode timestamp = inputPipeline.get("timestamp");
        
        assertEquals("java.util.Date", timestamp.get("type").asText());
        String dateValue = timestamp.get("value").asText();
        
        // Should be ISO 8601 format
        assertTrue(dateValue.contains("T"));
        assertTrue(dateValue.contains("Z"));
    }
    
    @Test
    public void testSerialize_WithObjectArray_SerializesWithTypes() throws Exception {
        Object[] objects = {
            "string",
            Integer.valueOf(42),
            null
        };
        IDataUtil.put(testPipeline.getCursor(), "mixedArray", objects);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        JsonNode mixedArray = inputPipeline.get("mixedArray");
        
        assertEquals("java.lang.Object[]", mixedArray.get("type").asText());
        
        JsonNode arrayValue = mixedArray.get("value");
        assertTrue(arrayValue.isArray());
        assertEquals(3, arrayValue.size());
        
        JsonNode item0 = arrayValue.get(0);
        assertEquals("java.lang.String", item0.get("type").asText());
        assertEquals("string", item0.get("value").asText());
        
        JsonNode item1 = arrayValue.get(1);
        assertEquals("java.lang.Integer", item1.get("type").asText());
        assertEquals("42", item1.get("value").asText());
        
        JsonNode item2 = arrayValue.get(2);
        assertTrue(item2.isNull());
    }
    
    @Test
    public void testSerialize_WithBothPipelines_SerializesBoth() throws Exception {
        IData inbound = IDataFactory.create();
        IDataUtil.put(inbound.getCursor(), "input", "inputValue");
        
        IData outbound = IDataFactory.create();
        IDataUtil.put(outbound.getCursor(), "output", "outputValue");
        
        String result = serializer.serialize("test.service", 100L, inbound, outbound);
        
        JsonNode json = mapper.readTree(result);
        
        JsonNode inputPipeline = json.get("inputPipeline");
        assertNotNull(inputPipeline);
        assertEquals("inputValue", inputPipeline.get("input").get("value").asText());
        
        JsonNode outputPipeline = json.get("outputPipeline");
        assertNotNull(outputPipeline);
        assertEquals("outputValue", outputPipeline.get("output").get("value").asText());
    }
    
    @Test
    public void testSerialize_WithEmptyPipeline_ProducesValidJson() throws Exception {
        IData empty = IDataFactory.create();
        
        String result = serializer.serialize("test.service", 100L, empty, empty);
        
        JsonNode json = mapper.readTree(result);
        assertNotNull(json);
        assertEquals("test.service", json.get("service").asText());
        assertEquals(100L, json.get("durationMillis").asLong());
        
        JsonNode inputPipeline = json.get("inputPipeline");
        assertNotNull(inputPipeline);
        assertTrue(inputPipeline.isObject());
        
        JsonNode outputPipeline = json.get("outputPipeline");
        assertNotNull(outputPipeline);
        assertTrue(outputPipeline.isObject());
    }
    
    @Test
    public void testSerialize_IsSingleLine() {
        IDataUtil.put(testPipeline.getCursor(), "field1", "value1");
        IDataUtil.put(testPipeline.getCursor(), "field2", "value2");
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        // Should be single line (no newlines except possibly at the end)
        String trimmed = result.trim();
        assertFalse(trimmed.contains("\n"));
    }
    
    @Test
    public void testPasswordFieldDetection_CaseInsensitive() throws Exception {
        IDataUtil.put(testPipeline.getCursor(), "PASSWORD", "secret1");
        IDataUtil.put(testPipeline.getCursor(), "Password", "secret2");
        IDataUtil.put(testPipeline.getCursor(), "pAsSwOrD", "secret3");
        IDataUtil.put(testPipeline.getCursor(), "myPasswordField", "secret4");
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertFalse(result.contains("secret1"));
        assertFalse(result.contains("secret2"));
        assertFalse(result.contains("secret3"));
        assertFalse(result.contains("secret4"));
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        
        assertEquals("*", inputPipeline.get("PASSWORD").get("value").asText());
        assertEquals("*", inputPipeline.get("Password").get("value").asText());
        assertEquals("*", inputPipeline.get("pAsSwOrD").get("value").asText());
        assertEquals("*", inputPipeline.get("myPasswordField").get("value").asText());
    }
    
    @Test
    public void testSerialize_WithSerializationError_HandlesGracefully() throws Exception {
        // Create an object that might throw on toString()
        Object problematic = new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("Serialization failed");
            }
        };
        
        IDataUtil.put(testPipeline.getCursor(), "problematic", problematic);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        JsonNode json = mapper.readTree(result);
        JsonNode inputPipeline = json.get("inputPipeline");
        JsonNode problematicNode = inputPipeline.get("problematic");
        
        String value = problematicNode.get("value").asText();
        assertTrue(value.contains("Serialization error"));
    }
}
