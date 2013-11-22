package rapidui.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.util.SparseArray;

public class HashMap3Int<TKey2, TKey3, TValue> implements Iterable<Entry<Integer, HashMap<TKey2, HashMap<TKey3, TValue>>>> {
	private SparseArray<HashMap<TKey2, HashMap<TKey3, TValue>>> map =
			new SparseArray<HashMap<TKey2, HashMap<TKey3, TValue>>>();
	
	public void put(int key1, TKey2 key2, TKey3 key3, TValue value) {
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
	
	public TValue get(int key1, TKey2 key2, TKey3 key3) {
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
	public Iterator<Entry<Integer, HashMap<TKey2, HashMap<TKey3, TValue>>>> iterator() {
		return new Iterator<Map.Entry<Integer, HashMap<TKey2, HashMap<TKey3, TValue>>>>() {
			private int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < map.size();
			}

			@Override
			public Entry<Integer, HashMap<TKey2, HashMap<TKey3, TValue>>> next() {
				final int key = map.keyAt(index);
				final HashMap<TKey2, HashMap<TKey3, TValue>> value = map.valueAt(index);
				++index;
				
				return new KeyValueEntry<Integer, HashMap<TKey2, HashMap<TKey3, TValue>>>(key, value);
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
