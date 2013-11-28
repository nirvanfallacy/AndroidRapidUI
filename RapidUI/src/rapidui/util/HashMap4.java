package rapidui.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class HashMap4<TKey1, TKey2, TKey3, TKey4, TValue> implements Iterable<Entry<TKey1, HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>>>> {
	private HashMap<TKey1, HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>>> map =
			new HashMap<TKey1, HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>>>();
	
	public void put(TKey1 key1, TKey2 key2, TKey3 key3, TKey4 key4, TValue value) {
		HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>> map2 = map.get(key1);
		if (map2 == null) {
			map2 = new HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>>();
			map.put(key1, map2);
		}
		
		HashMap<TKey3, HashMap<TKey4, TValue>> map3 = map2.get(key2);
		if (map3 == null) {
			map3 = new HashMap<TKey3, HashMap<TKey4, TValue>>();
			map2.put(key2, map3);
		}
		
		HashMap<TKey4, TValue> map4 = map3.get(key3);
		if (map4 == null) {
			map4 = new HashMap<TKey4, TValue>();
			map3.put(key3, map4);
		}
		
		map4.put(key4, value);
	}
	
	public TValue get(TKey1 key1, TKey2 key2, TKey3 key3, TKey4 key4) {
		HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>> map2 = map.get(key1);
		if (map2 == null) {
			return null;
		}
		
		HashMap<TKey3, HashMap<TKey4, TValue>> map3 = map2.get(key2);
		if (map3 == null) {
			return null;
		}
		
		HashMap<TKey4, TValue> map4 = map3.get(key3);
		if (map4 == null) {
			return null;
		}
		
		return map4.get(key4);
	}
	
	@Override
	public Iterator<Entry<TKey1, HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>>>> iterator() {
		return map.entrySet().iterator();
	}
	
	public boolean isEmpty() {
		return map.size() == 0;
	}

	public static <TKey1, TKey2, TKey3, TKey4, TValue> HashMap4<TKey1, TKey2, TKey3, TKey4, TValue> create() {
		return new HashMap4<TKey1, TKey2, TKey3, TKey4, TValue>();
	}
}
