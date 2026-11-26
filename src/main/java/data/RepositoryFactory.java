package data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import jakarta.persistence.EntityManagerFactory;

public class RepositoryFactory {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <R> R create(Class<R> repoInterface, Class<?> entityClass, EntityManagerFactory emf) {
        SimpleRepository impl = new SimpleRepository(emf, entityClass);

        return repoInterface.cast(Proxy.newProxyInstance(
            repoInterface.getClassLoader(),
            new Class<?>[] { repoInterface },
            (proxy, method, args) -> {
                try {
                    return method.invoke(impl, args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }));
    }
}