package com.kpa.test.common.adapters;

import com.kpa.test.common.ports.PersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
@ConditionalOnProperty(value = "persistence.enabled", havingValue = "false")
public class NoPersistenceAdapter implements PersistencePort {

  private final static Logger LOGGER = LoggerFactory.getLogger(NoPersistenceAdapter.class);

  @Override
  public void persist(Object object, @NonNull String identifier) {
   LOGGER.debug("Method not implemented as persistence is disabled");
  }

  @Override
  public void retrieveCollection(Class<?> itemClass, Collection<?> collection, String identifier) {
    LOGGER.debug("Method not implemented as persistence is disabled");
  }

  @Override
  public void retrieveMap(Class<?> keyClass, Class<?> valueClass, @NonNull Map<?, ?> map, @NonNull String identifier) {
    LOGGER.debug("Method not implemented as persistence is disabled");
  }

  @Override
  public Optional<Number> retrieveNumber(@NonNull String identifier) {
    LOGGER.debug("Method not implemented as persistence is disabled");
    return Optional.empty();
  }

}
