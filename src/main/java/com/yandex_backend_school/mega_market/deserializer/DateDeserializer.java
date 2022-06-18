package com.yandex_backend_school.mega_market.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 12:28 PM
 */

public class DateDeserializer extends StdDeserializer<Date> {
  public DateDeserializer() {
    super(Date.class);
  }

  @Override
  public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    final String value = jsonParser.readValueAs(String.class);
    final Instant instant = Instant.parse(value);
    return Date.from(instant);
  }
}