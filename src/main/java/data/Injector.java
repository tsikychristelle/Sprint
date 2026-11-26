package data;

import java.lang.reflect.Field;
import java.util.Map;

import annotation.Inject;

public class Injector {
    public static void inject(Object target, Map<Class<?>, Object> beans) throws IllegalAccessException {
        for (Field f : target.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Inject.class)) {
                Object bean = beans.get(f.getType());
                if (bean == null) {
                    throw new IllegalStateException("Aucun repository trouvé pour " + f.getType().getName());
                }
                f.setAccessible(true);
                f.set(target, bean);
            }
        }
    }
}