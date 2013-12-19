package rapidui.util;

import java.util.HashMap;
import java.util.Map;

import android.util.SparseArray;

public class Shortcuts {
	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}

	public static <K, V> HashMap<K, V> newHashMap(Object... params) {
		final HashMap<K, V> map = new HashMap<K, V>();
		initMap(map, params);
		return map;
	}

	public static <V> SparseArray<V> newSparseArray(Object... params) {
		final SparseArray<V> map = new SparseArray<V>();
		initSparseArray(map, params);
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static <K, V> void initMap(Map<K, V> map, Object... params) {
		for (int i = 0; i < params.length; i += 2) {
			final K key = (K) params[i];
			final V value = (V) params[i + 1];
			map.put(key, value);
		}
	}

	@SuppressWarnings("unchecked")
	public static <V> void initSparseArray(SparseArray<V> map, Object... params) {
		for (int i = 0; i < params.length; i += 2) {
			final int key = (Integer) params[i];
			final V value = (V) params[i + 1];
			map.put(key, value);
		}
	}
}
