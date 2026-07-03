package com.ibm.tel.wm.pipelinelogger;

import com.wm.data.IData;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for VerboseSerializer.
 * Tests serialization of various data types, password masking, and error handling.
 */
public class VerboseSerializerTest {
    
    private VerboseSerializer serializer;
    private IData testPipeline;
    
    @BeforeEach
    public void setUp() {
        serializer = new VerboseSerializer();
        testPipeline = IDataFactory.create();
    }
    
    @Test
    public void testIsEnabled_WhenConfigEnabled_ReturnsTrue() {
        Config.INSTANCE.setVerboseSerializerEnabled(true);
        assertTrue(serializer.isEnabled());
    }
    
    @Test
    public void testIsEnabled_WhenConfigDisabled_ReturnsFalse() {
        Config.INSTANCE.setVerboseSerializerEnabled(false);
        assertFalse(serializer.isEnabled());
    }
    
    @Test
    public void testSerialize_WithNullValue_HandlesGracefully() {
        IDataUtil.put(testPipeline.getCursor(), "nullField", null);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertTrue(result.contains("(null) nullField"));
        assertTrue(result.contains("test.service"));
        assertTrue(result.contains("100"));
    }
    
    @Test
    public void testSerialize_WithStringValue_SerializesCorrectly() {
        IDataUtil.put(testPipeline.getCursor(), "username", "john.doe");
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertTrue(result.contains("username"));
        assertTrue(result.contains("john.doe"));
        assertTrue(result.contains("java.lang.String"));
    }
    
    @Test
    public void testSerialize_WithPasswordField_MasksValue() {
        IDataUtil.put(testPipeline.getCursor(), "password", "secret123");
        IDataUtil.put(testPipeline.getCursor(), "userPassword", "secret456");
        IDataUtil.put(testPipeline.getCursor(), "dbPassword", "secret789");
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertFalse(result.contains("secret123"));
        assertFalse(result.contains("secret456"));
        assertFalse(result.contains("secret789"));
        assertTrue(result.contains("password"));
        assertTrue(result.contains("*"));
    }
    
    @Test
    public void testSerialize_WithStringArray_SerializesCorrectly() {
        String[] values = {"value1", "value2", "value3"};
        IDataUtil.put(testPipeline.getCursor(), "stringArray", values);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertTrue(result.contains("stringArray"));
        assertTrue(result.contains("value1"));
        assertTrue(result.contains("value2"));
        assertTrue(result.contains("value3"));
        assertTrue(result.contains("[0]"));
        assertTrue(result.contains("[1]"));
        assertTrue(result.contains("[2]"));
    }
    
    @Test
    public void testSerialize_WithPasswordArray_MasksAllValues() {
        String[] passwords = {"pass1", "pass2", "pass3"};
        IDataUtil.put(testPipeline.getCursor(), "passwords", passwords);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertFalse(result.contains("pass1"));
        assertFalse(result.contains("pass2"));
        assertFalse(result.contains("pass3"));
        assertTrue(result.contains("passwords"));
        assertTrue(result.contains("*"));
    }
    
    @Test
    public void testSerialize_With2DStringArray_SerializesCorrectly() {
        String[][] matrix = {
            {"a1", "a2"},
            {"b1", "b2"}
        };
        IDataUtil.put(testPipeline.getCursor(), "matrix", matrix);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertTrue(result.contains("matrix"));
        assertTrue(result.contains("a1"));
        assertTrue(result.contains("a2"));
        assertTrue(result.contains("b1"));
        assertTrue(result.contains("b2"));
        assertTrue(result.contains("[0][0]"));
        assertTrue(result.contains("[1][1]"));
    }
    
    @Test
    public void testSerialize_WithNestedIData_SerializesCorrectly() {
        IData nested = IDataFactory.create();
        IDataUtil.put(nested.getCursor(), "nestedField", "nestedValue");
        IDataUtil.put(testPipeline.getCursor(), "parent", nested);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertTrue(result.contains("parent"));
        assertTrue(result.contains("nestedField"));
        assertTrue(result.contains("nestedValue"));
    }
    
    @Test
    public void testSerialize_WithIDataArray_SerializesCorrectly() {
        IData item1 = IDataFactory.create();
        IDataUtil.put(item1.getCursor(), "id", "1");
        
        IData item2 = IDataFactory.create();
        IDataUtil.put(item2.getCursor(), "id", "2");
        
        IData[] array = {item1, item2};
        IDataUtil.put(testPipeline.getCursor(), "items", array);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertTrue(result.contains("items"));
        assertTrue(result.contains("[0]"));
        assertTrue(result.contains("[1]"));
    }
    
