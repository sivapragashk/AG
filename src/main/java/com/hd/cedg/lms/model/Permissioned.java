package com.hd.cedg.lms.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Permissioned {

	public abstract String getPermissionType();

	public abstract List<String> getAccessTags();

	public abstract void setAccessTags(List<String> accessTags);

	public boolean isPermissible(LearningUser user) {
		return isPermissible(user.getAccessTags());
	}
	
	public boolean isPermissible(List<String> userAccessTags) {
		List<String> accessTags = getAccessTags();
		if (accessTags.size() == 0) {
			return true;
		} else {
			for (String accessTag : accessTags) {
				if (userAccessTags.contains(accessTag)) {
					return true;
				}
			}
			return false;
		}
	}

	public static <V extends Permissioned> List<V> filterList(List<V> list,
			LearningUser user) {
		List<V> filteredList = new ArrayList<V>();
		for (V perm : list) {
			if (perm.isPermissible(user)) {
				filteredList.add(perm);
			}
		}
		return filteredList;
	}

	public static <K, V extends Permissioned> Map<K, V> filterMap(
			Map<K, V> map, LearningUser user) {
		Map<K, V> filteredMap = new HashMap<K, V>();
		for (K key : map.keySet()) {
			V perm = map.get(key);
			if (perm.isPermissible(user)) {
				filteredMap.put(key, perm);
			}
		}
		return filteredMap;
	}

}
