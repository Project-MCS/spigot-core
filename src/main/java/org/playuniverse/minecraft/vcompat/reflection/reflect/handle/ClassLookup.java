package org.playuniverse.minecraft.vcompat.reflection.reflect.handle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.syntaxphoenix.syntaxapi.reflection.ClassCache;
import com.syntaxphoenix.syntaxapi.reflection.ReflectionTools;
import com.syntaxphoenix.syntaxapi.utils.java.Arrays;

public class ClassLookup {

    public static final Lookup LOOKUP = MethodHandles.lookup();

    private Class<?> owner;

    private final HashMap<String, MethodHandle> constructors = new HashMap<>();
    private final HashMap<String, MethodHandle> methods = new HashMap<>();
    private final HashMap<String, VarHandle> fields = new HashMap<>();

    public ClassLookup(String classPath) {
        this.owner = ClassCache.getClass(classPath);
    }

    public ClassLookup(Class<?> owner) {
        this.owner = owner;
    }

    /*
     * 
     */

    public Class<?> getOwner() {
        return owner;
    }

    /*
     * 
     */

    public void delete() {
        constructors.clear();
        methods.clear();
        fields.clear();
        owner = null;
    }

    public boolean isValid() {
        return owner != null;
    }

    /*
     * 
     */

    public Collection<MethodHandle> getConstructors() {
        return constructors.values();
    }

    public Collection<MethodHandle> getMethods() {
        return methods.values();
    }

    public Collection<VarHandle> getFields() {
        return fields.values();
    }

    /*
     * 
     */

    public MethodHandle getConstructor(String name) {
        return isValid() ? constructors.get(name) : null;
    }

    public MethodHandle getMethod(String name) {
        return isValid() ? methods.get(name) : null;
    }

    public VarHandle getField(String name) {
        return isValid() ? fields.get(name) : null;
    }

    /*
     * 
     */

    public boolean hasConstructor(String name) {
        return isValid() && constructors.containsKey(name);
    }

    public boolean hasMethod(String name) {
        return isValid() && methods.containsKey(name);
    }

    public boolean hasField(String name) {
        return isValid() && fields.containsKey(name);
    }

    /*
     * 
     */

