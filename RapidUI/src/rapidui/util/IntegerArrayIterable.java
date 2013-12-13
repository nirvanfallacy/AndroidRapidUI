package rapidui.util;

import java.util.Iterator;

public class IntegerArrayIterable implements Iterable<Integer> {
	private int[] array;
	
	public IntegerArrayIterable(int[] array) {
		this.array = array;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			private int i = 0;
			
			@Override
			public void remove() {
			}
			
			@Override
			public Integer next() {
				return array[i++];
			}
			
			@Override
			public boolean hasNext() {
				return i < array.length;
			}
		};
	}
}
