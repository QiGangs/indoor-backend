package com.inlocate.backend.util.json;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.type.TypeReference;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
	public static <T> T readJsonToObject(Class<T> clazz, String json) {
		if (StringUtils.isBlank(json))
			return null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.getDeserializerProvider().withAdditionalDeserializers(new BaseDeserializers());
		try {
			T result = mapper.readValue(json, clazz);
			return result;
		} catch (Exception e) {
		}
		return null;
	}

	public static <T> T readJsonToObject(TypeReference<T> type, String json) {
		if (StringUtils.isBlank(json))
			return null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.getDeserializerProvider().withAdditionalDeserializers(new BaseDeserializers());
		try {
			T result = mapper.readValue(json, type);
			return result;
		} catch (Exception e) {
		}
		return null;
	}

	public static <T> List<T> readJsonToObjectList(Class<T> clazz, String json) {
		if (StringUtils.isBlank(json))
			return null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.getDeserializerProvider().withAdditionalDeserializers(new BaseDeserializers());
		try {
			CollectionType type = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
			List<T> result = mapper.readValue(json, type);
			return result;
		} catch (Exception e) {
		}
		return null;
	}

	public static Map readJsonToMap(String json) {
		if (StringUtils.isBlank(json))
			return new HashMap();
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializerProvider().withAdditionalDeserializers(new BaseDeserializers());
		try {
			Map result = mapper.readValue(json, Map.class);
			if (result == null)
				result = new HashMap();
			return result;
		} catch (Exception e) {
			return new HashMap();
		}

	}

	public static String writeObjectToJson(Object object) {
		if (object == null)
			return null;
		if (object instanceof HashMap) {
			try {
				((Map) object).remove(null);
			} catch (Exception e) {
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
		try {
			CustomSerializerFactory sf = new CustomSerializerFactory();
			sf.addGenericMapping(Date.class, new BaseDateSerializer());
			mapper.setSerializerFactory(sf);
			String data = mapper.writeValueAsString(object);
			return data;
		} catch (Exception e) {
		}
		return null;
	}

	public static String writeMapToJson(Map<String, String> dataMap) {
		if (dataMap == null)
			return null;
		if (dataMap instanceof HashMap) {
			try {
				dataMap.remove(null);
			} catch (Exception e) {
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
		try {
			String data = mapper.writeValueAsString(dataMap);
			return data;
		} catch (Exception e) {
		}
		return null;
	}

	public static String writeMapToJsonFormat(Map<String, String> dataMap) {
		if (dataMap == null)
			return null;
		if (dataMap instanceof HashMap) {
			try {
				dataMap.remove(null);
			} catch (Exception e) {
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
		try {
			String data = mapper.defaultPrettyPrintingWriter().writeValueAsString(dataMap);
			return data;
		} catch (Exception e) {
		}
		return null;
	}

	public static String addJsonKeyValue(String json, String key, String value) {
		Map info = readJsonToMap(json);
		info.put(key, value);
		return writeMapToJson(info);
	}

	public static String removeJsonKeyValue(String json, String key) {
		Map info = readJsonToMap(json);
		info.remove(key);
		return writeMapToJson(info);
	}

	public static String getJsonValueByKey(String json, String key) {
		Map<String, String> info = readJsonToMap(json);
		return info.get(key);
	}

}
