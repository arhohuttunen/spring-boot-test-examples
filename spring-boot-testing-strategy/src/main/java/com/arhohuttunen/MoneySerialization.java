package com.arhohuttunen;

import org.springframework.boot.jackson.JacksonComponent;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.ser.std.StdSerializer;

import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.io.IOException;
import java.util.Locale;

@JacksonComponent
public class MoneySerialization {

    static MonetaryAmountFormat monetaryAmountFormat;

    static {
        monetaryAmountFormat = MonetaryFormats.getAmountFormat(Locale.US);
    }

    static class MonetaryAmountSerializer extends StdSerializer<MonetaryAmount> {

        public MonetaryAmountSerializer() {
            super(MonetaryAmount.class);
        }

        @Override
        public void serialize(
                MonetaryAmount value,
                JsonGenerator generator,
                SerializationContext context) {

            generator.writeString(monetaryAmountFormat.format(value));
        }
    }

    static class MonetaryAmountDeserializer extends StdDeserializer<MonetaryAmount> {

        public MonetaryAmountDeserializer() {
            super(MonetaryAmount.class);
        }

        @Override
        public MonetaryAmount deserialize(
                JsonParser parser,
                DeserializationContext context) {

            JsonNode node = parser.readValueAsTree();
            String text = node.asString();
            return monetaryAmountFormat.parse(text);
        }
    }
}
