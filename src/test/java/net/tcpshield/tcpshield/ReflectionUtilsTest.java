package net.tcpshield.tcpshield;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReflectionUtilsTest {

    private static final String MODIFIED = "modified";
    private static final String ORIGINAL = "original";
    private ReflectionUtilsTestObject testObject;

    @BeforeEach
    public void resetTestObject() {
        testObject = new ReflectionUtilsTestObject(ORIGINAL);
    }

    @Test
    public void testSetPublicField() throws NoSuchFieldException, IllegalAccessException {
        ReflectionUtils.setField(testObject, "publicString", MODIFIED);
        assertEquals(MODIFIED, testObject.getPublicString());
    }

    @Test
    public void testSetPrivateField() throws NoSuchFieldException, IllegalAccessException {
        ReflectionUtils.setField(testObject, "privateString", MODIFIED);
        assertEquals(MODIFIED, testObject.getPrivateString());
    }

    @Test
    public void testSetPublicFinalField() throws NoSuchFieldException, IllegalAccessException {
        ReflectionUtils.setFinalField(testObject, "publicFinalString", MODIFIED);
        assertEquals(MODIFIED, testObject.getPublicFinalString());
    }

    @Test
    public void testSetPrivateFinalField() throws NoSuchFieldException, IllegalAccessException {
        ReflectionUtils.setFinalField(testObject, "privateFinalString", MODIFIED);
        assertEquals(MODIFIED, testObject.getPrivateFinalString());
    }

    @Test
    public void testSearchFieldByClass() {
        Field field = ReflectionUtils.searchFieldByClass(ReflectionUtilsTest.class, ReflectionUtilsTestObject.class);
        assertEquals("testObject", field.getName());
    }

    @Test
    public void testSearchFieldByNonExistentClass() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ReflectionUtils.searchFieldByClass(ReflectionUtilsTest.class, byte.class));
    }

    @Test
    public void testGetObjectInPrivateField() throws NoSuchFieldException, IllegalAccessException {
        String privateField = (String) ReflectionUtils.getObjectInPrivateField(testObject, "privateString");
        assertEquals(ORIGINAL, privateField);
    }

    public static class ReflectionUtilsTestObject {

        public final String publicFinalString;
        private final String privateFinalString;
        public String publicString;
        private String privateString;

        public ReflectionUtilsTestObject(String str) {
            this.publicFinalString = str;
            this.privateFinalString = str;
            this.publicString = str;
            this.privateString = str;
        }

        public String getPublicFinalString() {
            return publicFinalString;
        }

        public String getPrivateFinalString() {
            return privateFinalString;
        }

        public String getPublicString() {
            return publicString;
        }

        public void setPublicString(String publicString) {
            this.publicString = publicString;
        }

        public String getPrivateString() {
            return privateString;
        }

        public void setPrivateString(String privateString) {
            this.privateString = privateString;
        }
    }

}
