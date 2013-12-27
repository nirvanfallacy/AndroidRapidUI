package rapidui;

import java.util.WeakHashMap;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class RapidObject {
	private static WeakHashMap<Object, ObjectAspect> aspects;
	
	public static void init(Object o, Context context) {
		init(o, context, null);
	}
	
	public static void init(Object o) {
		init(o, null, null);
	}
	
	public static void init(Object o, View v) {
		final Context context = (v == null ? null : v.getContext());
		init(o, context, v);
	}
	
	private static void init(Object o, Context context, View v) {
		final ObjectAspect oa = new ObjectAspect(context, o, new ViewContainer(v));
		oa.injectCommonThings();
		oa.injectViews();
		
		if (aspects == null) {
			aspects = new WeakHashMap<Object, ObjectAspect>();
		}
		aspects.put(o, oa);
	}
	
	public static void setView(Object o, View v) {
		if (aspects == null) return;
		
		final ObjectAspect oa = aspects.get(o);
		if (oa == null) return;
		
		((ViewContainer) oa.getViewFinder()).setView(v);
	}
	
	public static void onCreate(Object o, Bundle savedInstanceState) {
		if (aspects == null) return;
		
		final ObjectAspect oa = aspects.get(o);
		if (oa == null) return;

		oa.registerListeners(Lifecycle.CREATE);
		oa.registerReceivers(Lifecycle.CREATE);
		oa.restoreInstanceStates(savedInstanceState);
	}

	public static void onDestroy(Object o) {
		if (aspects == null) return;
		
		final ObjectAspect oa = aspects.get(o);
		if (oa == null) return;

		oa.unregisterListeners(Lifecycle.CREATE);
		oa.unregisterReceivers(Lifecycle.CREATE);
	}

	public static void onStart(Object o) {
		if (aspects == null) return;
		
		final ObjectAspect oa = aspects.get(o);
		if (oa == null) return;

		oa.registerListeners(Lifecycle.START);
		oa.registerReceivers(Lifecycle.START);
	}

	public static void onStop(Object o) {
		if (aspects == null) return;
		
		final ObjectAspect oa = aspects.get(o);
		if (oa == null) return;

		oa.unregisterListeners(Lifecycle.START);
		oa.unregisterReceivers(Lifecycle.START);
	}

	public static void onResume(Object o) {
		if (aspects == null) return;
		
		final ObjectAspect oa = aspects.get(o);
		if (oa == null) return;

		oa.registerListeners(Lifecycle.RESUME);
		oa.registerReceivers(Lifecycle.RESUME);
	}

	public static void onPause(Object o) {
		if (aspects == null) return;
		
		final ObjectAspect oa = aspects.get(o);
		if (oa == null) return;

		oa.unregisterListeners(Lifecycle.RESUME);
		oa.unregisterReceivers(Lifecycle.RESUME);
	}

	public static void onSaveInstanceState(Object o, Bundle outState) {
		if (aspects == null) return;
		
		final ObjectAspect oa = aspects.get(o);
		if (oa == null) return;

		oa.saveInstanceStates(outState);
	}
	
	private Context context;
	
	public RapidObject() {
		init(this);
	}
	
	public RapidObject(Context context) {
		this.context = context;
		init(this, context);
	}
	
	protected RapidObject(View v) {
		init(this, v);
	}
	
	public Context getContext() {
		return context;
	}
	
	protected void setView(View v) {
		setView(this, v);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		onCreate(this, savedInstanceState);
	}
	
	public void onDestroy() {
		onDestroy(this);
	}
	
	public void onStart() {
		onStart(this);
	}
	
	public void onStop() {
		onStop(this);
	}
	
	public void onResume() {
		onResume(this);
	}
	
	public void onPause() {
		onPause(this);
	}
	
	public void onSaveInstanceState(Bundle outState) {
		onSaveInstanceState(this, outState);
	}
}
