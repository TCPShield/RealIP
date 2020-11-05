package net.tcpshield.tcpshield;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.tcpshield.tcpshield.exception.TCPShieldInitializationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ReflectionUtils {

    private static final Table<Class<?>, String, Field> CACHED_FIELDS_BY_NAME = HashBasedTable.create();
    private static final Table<Class<?>, Class<?>, Field> CACHED_FIELDS_BY_CLASS = HashBasedTable.create();
    private static final Field MODIFIERS_FIELD;

    static {
        Field tempModifiersField;
        try {
            tempModifiersField = getDeclaredField(Field.class, "modifiers");
        } catch (NoSuchFieldException e) { // workaround for when searching for the modifiers field on Java 12 or higher
            try {
                Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
                getDeclaredFields0.setAccessible(true);

                Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
                tempModifiersField = Arrays.stream(fields).filter(field -> field.getName().equals("modifiers")).findFirst().orElseThrow(() -> new TCPShieldInitializationException("Could not find the modifiers field"));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e2) {
                throw new TCPShieldInitializationException(e2);
            }
        }

        MODIFIERS_FIELD = tempModifiersField;
        MODIFIERS_FIELD.setAccessible(true);
    }

    public static void setFinalField(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        setFinalField(object, getPrivateField(object.getClass(), fieldName), value);
    }

    public static void setFinalField(Object object, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        if (Modifier.isFinal(field.getModifiers())) {
            MODIFIERS_FIELD.setInt(field, field.getModifiers() & ~Modifier.FINAL);
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
        Field cachedField = CACHED_FIELDS_BY_CLASS.get(clazz, searchFor);
        if (cachedField != null) return cachedField;

        Class<?> currentClass = clazz;
        do { 
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.getType() != searchFor) continue;

                CACHED_FIELDS_BY_CLASS.put(clazz, searchFor, field);
                return field;
            }

            currentClass = currentClass.getSuperclass();
        } while (currentClass != null);

        throw new IllegalArgumentException("no " + searchFor.getName() + " field for clazz = " + clazz.getName() + " found");
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field cachedField = CACHED_FIELDS_BY_NAME.get(clazz, fieldName);
        if (cachedField != null) return cachedField;

        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return getDeclaredField(superclass, fieldName);
            } else {
                throw e;
            }
        }

        CACHED_FIELDS_BY_NAME.put(clazz, fieldName, field);
        return field;
    }
}