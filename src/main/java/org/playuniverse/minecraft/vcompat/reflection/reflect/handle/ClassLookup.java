package org.playuniverse.minecraft.vcompat.reflection.reflect.handle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    
    private static final ClassLookup FIELD = ClassLookup.of(Field.class).searchField("mod", "modifiers");

    private Class<?> owner;
    private Lookup privateLookup;

    private final HashMap<String, MethodHandle> constructors = new HashMap<>();
    private final HashMap<String, MethodHandle> methods = new HashMap<>();
    private final HashMap<String, VarHandle> fields = new HashMap<>();

    protected ClassLookup(String classPath) throws IllegalAccessException {
        this(ClassCache.getClass(classPath));
    }

    protected ClassLookup(Class<?> owner) throws IllegalAccessException {
        this.owner = owner;
        this.privateLookup = owner != null ? MethodHandles.privateLookupIn(owner, LOOKUP) : null;
    }

    /*
     * 
     */

    public Class<?> getOwner() {
        return owner;
    }

    public Lookup getPrivateLockup() {
        return privateLookup;
    }

    /*
     * 
     */

    public void delete() {
        constructors.clear();
        methods.clear();
        fields.clear();
        owner = null;
        privateLookup = null;
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
                constructors.put(name, unreflect(constructor));
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
                    this.constructors.put(base + current, unreflect(constructor));
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
                methods.put(name, unreflect(method));
            } catch (IllegalAccessException | SecurityException e) {
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
                    this.methods.put(base + current, unreflect(method));
                    current++;
                }
            } catch (IllegalAccessException | SecurityException e) {
            }
        }
        return this;
    }

    /*
     * 
     */

    public ClassLookup searchField(Predicate<ClassLookup> predicate, String name, String fieldName, Class<?> type) {
        return predicate.test(this) ? searchField(name, fieldName, type) : this;
    }

    public ClassLookup searchField(String name, String fieldName) {
        if (hasMethod(name)) {
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
                fields.put(name, unreflect(field));
            } catch (IllegalAccessException | SecurityException e) {
            }
        }
        return this;
    }

    public ClassLookup searchField(String name, String fieldName, Class<?> type) {
        if (hasMethod(name)) {
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
            if (!field.getType().equals(type)) {
                return this;
            }
            try {
                fields.put(name, unreflect(field));
            } catch (IllegalAccessException | SecurityException e) {
            }
        }
        return this;
    }

    /*
     * 
     */

    private void unfinal(Field field) {
        if (!Modifier.isFinal(field.getModifiers())) {
            return;
        }
        FIELD.setFieldValue(field, "mod", field.getModifiers() & ~Modifier.FINAL);
    }

    private VarHandle unreflect(Field field) throws IllegalAccessException, SecurityException {
        if (Modifier.isStatic(field.getModifiers())) {
            boolean access = field.canAccess(null);
            if (!access) {
                field.setAccessible(true);
            }
            unfinal(field);
            VarHandle out = LOOKUP.unreflectVarHandle(field);
            if (!access) {
                field.setAccessible(false);
            }
            return out;
        }
        if (field.trySetAccessible()) {
            unfinal(field);
            VarHandle out = LOOKUP.unreflectVarHandle(field);
            field.setAccessible(false);
            return out;
        }
        unfinal(field);
        return LOOKUP.unreflectVarHandle(field);
    }

    private MethodHandle unreflect(Method method) throws IllegalAccessException, SecurityException {
        if (Modifier.isStatic(method.getModifiers())) {
            boolean access = method.canAccess(null);
            if (!access) {
                method.setAccessible(true);
            }
            MethodHandle out = LOOKUP.unreflect(method);
            if (!access) {
                method.setAccessible(false);
            }
            return out;
        }
        if (method.trySetAccessible()) {
            MethodHandle out = LOOKUP.unreflect(method);
            method.setAccessible(false);
            return out;
        }
        return LOOKUP.unreflect(method);
    }

    private MethodHandle unreflect(Constructor<?> constructor) throws IllegalAccessException {
        boolean access = constructor.canAccess(null);
        if (!access) {
            constructor.setAccessible(true);
        }
        MethodHandle out = LOOKUP.unreflectConstructor(constructor);
        if (!access) {
            constructor.setAccessible(false);
        }
        return out;
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

    /*
     * 
     */

    public static final ClassLookup of(Class<?> clazz) {
        try {
            return new ClassLookup(clazz);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static final ClassLookup of(String path) {
        try {
            return new ClassLookup(path);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

}
