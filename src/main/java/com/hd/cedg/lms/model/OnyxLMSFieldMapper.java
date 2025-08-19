package com.hd.cedg.lms.model;

import java.util.ArrayList;
import java.util.List;

public class OnyxLMSFieldMapper {

	private List<String[]> map;
	
	public OnyxLMSFieldMapper() {
		map = new ArrayList<String[]>();
	}

	public void define(String fieldName, String onyxValue, String ekpValue) {
		String[] entry = new String[3];
		entry[0] = fieldName;
		entry[1] = onyxValue;
		entry[2] = ekpValue;
		map.add(entry);
	}

	public String onyxToEkpLookup(String fieldName, String onyxValue) {
		for (String[] entry : map) {
			boolean match = false;
			
			if (entry[0].equals(fieldName)) {
				if (onyxValue == null) {
					if (entry[1] == null) {
						match = true;
					}
				} else {
					if (onyxValue.equals(entry[1])) {
						match = true;
					}
				}
			}
			
			if (match) {
				return entry[2];
			}
		}
		return null;
	}

	public String ekpToOnyxLookup(String fieldName, String ekpValue) {
		for (String[] entry : map) {
			boolean match = false;
			
			if (entry[0].equals(fieldName)) {
				if (ekpValue == null) {
					if (entry[2] == null) {
						match = true;
					}
				} else {
					if (ekpValue.equals(entry[2])) {
						match = true;
					}
				}
			}
			
			if (match) {
				return entry[1];
			}
		}
		return null;
	}

}
