package json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for JSON operations using Jackson.
 */
public class JsonUtils {

  public static ObjectMapper getMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(Feature.AUTO_CLOSE_SOURCE, false);
    return mapper;
  }

  public static JsonParser getJsonParser(InputStream input, ObjectMapper mapper) throws IOException {
    JsonFactory factory = mapper.getFactory();
    return factory.createParser(input);
  }

  public static String writeObjectToJson(Object obj) throws IOException {
    return new ObjectMapper().writeValueAsString(obj);
  }

  public static <T> T deserialize(InputStream input, Class<T> classToMapTo) throws IOException {
    ObjectMapper mapper = getMapper();
    JsonParser parser = getJsonParser(input, mapper);
    return mapper.readValue(parser, classToMapTo);
  }

}
