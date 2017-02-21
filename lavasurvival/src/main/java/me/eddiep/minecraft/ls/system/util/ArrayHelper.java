package me.eddiep.minecraft.ls.system.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A utility class for doing basic things with arrays
 */
public class ArrayHelper {
    /**
     * Checks to see if a condition is true for every element in an array. If any item in the array does not meet
     * the condition, then an {@link IllegalStateException} will be thrown
     * @param collection    The collection to check
     * @param func          The condition
     * @param failedMessage What the exception should say when it's false
     * @param <T>           The array type
     */
    public static <T> void assertTrueFor(T[] collection, PFunction<T, Boolean> func, String failedMessage) {
        for (T temp : collection)
            try {
                if (!func.run(temp))
                    throw new IllegalStateException(failedMessage);
            } catch (Throwable t) {
                throw new IllegalStateException(failedMessage, t);
            }
    }

    /**
     * Execute a function for each item in this array
     * @param collection The array to iterate over
     * @param func       The function to execute
     * @param <T>        The type of this array
     */
    public static <T> void forEach(T[] collection, PRunnable<T> func) {
        for (T temp : collection)
            func.run(temp);
    }

    public static <T, R> List<R> transform(List<T> original, PFunction<T, R> func) {
        return original.stream().map(func::run).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T, R> R[] transform(T[] original, PFunction<T, R> func) {
        R[] newArray = (R[]) Array.newInstance(original.getClass().getComponentType(), original.length);

        for (int i = 0; i < original.length; i++) {
            R newItem = func.run(original[i]);
            newArray[i] = newItem;
        }

        return newArray;
    }

    /**
     * Combine the contents of two arrays and return the result. Array B will be appended onto array A
     * @param a   The first array
     * @param b   The second array
     * @param <T> The types of these arrays
     * @return An array with the contents of both A and B
     */
    public static <T> T[] combine(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public static <T> List<T> combind(List<T> a, List<T> b) {
        b.addAll(a);
        return b;
    }

    /**
     * Checks to see if this array contains an object. This method uses the {@link Object#equals(Object)} function and the
     * {@link Object#hashCode()} function to compare
     * @param a   The array to check
     * @param obj The object that should be inside the array
     * @param <T> The type of this array
     * @return True if the item is inside the array, false otherwise
     */
    public static <T> boolean contains(T[] a, T obj) {
        return Arrays.stream(a).anyMatch(item -> item.equals(obj) || item.hashCode() == obj.hashCode());
    }

    public static <T> String toString(ArrayList<T> arrayList) {
        StringBuilder builder = new StringBuilder();
        for (T d : arrayList) {
            builder.append(d);
            builder.append("\t");
        }

        return builder.toString();
    }

    public static <T> T random(T[] list) {
        return list[RandomHelper.random(list.length)];
    }

    public static <T> T random(List<T> list) {
        return list.get(RandomHelper.random(list.size()));
    }
}