package com.inlocate.backend.util.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.TimestampDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class BaseTimestampDeserializer extends TimestampDeserializer {
	protected java.util.Date _parseDate(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonToken t = jp.getCurrentToken();
		try {
			if (t == JsonToken.VALUE_NUMBER_INT) return new java.util.Date(jp.getLongValue());
			if (t == JsonToken.VALUE_STRING) {
				String str = jp.getText().trim();
				if (str.length() == 0) return null;
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return formatter.parse(str);
			}
			throw ctxt.mappingException(_valueClass);
		} catch (ParseException e) {
			throw ctxt.mappingException(_valueClass);
		} catch (IllegalArgumentException iae) {
			throw ctxt.weirdStringException(_valueClass, "not a valid representation (error: " + iae.getMessage()	+ ")");
		}
	}
}
