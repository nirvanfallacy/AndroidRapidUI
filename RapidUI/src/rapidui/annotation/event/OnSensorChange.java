package rapidui.annotation.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import rapidui.Lifecycle;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnSensorChange {
	Lifecycle lifecycle() default Lifecycle.START;
	int[] sensorType();
	int rate();
}
