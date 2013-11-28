package rapidui.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class HashMap2<TKey1, TKey2, TValue> implements Iterable<Entry<TKey1, HashMap<TKey2, TValue>>> {
	private HashMap<TKey1, HashMap<TKey2, TValue>> map =
			new HashMap<TKey1, HashMap<TKey2, TValue>>();
	
	public void put(TKey1 key1, TKey2 key2, TValue value) {
		HashMap<TKey2, TValue> map2 = map.get(key1);
		if (map2 == null) {
			map2 = new HashMap<TKey2, TValue>();
			map.put(key1, map2);
		}
		
		map2.put(key2, value);
	}
	
	public TValue get(TKey1 key1, TKey2 key2) {
		HashMap<TKey2, TValue> map2 = map.get(key1);
		if (map2 == null) {
			return null;
		}
		
		return map2.get(key2);
	}
	
	@Override
	public Iterator<Entry<TKey1, HashMap<TKey2, TValue>>> iterator() {
		return map.entrySet().iterator(); 
	}
	
	public boolean isEmpty() {
		return map.size() == 0;
	}
	
	public static <TKey1, TKey2, TValue> HashMap2<TKey1, TKey2, TValue> create() {
		return new HashMap2<TKey1, TKey2, TValue>();
	}
}
