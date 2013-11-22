package rapidui.util;

import java.util.Map;

public class KeyValueEntry<K, V> implements Map.Entry<K, V> {
	private K key;
	private V value;
	
	public KeyValueEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V object) {
		final V old = value;
		this.value = object;
		return old;
	}
}
