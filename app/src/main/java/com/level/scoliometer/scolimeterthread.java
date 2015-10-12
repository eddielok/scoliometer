package com.level.scoliometer;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.widget.Toast;



public class scolimeterthread extends Thread {
    private Panel _panel;
    private boolean _run = false;
    private static final int TOAST_DURATION = 10000;
    //angle
    public void onTouch(int touchX, int touchY) {
    	//Toast.makeText(_panel.getContext(), touchX, TOAST_DURATION).show();
    	
    }
    public scolimeterthread(Panel panel) {
        _panel = panel;
    }
    
    public void setRunning(boolean run) {
        _run = run;
    }
    
    public boolean isRunning() {
        return _run;
    }
   // public void onOrientationChanged(float newOrientation, float newPitch, float newRoll) {
    //	_panel.setx(newPitch);
    //	_panel.sety(newRoll);
    	
   // }
	@SuppressLint("WrongCall")
    @Override
    public void run() {
        Canvas c;
        while (_run) {
            c = null;
            try {
                c = _panel.getHolder().lockCanvas(null);
                synchronized (_panel.getHolder()) {
                    _panel.updatePhysics();
                    //_panel.checkForWinners();
                    _panel.onDraw(c);
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    _panel.getHolder().unlockCanvasAndPost(c);
                }
            }
        }
    }
}


