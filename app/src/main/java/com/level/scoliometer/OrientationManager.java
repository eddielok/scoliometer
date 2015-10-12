package com.level.scoliometer;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class OrientationManager {
	
	private static Sensor sensor;
	private static SensorManager sensorManager;
	// you could use an OrientationListener array instead
	// if you plans to use more than one listener
	private static OrientationListener listener;
	
	/** indicates whether or not Orientation Sensor is supported */
	private static Boolean supported;
	/** indicates whether or not Orientation Sensor is running */
	private static boolean running = false;
	private static float calibratedPitch;
	private static float calibratedRoll;
	
	private SensorManager mgr;
	private static Sensor accel;
	private static Sensor compass;
	private Sensor orient;
	private static boolean ready = false;
	private static float[] accelValues = new float[3];
	private static float[] compassValues = new float[3];
	private static float[] inR = new float[9];
	private static float[] outR = new float[9];
	private static float[] inclineMatrix = new float[9];
	private static float[] orientationValues = new float[3];
	private static float[] prefValues = new float[3];
	private float mAzimuth;
	private static double mInclination;
	private static int counter;
	/** Sides of the phone */
	enum Side {
		TOP,
		BOTTOM,
		LEFT,
		RIGHT;
	}
	
	
	
	public static void setCalibration(float...values) {		
		calibratedPitch = values[0];
		calibratedRoll = values[1];
	}
	public static float calibratedroll() {
		return calibratedRoll;
	}
	/**
	 * Reset the calibration
	 */
	public static void resetCalibration() {
		calibratedPitch = 0;
		calibratedRoll = 0;
	}
	/**
	 * Returns true if the manager is listening to orientation changes
	 */
	public static boolean isListening() {
		return running;
	}
	
	/**
	 * Unregisters listeners
	 */
	public static void stopListening() {
		running = false;
		try {
			if (sensorManager != null && sensorEventListener != null) {
				sensorManager.unregisterListener(sensorEventListener);
			}
		} catch (Exception e) {}
	}
	
	/**
	 * Returns true if at least one Orientation sensor is available
	 */
	public static boolean isSupported() {
		if (supported == null) {
			if (ScoliometerActivity.getContext() != null) {
				sensorManager = (SensorManager) ScoliometerActivity.getContext()
						.getSystemService(Context.SENSOR_SERVICE);
				List<Sensor> sensors = sensorManager.getSensorList(
						Sensor.TYPE_ORIENTATION);
				sensors = sensorManager.getSensorList(
						Sensor.TYPE_ORIENTATION);
				sensors = sensorManager.getSensorList(
						Sensor.TYPE_ACCELEROMETER);
				supported = new Boolean(sensors.size() > 0);
			} else {
				supported = Boolean.FALSE;
			}
		}
		return supported;
	}
	
	/**
	 * Registers a listener and start listening
	 */
	public static void startListening(
			OrientationListener orientationListener) {
		sensorManager = (SensorManager) ScoliometerActivity.getContext()
				.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorManager.getSensorList(
				Sensor.TYPE_ORIENTATION);
		accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);///
		compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
		if (sensors.size() > 0) {
			sensor = sensors.get(0);
			running = sensorManager.registerListener(
					sensorEventListener, sensor, 
					SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(
					sensorEventListener, accel, 
					SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(
					sensorEventListener, compass, 
					SensorManager.SENSOR_DELAY_NORMAL);
			listener = orientationListener;
		}
	}

	/**
	 * The listener that listen to events from the orientation listener
	 */
	private static SensorEventListener sensorEventListener = 
		new SensorEventListener() {
		
		/** The side that is currently up */
		private Side currentSide = null;
		private Side oldSide = null;
		private float azimuth;
		private float pitch;
		private float roll;
		private float croll;
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		
		public void onSensorChanged(SensorEvent event) {
			//SensorManager.remapCoordinateSystem(pitch,
             //       SensorManager.AXIS_X, SensorManager.AXIS_Z, roll);
			
	         switch(event.sensor.getType()) {
	         case Sensor.TYPE_ACCELEROMETER:
	         for(int i=0; i<3; i++) {
	         accelValues[i] = event.values[i];
	         }	         
	         if(compassValues[0] != 0)
	         ready = true;
	         break;
	         case Sensor.TYPE_MAGNETIC_FIELD:
	         for(int i=0; i<3; i++) {
	         compassValues[i] = event.values[i];
	         }
	         if(accelValues[2] != 0)
	         ready = true;
	         break;
	         case Sensor.TYPE_ORIENTATION:
	        	    for(int i=0; i<3; i++) {
	        	    orientationValues[i] = event.values[i];
	        	    }
	         }
	         if(!ready)
	             return;
	             if(SensorManager.getRotationMatrix(
	             inR, inclineMatrix, accelValues, compassValues)) {
	             //reallocate the rotation of XYZ axis
	            	 SensorManager.remapCoordinateSystem(inR, 
	     		    		SensorManager.AXIS_Z, SensorManager.AXIS_X, outR);
	             // got a good rotation matrix
	             SensorManager.getOrientation(outR, prefValues);
	             mInclination = SensorManager.getInclination(inclineMatrix);
	             // Display every 10th value
	             if(counter++ % 10 == 0) {
	             counter = 1;
	             }
	             }
	             
	         /*
			if (pitch < -45 && pitch > -135) {
				// top side up
				currentSide = Side.TOP;
			} else if (pitch > 45 && pitch < 135) {
				// bottom side up
				currentSide = Side.BOTTOM;
			} else if (roll > 45) {
				// right side up
				currentSide = Side.RIGHT;
			} else if (roll < -45) {
				// left side up
				currentSide = Side.LEFT;
			} */
			
			if (currentSide != null && !currentSide.equals(oldSide)) {
				
				oldSide = currentSide;
			}
			azimuth = (float) Math.toDegrees(prefValues[0]);//event.values[0]; 	// azimuth
			 pitch = (float) Math.toDegrees(prefValues[1])- calibratedPitch;//event.values[1] - calibratedPitch;     	// pitch
	         //if (azimuth < 0)
	        	 if (pitch >= 82 || pitch <= -82)
	        	 roll = (float) (orientationValues[2]);
	        else
			 roll = (float) Math.toDegrees(prefValues[2]);//event.values[2] - calibratedRoll;      	// roll
			 
	         croll = (float) Math.toDegrees(prefValues[2]) - calibratedRoll;

			 // forwards orientation to the OrientationListener
			listener.onOrientationChanged(azimuth, pitch, roll, croll);
		}
		
	};

}
