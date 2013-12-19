package rapidui.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

import rapidui.Lifecycle;
import rapidui.annotation.event.ListenSensor;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OnSensorChangeRegistrar extends UnregisterableEventRegistrar {
	private Sensor sensor;
	private int rate;
	
	public OnSensorChangeRegistrar(Sensor sensor, int rate) {
		this.sensor = sensor;
		this.rate = rate;
	}
	
	@Override
	public int[] getTargetViewIds(Annotation annotation) {
		return null;
	}

	@Override
	public Object createEventDispatcher(Object target, final Object instance,
			HashMap<Class<?>, Method> methods) {

		return null;
	}

	@Override
	public void registerEventListener(Object target, Object dispatcher) {
		((SensorManager) target).registerListener((SensorEventListener) dispatcher, sensor, rate);
	}
	
	@Override
	public void unregisterEventListener(Object target, Object dispatcher) {
		((SensorManager) target).unregisterListener((SensorEventListener) dispatcher);
	}

	@Override
	public Lifecycle getLifecycle(Annotation annotation) {
		return ((ListenSensor) annotation).lifecycle();
	}
}
