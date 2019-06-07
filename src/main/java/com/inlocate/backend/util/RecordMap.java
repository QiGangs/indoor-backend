package com.inlocate.backend.util;

import org.apache.commons.collections.map.CaseInsensitiveMap;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

public class RecordMap extends CaseInsensitiveMap {
	private static final long serialVersionUID = 3445316538262854490L;
	public RecordMap(Map map){
		super(map);
	}
	public Long getLong(String key){
		Object value = get(key);
		if(value==null) return null;
		return Long.parseLong(value.toString());
	}
	public Integer getInteger(String key){
		Object value = get(key);
		if(value==null) return null;
		return Integer.parseInt(value.toString());
	}
	public Double getDouble(String key){
		Object value = get(key);
		if(value==null) return null;
		return Double.parseDouble(value.toString());
	}
	public Timestamp getTimestamp(String key){
		Object value = get(key);
		if(value==null) return null;
		return (Timestamp) value;
	}
	public Date getDate(String key){
		Object value = get(key);
		if(value==null) return null;
		return (Date) value;
	}
	public String getString(String key){
		Object value = get(key);
		if(value==null) return null;
		return value.toString();
	}
	public Boolean getBoolean(String key){
		Object value = get(key);
		if(value==null) return false;
		return Boolean.parseBoolean(value.toString());
	}
	public Timestamp string2Timestamp(String key){
		Object value = get(key);
		if(value==null) return null;
		return DateUtil.parseTimestamp(""+value);
	}
	public Date string2Date(String key){
		Object value = get(key);
		if(value==null) return null;
		return DateUtil.parseDate(""+value);
	}
}
