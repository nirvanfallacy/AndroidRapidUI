package rapidui.eventhandler;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class EventHandlers<T> implements Iterable<T> {
	private LinkedList<T> list;

	public void add(T listener) {
		add(listener, null);
	}

	public void add(T listener, Runnable onFirstAdd) {
		if (listener == null) return;
		
		if (list == null) {
			list = new LinkedList<T>();
		}
		
		if (list.contains(listener)) return;
		
		list.add(listener);
		if (list.size() == 1 && onFirstAdd != null) {
			onFirstAdd.run();
		}
	}

	public boolean remove(T listener) {
		return remove(listener, null);
	}

	public boolean remove(T listener, Runnable onLastRemove) {
		if (listener == null || list == null) return false;
		
		final boolean removed = list.remove(listener);
		
		if (removed && list.isEmpty() && onLastRemove != null) {
			onLastRemove.run();
		}
		
		return removed;
	}

	@Override
	public Iterator<T> iterator() {
		if (list == null) {
			return Collections.<T>emptyList().iterator();
		} else {
			return list.iterator();
		}
	}
}
