package com.level.scoliometer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import com.level.scoliometer.ScoliometerActivity;


import android.R.color;
import android.R.string;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class Panel extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener {

	private scolimeterthread drawer;
    /**
     * Cache variable for all used images.
     */
    private Map<Integer, Bitmap> _bitmapCache = new HashMap<Integer, Bitmap>();
    private static final double MAX_SINUS = Math.sin(Math.PI / 2);
    private static final int TOAST_DURATION = 2000;
    private double x, y, npitch, nroll, nazimuth, croll;
    private double firstpitch, secondpitch;
    private double firstroll, secondroll;
    private boolean calibrating;
    private int mode;
    private double roma, romb;
    private double onea, oneb, twoa, twob;
    private String ra, rb, cola, colb;

    private String Ethnic_Gp, Gender, Age, Foe;
    private double romc, romd, threec, threed, fourc, fourd;
    public boolean isspine;
    private Context CONTEXT;
    public Panel(Context context, AttributeSet attrs) {
    	super(context, attrs);
        fillBitmapCache();
        getHolder().addCallback(this);
        drawer = new scolimeterthread(this);
        setFocusable(true);
        firstpitch = 0;
        secondpitch = 0;
        firstroll = 0;
        secondroll = 0;
        roma = 0;
        romb = 0;
        onea=0;
        oneb=0;
        twoa=0;
        twob=0;
        ra= "Posture A:";
        rb= "Posture B:";
        cola = "One";
        colb = "Two";
        CONTEXT = context;

       // threec=0;
       // threed=0;
       // fourc=0;
      //  fourd=0;
      //  isspine =false;
      //  Ethnic_Gp= "";
     //   Age="";
      //  Foe="";
     //   Gender="";
    }
    
    /**
     * Fill the bitmap cache.
     */
    private void fillBitmapCache() {     
        _bitmapCache.put(R.drawable.protractor, BitmapFactory.decodeResource(getResources(), R.drawable.protractor));
        
    }
    public void setEthnic_Gp(String egp) {
        this.Ethnic_Gp = egp;
    }
    public void setGender(String gender) {
        this.Gender = gender;
    }
    public void setAge(String age) {
        this.Age = age;
    }
    public void setfoe(String foe) {
        this.Foe = foe;
    }
    public String getEthnic_Gp() {
        return this.Ethnic_Gp;
    }
    public String getGender() {return this.Gender;}
    public String getAge() {
        return this.Age;
    }
    public String getfoe() {
        return this.Foe;
    }
    public Boolean getspine() {
        return this.isspine;
    }
    public void setazimuth(double az) {
    	this.nazimuth = az;
    }
    public void setmode(int nmode) {
        this.mode=nmode;
     }
    public boolean getcalibrating() {
    	return this.calibrating;
    }
    public void setfonta() {
    	ra= "Posture A:";
        rb= "Posture B:";
        cola = "One";
        colb = "Two";
    }
    public void setfontb() {
    	ra= "Standing (z):";
        rb= "Flexion (z):";
        cola = "Thoracic";
        colb = "Lumbar";
    }
    public void setfontc() {
    	ra= "Extension:";
        rb= "Flexion:";
        cola = "One";
        colb = "Two";
    }
    public void setfontSE() {
        ra= "Standing  (z):";
        rb= "Extension  (z):";
        cola = "Thoracic";
        colb = "Lumbar";
    }
    public void setcalibrating(boolean c1) {
    	this.calibrating = c1;
    }
    public void setpitch(double angle1) {
        this.npitch=angle1;
     }
    public double dround(double d) {
    	NumberFormat numberFormat = DecimalFormat.getInstance(); 
    	numberFormat.setMaximumFractionDigits(1); 
    	String formattedText = numberFormat.format(d); 
    	return Double.parseDouble(formattedText);
    }
    public String sround(double d) {
    	NumberFormat numberFormat = DecimalFormat.getInstance(); 
    	numberFormat.setMaximumFractionDigits(1); 
    	String formattedText = numberFormat.format(d); 
    	return formattedText;
    }

    public void setroll(double angle2) {    
        this.nroll=angle2;
     }
    public void setcroll(double angle1) {    
        this.croll=angle1;
     }
    public void resetall() {    
        this.onea=0;
        this.oneb=0;
        this.twoa=0;
        this.twob=0;
        this.roma=0;
        this.romb=0;
/*
        this.threec=0;
        this.threed=0;
        this.fourc=0;
        this.fourd=0;
        this.romc=0;
        this.romd=0;
        */
     }
    public void setonea(double oa) {
    	if (oa != 0)
    	this.onea=oa;
    }
    public void setoneb(double ob) {
    	if (ob != 0)
    	this.oneb=ob;
    }
    public void settwoa(double ta) {
    	if (ta != 0)
    	this.twoa=ta;
    }
    public void settwob(double tb) {
    	if (tb != 0)
    	this.twob=tb;
    }
    public double getonea() {
    	return this.onea;
    }
    public double getoneb() {
    	return this.oneb;
    }
    public double gettwoa() {
    	return this.twoa;
    }
    public double gettwob() {
    	return this.twob;
    }

    public double getthreec() {
        return this.threec;
    }
    public double getthreed() {
        return this.threed;
    }
    public double getfourc() {
        return this.fourc;
    }
    public double getfourd() {
        return this.fourd;
    }

    public void setspine(boolean nspine) {
        this.isspine=nspine;
    }
    public void setthreec(double tc) {
       // if (tc != 0)
            this.threec=tc;
    }
    public void setthreed(double td) {
        //if (td != 0)
            this.threed=td;
    }
    public void setfourc(double fc) {
        //if (fc != 0)
            this.fourc=fc;
    }
    public void setfourd(double fd) {
       // if (fd != 0)
            this.fourd=fd;
    }
    public void setromd() {
        this.romc=this.threed-this.threec;
    }
    public void setrome() {
        this.romd=this.fourd-this.fourc;
    }
    /*
    public void setfirst(double firstp, double firstr) { 
    
        this.firstpitch=firstp;
        this.firstroll=firstr;
     }
    public void setsecond(double secondp, double secondr) {     
    	this.secondpitch=secondp;
        this.secondroll=secondr;
     } */
    public double getfirstpitch() {
    	return this.firstpitch;
    }
    public double getsecondpitch() {
    	return this.secondpitch;
    }
    public double getfirstroll() {
    	return this.firstroll;
    }
    public double getsecondroll() {
    	return this.secondroll;
    }
    public void setx(double x1) {     
       this.x=x1;       
    }
    public void sety(double y1) {     
        this.y=y1;       
     }
    public double getroma() {
    	return this.roma;
    }
    public double getromb() {
    	return this.romb;
    }
    public void setroma() {     
    	this.roma=this.oneb-this.onea;       
     }
    public void setromb() {     
    	this.romb=this.twob-this.twoa;       
     }
    public void setromc() {
    	this.roma=this.oneb-this.onea;
     }
    public scolimeterthread getThread() {
        return drawer;
    }
    
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		 if (!drawer.isAlive()) {
	            drawer = new scolimeterthread(this);
	        }
	        drawer.setRunning(true);
	        drawer.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		 boolean retry = true;
		 try {
	        drawer.setRunning(false);
	        while (retry) {
	           
	                drawer.join();
	                retry = false;
	                // free resources
	                System.gc();
	            } 
	        }
		 catch (InterruptedException e) {
             // we will try it again and again...
         }
	        Log.i("thread", "Thread terminated...");	    
	}
	@Override
    @SuppressLint("WrongCall")
    public void onDraw(Canvas canvas) {
		int scaledSize = getResources().getDimensionPixelSize(R.dimen.font_size);
        int sscaledSize = getResources().getDimensionPixelSize(R.dimen.sfont_size);
        int esscaledSize = getResources().getDimensionPixelSize(R.dimen.esfont_size);
		 if (canvas != null) {
	     Paint paint = new Paint();
		 paint.setColor(Color.BLACK);
		 paint.setTypeface(Typeface.DEFAULT_BOLD); 
		 paint.setTextSize(scaledSize);

             Paint spaint = new Paint();
             spaint.setColor(Color.BLACK);
            spaint.setTypeface(Typeface.DEFAULT_BOLD);
             spaint.setTextSize(sscaledSize);

             Paint espaint = new Paint();
             espaint.setColor(Color.BLACK);
             espaint.setTypeface(Typeface.DEFAULT_BOLD);
             espaint.setTextSize(esscaledSize);
		 //draw pitch
		 int rollSize = getResources().getDimensionPixelSize(R.dimen.roll_size);
		 Paint rollpaint = new Paint();
		 rollpaint.setColor(Color.BLACK);
		 rollpaint.setTypeface(Typeface.DEFAULT_BOLD); 
		 rollpaint.setTextSize(rollSize);
		 
		 int cwidth =canvas.getWidth();
		 int cheight=canvas.getHeight();;
		canvas.drawColor(0xFFFFFFFF);
		double tangle;
		//tangle = ((this.nroll))* Math.PI/180;
		if (nroll < 0) tangle = ((this.nroll)+90)* Math.PI/180;
		else tangle = ((this.nroll)-90)* Math.PI/180;
		Rect dst = new Rect(10, 0, cwidth-10, cheight-2);
		Bitmap myBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.protractor);
    	canvas.drawBitmap(myBitmap, null, dst, null);
		canvas.drawBitmap(_bitmapCache.get(R.drawable.protractor), null, dst, null);
		//if (npitch > 90) tangle=(npitch-90)* Math.PI/180;
		//if (npitch < -90) tangle=(npitch+90)* Math.PI/180;
		int hoffset = getResources().getDimensionPixelSize(R.dimen.hoffset);
		canvas.drawLine(cwidth/2,0, //cwidth/2, cheight,
				    (float)(cwidth/2-(cwidth/2*0.92* Math.sin((tangle)))),
				    (float)((hoffset+cheight*0.92* Math.cos((tangle)))),
			    paint);
		//draw text
		 /*
        xa1, ya1 | xb1, yb1 | xc1, yc1
        xa2, ya2 | xb2, yb2 | xc2, yc2
        xa3, ya3 | xb3, yb3 | xc3, yc3
        xa4, ya4 | xb4, yb4 | xc4, yc4
        dx: 50dip
        dy2: 50dip
        dy3: 100dip
        dy4: 150 dip
        */
     /* version two updated for spine mode
        xa1, ya1 | xb1, yb1 |                     | xc1, yc1 |
        xa2, ya2 | xb2, yb2 | xb25, yb25 | xc2, yc2 | xc25, yc25
        xa3, ya3 | xb3, yb3 | xb35, yb35 | xc3, yc3 | xc35, yc35
        xa4, ya4 | xb4, yb4 | xb45, yb45 | xc4, yc1 | xc45, yc45
        dx: 50dip
        dy2: 50dip
        dy3: 100dip
        dy4: 150 dip
        */
		int dbx = getResources().getDimensionPixelSize(R.dimen.dbx);
		int dcx = getResources().getDimensionPixelSize(R.dimen.dcx);
		int dy2 = getResources().getDimensionPixelSize(R.dimen.dy2);
		int dy3 = getResources().getDimensionPixelSize(R.dimen.dy3);
		int dy4 = getResources().getDimensionPixelSize(R.dimen.dy4);

        int dy5 = getResources().getDimensionPixelSize(R.dimen.dy5);
        int dx5 = getResources().getDimensionPixelSize(R.dimen.dsx);
        int xb25, xb35, xb45, xc25, xc35, xc45;
        int yb25, yb35, yb45, yc25, yc35, yc45;

		int xa1, xa2, xa3, xa4, xb1, xb2, xb3, xb4, xc1, xc2, xc3, xc4;
		int ya1, ya2, ya3, ya4, yb1, yb2, yb3, yb4, yc1, yc2, yc3, yc4;

        xa1 = cwidth/4;
        ya1 = cheight/8;
        xb1 = cwidth/2+dbx;
        yb1 = cheight/8;
        xc1 = cwidth/2+dcx;
        yc1 = cheight/8;
       
        xa2 = cwidth/4;
        ya2 = cheight/8+dy2;
        xb2 = cwidth/2+dbx;
        yb2 = cheight/8+dy2;
        xc2 = cwidth/2+dcx;
        yc2 = cheight/8+dy2;
       
        xa3 = cwidth/4;
        ya3 = cheight/8+dy3;
        xb3 = cwidth/2+dbx;
        yb3 = cheight/8+dy3;
        xc3 = cwidth/2+dcx;
        yc3 = cheight/8+dy3;
       
        xa4 = cwidth/4;
        ya4 = cheight/8+dy4;
        xb4 = cwidth/2+dbx;
        yb4 = cheight/8+dy4;
        xc4 = cwidth/2+dcx;
        yc4 = cheight/8+dy4;

             xb25 = xb2+dx5;
             yb25 = cheight/8+dy2;
             xc25 = xc2+dx5;
             yc25 = cheight/8+dy2;
             xb35 = xb3+dx5;
             yb35 = cheight/8+dy3;
             xc35 = xc3+dx5;
             yc35 = cheight/8+dy3;
             xb45 = xb2+dx5;
             yb45 = cheight/8+dy4;
             xc45 = xc2+dx5;
             yc25 = cheight/8+dy4;
             //new added
             if (isspine)
             {
                 spaint.setColor(Color.BLACK);
                 espaint.setColor(Color.BLACK);
             } else
             {
                 spaint.setColor(Color.TRANSPARENT);
                 espaint.setColor(Color.TRANSPARENT);
             }
            // if (isspine)
           //  {
                 if (mode == 0 && isspine) {
                     canvas.drawText("(" + String.valueOf(sround(threec)) + ")", xb25, yb25, spaint);
                     canvas.drawText("(" + String.valueOf(sround(threed)) + ")", xb25, yc35, spaint);
                     canvas.drawText("(" + String.valueOf(sround(fourc)) + ")", xc25, yb25, spaint);
                     canvas.drawText("(" + String.valueOf(sround(fourd)) + ")", xc25, yc35, spaint);
                     canvas.drawText("Ethnic_Gp:"+Ethnic_Gp, cwidth-dy5, cheight-dy5, espaint);
                     canvas.drawText("Age:"+Age, cwidth-dy5, cheight-dy4, espaint);
                     canvas.drawText("Gender:"+Gender, cwidth-dy5, cheight-dy3, espaint);
                     canvas.drawText("Selected:"+Foe, cwidth-dy5, cheight-dy2, espaint);
                 }
           //  }
        //canvas.drawText("p"+sround(npitch), cwidth/2+150, cheight/2, pitchpaint);
        if (croll > -0.1 & croll < 0.9)
        canvas.drawText(sround(croll)+"°", cwidth/2-dy3, cheight/2+dbx, rollpaint);
        else
        canvas.drawText(sround(croll*(-1))+"°", cwidth/2-dy3, cheight/2+dbx, rollpaint);
        //canvas.drawText("a"+sround(nazimuth), cwidth/2-50, cheight/2+70, pitchpaint);
        canvas.drawText("Region(degree):", xa1, ya1, paint);
        canvas.drawText(cola, xb1, yb1, paint);
        if (mode == 0)
        canvas.drawText(colb, xc1, yc1, paint);       
        canvas.drawText(ra, xa2, ya2, paint);
        canvas.drawText(sround((this.onea)), xb2, yb2, paint);
        if (mode == 0)
        canvas.drawText(sround((this.twoa)), xc2, yc2, paint);   
        canvas.drawText(rb, xa3, ya3, paint);
        canvas.drawText(sround((this.oneb)), xb3, yb3, paint);
        if (mode == 0)
        {        
        canvas.drawText(sround((this.twob)), xc3, yc3, paint);
        } 
        canvas.drawText("ROM:", xa4, ya4, paint);
        canvas.drawText(sround((this.roma)), xb4, yb4, paint);
        if (mode == 0)
        canvas.drawText(sround((this.romb)), xc4, yc4, paint);
		 }
	}
	
	
	public void updatePhysics() {
		// TODO Auto-generated method stub
		
	}

	public void checkForWinners() {
		// TODO Auto-generated method stub
		
	}

	public void onOrientationChanged(float orientation, float pitch, float roll) {
		// TODO Auto-generated method stub
		//Toast.makeText(this.getContext(), String.valueOf(roll), 500).show();
		if (drawer != null) {
			//drawer.onOrientationChanged(orientation, pitch, roll);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		//Toast.makeText(this.getContext(), "abc", 1000).show();
	//	if (drawer != null) {
	//		drawer.onTouch((int) event.getX(), (int) event.getY());
	//	}
		return true;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		String touchx = String.valueOf(event.getX());
		calibrating = true;
	//	Toast.makeText(this.getContext(), touchx, 10000).show();
	    return false;
	}
}
