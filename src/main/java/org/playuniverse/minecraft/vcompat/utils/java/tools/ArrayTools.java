package org.playuniverse.minecraft.vcompat.utils.java.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public final class ArrayTools {

    private ArrayTools() {}
    
    public static List<String> toLowercaseList(String... values) {
        ArrayList<String> list = new ArrayList<>();
        for (String value : values) {
            list.add(value.toLowerCase());
        }
        return list;
    }

    public static Object filter(Object[] array, String name) {
        for (Object object : array) {
            if (object.toString().equals(name)) {
                return object;
            }
        }
        return null;
    }

    public static Object[][] partition(Object[] args, int length) {
        return partition((a, b) -> new Object[a][b], args, length);
    }

    public static <E> E[][] partition(BiFunction<Integer, Integer, E[][]> function, E[] args, int length) {
        int amount = (int) Math.floor((double) args.length / length);
        int size = args.length % length;
        if (size != 0) {
            amount++;
        }
        E[][] output = function.apply(amount, length);
        for (int index = 0; index < amount; index++) {
            if (index != amount - 1) {
                System.arraycopy(args, index * length, output[index], 0, length);
                continue;
            }
            System.arraycopy(args, index * length, output[index], 0, size);
        }
        return output;
    }

}