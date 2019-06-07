package com.inlocate.backend.util.json;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseDateSerializer extends JsonSerializer<Date>{
    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
    	SimpleDateFormat formatter = null;
    	if(value instanceof Timestamp) formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	else formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(value);
        jgen.writeString(formattedDate);
    }
}
