package com.kpa.test.common.adapters;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kpa.test.common.ports.PersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
@ConditionalOnProperty(value = "persistence.enabled", havingValue = "true")
public class PersistenceAdapter implements PersistencePort {

  private final static Logger LOGGER = LoggerFactory.getLogger(PersistenceAdapter.class);
  private final ObjectMapper objectMapper;
  private final String rootDir;

  @Autowired
  public PersistenceAdapter(@Value("${persistence.root-dir}") String rootDir) {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    this.rootDir = rootDir;
  }

  @Override
  public void persist(Object object, @NonNull String identifier) {
    File file = getFile(identifier);
    if(!file.exists()) {
      if(file.getParentFile() != null) {
        file.getParentFile().mkdirs();
      } else {
        LOGGER.debug("No parent file found for the file {}", file.getAbsolutePath());
      }

      try {
        file.createNewFile();
      } catch (IOException e) {
        LOGGER.error("Error while creating file {}", file.getAbsolutePath(), e);
        return;
      }
    }
    try {
      objectMapper.writeValue(file, object);
      LOGGER.info("Persisted {} to file {}", identifier, file.getAbsolutePath());
    } catch (IOException e) {
      LOGGER.error("Error while persisting {} to file {}", identifier, file.getAbsolutePath(), e);
    }
  }

  @Override
  public void retrieveCollection(Class<?> itemClass, Collection<?> collection, String identifier) {
    File file = getFile(identifier);
    JavaType retrieveType = objectMapper.getTypeFactory().constructCollectionType(Collection.class, itemClass);
    try {
      collection.addAll(objectMapper.readValue(file, retrieveType));
      if(CollectionUtils.isEmpty(collection)) {
        LOGGER.info("No data found for identifier {}", identifier);
      } else {
        LOGGER.debug("Retrieved {} from file {}", identifier, file.getAbsolutePath());
      }
    } catch (IOException e) {
      LOGGER.warn("Error while retrieving {} from file {}", identifier , file.getAbsolutePath(), e);
    }
  }

  @Override
  public void retrieveMap(Class<?> keyClass, Class<?> valueClass, @NonNull Map<?, ?> map, @NonNull String identifier) {
    File file = getFile(identifier);
    JavaType retrieveType = objectMapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
    try {
      map.putAll(objectMapper.readValue(file, retrieveType));
      if(CollectionUtils.isEmpty(map)) {
        LOGGER.info("No data found for identifier {}", identifier);
      } else {
        LOGGER.debug("Retrieved {} from file {}", identifier, file.getAbsolutePath());
      }
    } catch (IOException e) {
      LOGGER.warn("Error while retrieving {} from file {}", identifier, file.getAbsolutePath(), e);
    }
  }

  @Override
  public Optional<Number> retrieveNumber(@NonNull String identifier) {
    File file = getFile(identifier);
    try {
      return Optional.of(objectMapper.readValue(file, Number.class));
    } catch (IOException e) {
      LOGGER.error("Error while retrieving number {} from file {}", identifier, file.getAbsolutePath(), e);
      return Optional.empty();
    }
  }

  private File getFile(String identifier) {
    return Paths.get(rootDir).resolve(identifier + ".json").toFile();
  }
}
