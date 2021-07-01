package org.playuniverse.minecraft.vcompat.reflection.reflect.handle;

public class ClassLookupCache extends AbstractClassLookupCache<ClassLookup> {

    @Override
    protected ClassLookup create(Class<?> clazz) {
        return new ClassLookup(clazz);
    }

    @Override
    protected ClassLookup create(String path) {
        return new ClassLookup(path);
    }

}
