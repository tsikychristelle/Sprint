package data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotation.Repository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import util.Util;

public class JpaContext {
    private static EntityManagerFactory emf;
    private static final Map<Class<?>, Object> repositories = new HashMap<>();

    public static void init(List<String> packages, String persistenceUnit) throws Exception {
        emf = Persistence.createEntityManagerFactory(persistenceUnit);

        for (String className : Util.findClasses(packages, Repository.class)) {
            Class<?> repoInterface = Class.forName(className);
            if (!repoInterface.isInterface()) continue;

            Class<?> entity = extractEntityType(repoInterface);
            repositories.put(repoInterface, RepositoryFactory.create(repoInterface, entity, emf));
        }
    }

    private static Class<?> extractEntityType(Class<?> repo) {
        for (Type t : repo.getGenericInterfaces()) {
            if (t instanceof ParameterizedType pt && pt.getRawType() == CrudRepository.class) {
                return (Class<?>) pt.getActualTypeArguments()[0];
            }
        }
        throw new IllegalStateException(repo.getName() + " doit étendre CrudRepository<Entité, Id>");
    }

    public static Map<Class<?>, Object> getRepositories() {
        return repositories;
    }

    public static void close() {
        if (emf != null && emf.isOpen()) emf.close();
    }
}