    @Test
    public void testSerialize_WithByteArray_MasksValue() {
        byte[] bytes = {1, 2, 3, 4, 5};
        IDataUtil.put(testPipeline.getCursor(), "binaryData", bytes);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertTrue(result.contains("binaryData"));
        assertTrue(result.contains("*"));
        assertTrue(result.contains("byte[]"));
    }
    
    @Test
    public void testSerialize_WithDateValue_FormatsAsISO8601() {
        Date now = new Date();
        IDataUtil.put(testPipeline.getCursor(), "timestamp", now);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertTrue(result.contains("timestamp"));
        assertTrue(result.contains("java.util.Date"));
        // Should contain ISO 8601 format (contains 'T' and 'Z')
        assertTrue(result.contains("T"));
        assertTrue(result.contains("Z"));
    }
    
    @Test
    public void testSerialize_WithObjectArray_SerializesCorrectly() {
        Object[] objects = {
            "string",
            Integer.valueOf(42),
            null
        };
        IDataUtil.put(testPipeline.getCursor(), "mixedArray", objects);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertTrue(result.contains("mixedArray"));
        assertTrue(result.contains("string"));
        assertTrue(result.contains("42"));
        assertTrue(result.contains("(null)"));
    }
    
    @Test
    public void testSerialize_WithComplexPipeline_HandlesAllTypes() {
        // Build a complex pipeline with multiple data types
        IDataUtil.put(testPipeline.getCursor(), "stringField", "value");
        IDataUtil.put(testPipeline.getCursor(), "password", "secret");
        IDataUtil.put(testPipeline.getCursor(), "nullField", null);
        IDataUtil.put(testPipeline.getCursor(), "numberField", Integer.valueOf(123));
        
        String[] array = {"a", "b", "c"};
        IDataUtil.put(testPipeline.getCursor(), "arrayField", array);
        
        IData nested = IDataFactory.create();
        IDataUtil.put(nested.getCursor(), "nestedValue", "nested");
        IDataUtil.put(testPipeline.getCursor(), "nestedDoc", nested);
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertTrue(result.contains("test.service"));
        assertTrue(result.contains("100"));
        assertTrue(result.contains("stringField"));
        assertTrue(result.contains("value"));
        assertTrue(result.contains("password"));
        assertFalse(result.contains("secret"));
        assertTrue(result.contains("*"));
        assertTrue(result.contains("(null) nullField"));
        assertTrue(result.contains("123"));
        assertTrue(result.contains("arrayField"));
        assertTrue(result.contains("nestedDoc"));
        assertTrue(result.contains("nestedValue"));
    }
    
    @Test
    public void testSerialize_WithBothPipelines_SerializesBoth() {
        IData inbound = IDataFactory.create();
        IDataUtil.put(inbound.getCursor(), "input", "inputValue");
        
        IData outbound = IDataFactory.create();
        IDataUtil.put(outbound.getCursor(), "output", "outputValue");
        
        String result = serializer.serialize("test.service", 100L, inbound, outbound);
        
        assertNotNull(result);
        assertTrue(result.contains("Input  Pipeline"));
        assertTrue(result.contains("Output Pipeline"));
        assertTrue(result.contains("input"));
        assertTrue(result.contains("inputValue"));
        assertTrue(result.contains("output"));
        assertTrue(result.contains("outputValue"));
    }
    
    @Test
    public void testSerialize_WithEmptyPipeline_HandlesGracefully() {
        IData empty = IDataFactory.create();
        
        String result = serializer.serialize("test.service", 100L, empty, empty);
        
        assertNotNull(result);
        assertTrue(result.contains("test.service"));
        assertTrue(result.contains("100"));
        assertTrue(result.contains("Input  Pipeline"));
        assertTrue(result.contains("Output Pipeline"));
    }
    
    @Test
    public void testPasswordFieldDetection_CaseInsensitive() {
        IDataUtil.put(testPipeline.getCursor(), "PASSWORD", "secret1");
        IDataUtil.put(testPipeline.getCursor(), "Password", "secret2");
        IDataUtil.put(testPipeline.getCursor(), "pAsSwOrD", "secret3");
        IDataUtil.put(testPipeline.getCursor(), "myPasswordField", "secret4");
        
        String result = serializer.serialize("test.service", 100L, testPipeline, IDataFactory.create());
        
        assertNotNull(result);
        assertFalse(result.contains("secret1"));
        assertFalse(result.contains("secret2"));
        assertFalse(result.contains("secret3"));
        assertFalse(result.contains("secret4"));
    }

}
