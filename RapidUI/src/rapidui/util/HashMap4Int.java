package rapidui.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.util.SparseArray;

public class HashMap4Int<TKey2, TKey3, TKey4, TValue> implements Iterable<Entry<Integer, HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>>>> {
	private SparseArray<HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>>> map =
			new SparseArray<HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>>>();
	
	public void put(int key1, TKey2 key2, TKey3 key3, TKey4 key4, TValue value) {
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
	
	public TValue get(int key1, TKey2 key2, TKey3 key3, TKey4 key4) {
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
	public Iterator<Entry<Integer, HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>>>> iterator() {
		return new Iterator<Map.Entry<Integer, HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>>>>() {
			private int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < map.size();
			}

			@Override
			public Entry<Integer, HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>>> next() {
				final int key = map.keyAt(index);
				final HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>> value = map.valueAt(index);
				++index;
				
				return new KeyValueEntry<Integer, HashMap<TKey2, HashMap<TKey3, HashMap<TKey4, TValue>>>>(key, value);
			}

			@Override
			public void remove() {
			}
		};
	}
	
	public boolean isEmpty() {
		return map.size() == 0;
	}
}
