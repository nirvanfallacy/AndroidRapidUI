package rapidui.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class HashMap3<TKey1, TKey2, TKey3, TValue> implements Iterable<Entry<TKey1, HashMap<TKey2, HashMap<TKey3, TValue>>>> {
	private HashMap<TKey1, HashMap<TKey2, HashMap<TKey3, TValue>>> map =
			new HashMap<TKey1, HashMap<TKey2, HashMap<TKey3, TValue>>>();
	
	public void put(TKey1 key1, TKey2 key2, TKey3 key3, TValue value) {
		HashMap<TKey2, HashMap<TKey3, TValue>> map2 = map.get(key1);
		if (map2 == null) {
			map2 = new HashMap<TKey2, HashMap<TKey3, TValue>>();
			map.put(key1, map2);
		}
		
		HashMap<TKey3, TValue> map3 = map2.get(key2);
		if (map3 == null) {
			map3 = new HashMap<TKey3, TValue>();
			map2.put(key2, map3);
		}
		
		map3.put(key3, value);
	}
	
	public TValue get(TKey1 key1, TKey2 key2, TKey3 key3) {
		HashMap<TKey2, HashMap<TKey3, TValue>> map2 = map.get(key1);
		if (map2 == null) {
			return null;
		}
		
		HashMap<TKey3, TValue> map3 = map2.get(key2);
		if (map3 == null) {
			return null;
		}
		
		return map3.get(key3);
	}
	
	@Override
	public Iterator<Entry<TKey1, HashMap<TKey2, HashMap<TKey3, TValue>>>> iterator() {
		return map.entrySet().iterator(); 
	}
	
	public boolean isEmpty() {
		return map.size() == 0;
	}
	
	public static <TKey1, TKey2, TKey3, TValue> HashMap3<TKey1, TKey2, TKey3, TValue> create() {
		return new HashMap3<TKey1, TKey2, TKey3, TValue>();
	}
}
