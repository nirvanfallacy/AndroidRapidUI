package rapidui.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.util.SparseArray;

public class SparseArray2<TKey2, TValue> implements Iterable<Entry<Integer, HashMap<TKey2, TValue>>> {
	private SparseArray<HashMap<TKey2, TValue>> map =
			new SparseArray<HashMap<TKey2, TValue>>();
	
	public void put(int key1, TKey2 key2, TValue value) {
		HashMap<TKey2, TValue> map2 = map.get(key1);
		if (map2 == null) {
			map2 = new HashMap<TKey2, TValue>();
			map.put(key1, map2);
		}
		
		map2.put(key2, value);
	}
	
	public TValue get(int key1, TKey2 key2) {
		HashMap<TKey2, TValue> map2 = map.get(key1);
		if (map2 == null) {
			return null;
		}
		
		return map2.get(key2);
	}
	
	@Override
	public Iterator<Entry<Integer, HashMap<TKey2, TValue>>> iterator() {
		return new Iterator<Map.Entry<Integer, HashMap<TKey2, TValue>>>() {
			private int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < map.size();
			}

			@Override
			public Entry<Integer, HashMap<TKey2, TValue>> next() {
				final int key = map.keyAt(index);
				final HashMap<TKey2, TValue> value = map.valueAt(index);
				++index;
				
				return new KeyValueEntry<Integer, HashMap<TKey2, TValue>>(key, value);
			}

			@Override
			public void remove() {
			}
		};
	}
	
	public boolean isEmpty() {
		return map.size() == 0;
	}
	
	public void clear() {
		map.clear();
	}
	
	public static <TKey2, TValue> SparseArray2<TKey2, TValue> create() {
		return new SparseArray2<TKey2, TValue>();
	}
}
