package com.yandex_backend_school.mega_market.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 12:28 PM
 */

public class DateDeserializer extends StdDeserializer<LocalDateTime> {
  public DateDeserializer() {
    super(LocalDateTime.class);
  }

  @Override
  public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    final String value = jsonParser.readValueAs(String.class);
    final Instant instant = Instant.parse(value);
    return LocalDateTime.from(instant);
  }
}