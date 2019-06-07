package com.inlocate.backend.util.json;

import org.codehaus.jackson.map.module.SimpleDeserializers;

import java.sql.Timestamp;

public class BaseDeserializers extends SimpleDeserializers {
	public BaseDeserializers(){
		addDeserializer(Timestamp.class, new BaseTimestampDeserializer());
	}
}
