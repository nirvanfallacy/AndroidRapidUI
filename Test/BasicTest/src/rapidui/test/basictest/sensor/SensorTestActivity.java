package rapidui.test.basictest.sensor;

import rapidui.RapidActivity;
import rapidui.annotation.Layout;
import rapidui.annotation.LayoutElement;
import rapidui.annotation.event.ListenSensor;
import rapidui.annotation.event.OnSensorChange;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.widget.TextView;

@Layout
public class SensorTestActivity extends RapidActivity {
	@LayoutElement TextView textAccInfo;

	@OnSensorChange(
			@ListenSensor(sensorType=Sensor.TYPE_ACCELEROMETER, rate=SensorManager.SENSOR_DELAY_GAME))
	void onSensorChange(SensorEvent e) {
		textAccInfo.setText("X = " + e.values[0] +
				"\nY = " + e.values[1] +
				"\nZ = " + e.values[2]);
	}
}
