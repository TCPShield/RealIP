package net.tcpshield.tcpshield;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtils {

    private static final Table<Class<?>, String, Field> CACHED_FIELDS = HashBasedTable.create();

    public static void setFinalField(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        setFinalField(object, getPrivateField(object.getClass(), fieldName), value);
    }

    public static void setFinalField(Object object, Field field, Object value) throws IllegalAccessException, NoSuchFieldException {
        field.setAccessible(true);
        if (Modifier.isFinal(field.getModifiers())) {
            Field modifiersField = getDeclaredField(Field.class, "modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }

        setField(object, field, value);
    }

    public static void setField(Object object, String fieldName, Object value) throws IllegalAccessException, NoSuchFieldException {
        setField(object, getPrivateField(object.getClass(), fieldName), value);
    }

    public static void setField(Object object, Field field, Object value) throws IllegalAccessException {
        field.set(object, value);
    }

    public static Object getObjectInPrivateField(Object object, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field field = getPrivateField(object.getClass(), fieldName);
        return field.get(object);
    }

    public static Field getPrivateField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = getDeclaredField(clazz, fieldName);
        field.setAccessible(true);
        return field;
    }

    public static Field searchFieldByClass(Class<?> clazz, Class<?> searchFor) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getType() == searchFor) {
                return f;
            }
        }

        throw new IllegalArgumentException("no " + searchFor.getName() + " field for clazz = " + clazz.getName() + " found");
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field cachedField = CACHED_FIELDS.get(clazz, fieldName);
        if (cachedField != null) return cachedField;

        Field field = clazz.getDeclaredField(fieldName);
        CACHED_FIELDS.put(clazz, fieldName, field);
        return field;
    }

}