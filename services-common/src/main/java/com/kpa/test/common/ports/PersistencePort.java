package com.kpa.test.common.ports;

import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface PersistencePort {

  void persist(Object object, @NonNull String identifier);

  void retrieveCollection(Class<?> itemClass, @NonNull Collection<?> collection, @NonNull String identifier);

  void retrieveMap(Class<?> keyClass, Class<?> valueClass, @NonNull Map<?, ?> map, @NonNull String identifier);

  Optional<Number> retrieveNumber(@NonNull String identifier);
}
