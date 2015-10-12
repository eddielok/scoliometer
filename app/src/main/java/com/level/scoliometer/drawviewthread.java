
package com.level.scoliometer;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.widget.Toast;



public class drawviewthread extends Thread {
    private drawView dv;
    private boolean _run = false;
    private static final int TOAST_DURATION = 10000;
    //angle
    public void onTouch(int touchX, int touchY) {
    	//Toast.makeText(_panel.getContext(), touchX, TOAST_DURATION).show();
    	
    }
    public drawviewthread(drawView tempdv) {
        dv = tempdv;
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
	@Override
    @SuppressLint("WrongCall")
    public void run() {
        Canvas c;
        while (_run) {
            c = null;
            try {
                c = dv.getHolder().lockCanvas(null);
                synchronized (dv.getHolder()) {
                    //_panel.checkForWinners();
                    dv.onDraw(c);
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    dv.getHolder().unlockCanvasAndPost(c);
                }
            }
        }
    }
}