    public Object init() {
        if (!isValid()) {
            return null;
        }
        MethodHandle handle = constructors.computeIfAbsent("$base#empty", (ignore) -> {
            try {
                return LOOKUP.unreflectConstructor(owner.getConstructor());
            } catch (IllegalAccessException | NoSuchMethodException | SecurityException e) {
                return null;
            }
        });
        if (handle == null) {
            constructors.remove("$base#empty");
            return null;
        }
        try {
            return handle.invoke();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object init(String name, Object... args) {
        if (!isValid() || !constructors.containsKey(name)) {
            return null;
        }
        try {
            return constructors.get(name).invokeWithArguments(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 
     */

    public ClassLookup execute(String name, Object... args) {
        run(name, args);
        return this;
    }

    public ClassLookup execute(Object source, String name, Object... args) {
        run(source, name, args);
        return this;
    }

    public Object run(String name, Object... args) {
        if (!isValid() || !methods.containsKey(name)) {
            return null;
        }
        try {
            return methods.get(name).invokeWithArguments(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object run(Object source, String name, Object... args) {
        if (!isValid() || !methods.containsKey(name)) {
            return null;
        }
        try {
            return methods.get(name).invokeWithArguments(mergeBack(args, source));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 
     */

    public Object getFieldValue(String name) {
        return isValid() && fields.containsKey(name) ? fields.get(name).get() : null;
    }

    public Object getFieldValue(Object source, String name) {
        return isValid() && fields.containsKey(name) ? fields.get(name).get(source) : null;
    }

    public void setFieldValue(String name, Object value) {
        if (!isValid() || !fields.containsKey(name)) {
            return;
        }
        fields.get(name).set(value);
    }

    public void setFieldValue(Object source, String name, Object value) {
        if (!isValid() || !fields.containsKey(name)) {
            return;
        }
        fields.get(name).set(source, value);
    }

    /*
     * 
     */

    public ClassLookup searchConstructor(Predicate<ClassLookup> predicate, String name, Class<?>... args) {
        return predicate.test(this) ? searchConstructor(name, args) : this;
    }

    public ClassLookup searchConstructor(String name, Class<?>... arguments) {
        if (hasConstructor(name)) {
            return this;
        }
        Constructor<?> constructor = null;
        try {
            constructor = owner.getDeclaredConstructor(arguments);
        } catch (NoSuchMethodException | SecurityException e) {
        }
        if (constructor == null) {
            try {
                constructor = owner.getConstructor(arguments);
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }
        if (constructor != null) {
            try {
                boolean access = constructor.canAccess(null);
                if (!access) {
                    constructor.setAccessible(true);
                }
                constructors.put(name, LOOKUP.unreflectConstructor(constructor));
                if (!access) {
                    constructor.setAccessible(false);
                }
            } catch (IllegalAccessException e) {
            }
        }
        return this;
    }

    public ClassLookup searchConstructorsByArguments(String base, Class<?>... arguments) {
        Constructor<?>[] constructors = Arrays.merge(Constructor<?>[]::new, owner.getDeclaredConstructors(), owner.getConstructors());
        if (constructors.length == 0) {
            return this;
        }
        base += '-';
        int current = 0;
        for (Constructor<?> constructor : constructors) {
            Class<?>[] args = constructor.getParameterTypes();
            if (args.length != arguments.length) {
                continue;
            }
            try {
                if (ReflectionTools.hasSameArguments(arguments, args)) {
                    boolean access = constructor.canAccess(null);
                    if (!access) {
                        constructor.setAccessible(true);
                    }
                    this.constructors.put(base + current, LOOKUP.unreflectConstructor(constructor));
                    if (!access) {
                        constructor.setAccessible(false);
                    }
                    current++;
                }
            } catch (IllegalAccessException e) {
            }
        }
        return this;
    }

    /*
     * 
     */

    public ClassLookup searchMethod(Predicate<ClassLookup> predicate, String name, String methodName, Class<?>... arguments) {
        return predicate.test(this) ? searchMethod(name, methodName, arguments) : this;
    }

    public ClassLookup searchMethod(String name, String methodName, Class<?>... arguments) {
        if (hasMethod(name)) {
            return this;
        }
        Method method = null;
        try {
            method = owner.getDeclaredMethod(methodName, arguments);
        } catch (NoSuchMethodException | SecurityException e) {
        }
        if (method == null) {
            try {
                method = owner.getMethod(methodName, arguments);
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }
        if (method != null) {
            try {
                methods.put(name, LOOKUP.unreflect(method));
            } catch (IllegalAccessException e) {
            }
        }
        return this;
    }

    public ClassLookup searchMethodsByArguments(String base, Class<?>... arguments) {
        Method[] methods = Arrays.merge(Method[]::new, owner.getDeclaredMethods(), owner.getMethods());
        if (methods.length == 0) {
            return this;
        }
        base += '-';
        int current = 0;
        for (Method method : methods) {
            Class<?>[] args = method.getParameterTypes();
            if (args.length != arguments.length) {
                continue;
            }
            try {
                if (ReflectionTools.hasSameArguments(arguments, args)) {
                    this.methods.put(base + current, LOOKUP.unreflect(method));
                    current++;
                }
            } catch (IllegalAccessException e) {
            }
        }
        return this;
    }

    /*
     * 
     */

    public ClassLookup searchField(Predicate<ClassLookup> predicate, String name, String fieldName) {
        return predicate.test(this) ? searchField(name, fieldName) : this;
    }

    public ClassLookup searchField(String name, String fieldName) {
        if (hasField(name)) {
            return this;
        }
        Field field = null;
        try {
            field = owner.getDeclaredField(fieldName);
        } catch (NoSuchFieldException | SecurityException e) {
        }
        if (field == null) {
            try {
                field = owner.getField(fieldName);
            } catch (NoSuchFieldException | SecurityException e) {
            }
        }
        if (field != null) {
            try {
                fields.put(name, LOOKUP.unreflectVarHandle(field));
            } catch (IllegalAccessException e) {
            }
        }
        return this;
    }

    public ClassLookup searchFields(String base, String fieldName) {
        if (!isValid()) {
            return this;
        }
        Field[] searching = owner.getFields();
        int current = 0;
        for (Field field : searching) {
            try {
                if (field.getName().startsWith(fieldName)) {
                    fields.put(base + current, LOOKUP.unreflectVarHandle(field));
                    current++;
                }
            } catch (IllegalAccessException e) {
            }
        }
        return this;
    }

    public ClassLookup searchField(String name, Class<?> type) {
        if (hasField(name)) {
            return this;
        }
        Field[] searching = owner.getFields();
        for (Field field : searching) {
            if (field.getType() == type) {
                try {
                    fields.put(name, LOOKUP.unreflectVarHandle(field));
                } catch (IllegalAccessException e) {
                }
                return this;
            }
        }
        return this;
    }

    public ClassLookup searchFields(String base, Class<?> type) {
        if (!isValid()) {
            return this;
        }
        Field[] searching = owner.getFields();
        int current = 0;
        for (Field field : searching) {
            try {
                if (field.getType() == type) {
                    fields.put(base + current, LOOKUP.unreflectVarHandle(field));
                    current++;
                }
            } catch (IllegalAccessException e) {
            }
        }
        return this;
    }

    /*
     * 
     */

    public static void uncache(ClassLookup lookup) {
        Class<?> search = lookup.getOwner();
        lookup.delete();
        if (ClassCache.CLASSES.isEmpty()) {
            return;
        }
        Optional<Entry<String, Class<?>>> option = ClassCache.CLASSES.entrySet().stream().filter(entry -> entry.getValue().equals(search))
            .findFirst();
        if (option.isPresent()) {
            ClassCache.CLASSES.remove(option.get().getKey());
        }
    }

    public static Object[] mergeBack(Object[] array1, Object... array2) {
        Object[] output = new Object[array1.length + array2.length];
        System.arraycopy(array2, 0, output, 0, array2.length);
        System.arraycopy(array1, 0, output, array2.length, array1.length);
        return output;
    }

}
