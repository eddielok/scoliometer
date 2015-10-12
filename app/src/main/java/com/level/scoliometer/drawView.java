package com.level.scoliometer;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class drawView extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener {
    private Paint textPaint = new Paint();
    private Paint textborder = new Paint();
    private Paint stextborder = new Paint();
    private Paint estextborder = new Paint();
    private boolean calibrating;
    private int mode;
    private boolean isblank;
    private double npitch, nroll, croll;
    private double roma, romb;
    private double onea, oneb, twoa, twob;
    private String Ethnic_Gp, Gender, Age, Foe;
    private double romc, romd, threec, threed, fourc, fourd;
    public boolean isspine;
    private String ra, rb, cola, colb;
    private drawviewthread drawer;
    private Context ct;
    Button myButton1;  
	public drawView(Context context) {
		super(context);
		//getHolder().addCallback(this);
		ct = context;
		drawer = new drawviewthread(this);
		// Create out paint to use for drawing
        textPaint.setARGB(255, 125, 255, 255);
        textPaint.setStrokeWidth(5);
        int scaledSize = getResources().getDimensionPixelSize(R.dimen.font_size);
        textborder.setARGB(255, 0, 0, 0);
        textborder.setTextSize(scaledSize);
        textborder.setTypeface(Typeface.DEFAULT_BOLD);
        textborder.setStyle(Paint.Style.STROKE);
        textborder.setStrokeWidth(2);

        int sscaledSize = getResources().getDimensionPixelSize(R.dimen.sfont_size);
        stextborder.setARGB(255, 0, 0, 0);
        stextborder.setTextSize(sscaledSize);
        stextborder.setTypeface(Typeface.DEFAULT_BOLD);
        stextborder.setStyle(Paint.Style.STROKE);
        stextborder.setStrokeWidth(2);

        int esscaledSize = getResources().getDimensionPixelSize(R.dimen.esfont_size);
        estextborder.setARGB(255, 0, 0, 0);
        estextborder.setTextSize(esscaledSize);
        estextborder.setTypeface(Typeface.DEFAULT_BOLD);
        estextborder.setStyle(Paint.Style.STROKE);
        estextborder.setStrokeWidth(2);
        roma = 0;
        romb = 0;
        onea=0;
        oneb=0;
        twoa=0;
        twob=0;

        romc=0;
        romd=0;
        threec=0;
        threed=0;
        fourc=0;
        fourd=0;
        isspine =false;
        isblank=false;

       // threec=0;
       // threed=0;
      //  fourc=0;
     //   fourd=0;
      //  isspine =false;
      //  Ethnic_Gp= "";
      //  Age="";
      //  Foe="";
      //  Gender="";

        /* This call is necessary, or else the 
         * draw method will not be called. 
         */
        setWillNotDraw(false);
        
	}
    public void setfonta() {
    	ra= "Posture A:";
        rb= "Posture B:";
        cola = "One";
        colb = "Two";
    }
    public void setfontb() {
    	ra= "Standing  (z):";
        rb= "Flexion  (z):";
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
    public boolean getcalibrating() {
    	return this.calibrating;
    }
    public void setcalibrating(boolean c1) {
    	this.calibrating = c1;
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
    public void setmode(int nmode) {
        this.mode=nmode;
     }
    public void setblank(boolean nblank) {
        this.isblank=nblank;
     }

    public void resetall() {    
        this.onea=0;
        this.oneb=0;
        this.twoa=0;
        this.twob=0;
        this.roma=0;
        this.romb=0;


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
    public void setspine(boolean nspine) {
        this.isspine=nspine;
    }
    public void setthreec(double tc) {
  //      if (tc != 0)
            this.threec=tc;
    }
    public void setthreed(double td) {
   //     if (td != 0)
            this.threed=td;
    }
    public void setfourc(double fc) {
    //    if (fc != 0)
            this.fourc=fc;
    }
    public void setfourd(double fd) {
     //   if (fd != 0)
            this.fourd=fd;
    }
    public void setrome() {
        this.romd=this.fourd-this.fourc;
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
    public void setromd() {
        this.romd=this.fourd-this.fourc;
    }
    public void setpitch(double angle1) {
        this.npitch=angle1;
     }
    public void setroll(double angle2) {    
        this.nroll=angle2;
     }
    public void setcroll(double cangle) {    
        this.croll=cangle;
     }

	@Override
    protected void onDraw(Canvas canvas){
		// A Simple Text Render to test the display
		if (this.isblank) {
			canvas.drawColor(0xFFFFFFFF);
		}
        int sscaledSize = getResources().getDimensionPixelSize(R.dimen.sfont_size);
        int esscaledSize = getResources().getDimensionPixelSize(R.dimen.esfont_size);
        Paint spaint = new Paint();
        spaint.setColor(Color.WHITE);
        spaint.setTypeface(Typeface.DEFAULT_BOLD);
        spaint.setTextSize(sscaledSize);

        Paint espaint = new Paint();
        espaint.setColor(Color.WHITE);
        espaint.setTypeface(Typeface.DEFAULT_BOLD);
        espaint.setTextSize(esscaledSize);

		int rollSize = getResources().getDimensionPixelSize(R.dimen.roll_size);
		Paint rollpaint = new Paint();
		 rollpaint.setColor(Color.BLACK);
		 rollpaint.setTypeface(Typeface.DEFAULT_BOLD); 
		 rollpaint.setTextSize(rollSize);
		int scaledSize = getResources().getDimensionPixelSize(R.dimen.font_size);	
		 Paint paint = new Paint();
		 paint.setColor(Color.WHITE);
		 paint.setTypeface(Typeface.DEFAULT_BOLD); 
		 paint.setTextSize(scaledSize);
		 int cwidth =canvas.getWidth();
		 int cheight=canvas.getHeight();
		 //draw text
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

        canvas.drawText("Region(degree):", xa1, ya1, paint);
        canvas.drawText(cola, xb1, yb1, paint);

        //new added
        if (isspine)
        {
            spaint.setColor(Color.WHITE);
            espaint.setColor(Color.WHITE);
        } else
        {
            spaint.setColor(Color.TRANSPARENT);
            espaint.setColor(Color.TRANSPARENT);
        }
        if (mode == 0 && isspine) {
            canvas.drawText("(" + String.valueOf(sround(threec)) + ")", xb25, yb25, spaint);
            canvas.drawText("(" + String.valueOf(sround(threed)) + ")", xb25, yc35, spaint);
            canvas.drawText("(" + String.valueOf(sround(fourc)) + ")", xc25, yb25, spaint);
            canvas.drawText("(" + String.valueOf(sround(fourd)) + ")", xc25, yc35, spaint);
            canvas.drawText("Ethnic_Gp:"+Ethnic_Gp, cwidth-dx5, cheight-dy5- dy3, espaint);
            canvas.drawText("Age:"+Age, cwidth-dx5, cheight-dy4- dy3, espaint);
            canvas.drawText("Gender:" + Gender, cwidth - dx5, cheight - dy3- dy3, espaint);
            canvas.drawText("Selected:" + Foe, cwidth - dx5, cheight- dy2- dy3, espaint);

            canvas.drawText("(" + String.valueOf(sround(threec)) + ")", xb25, yb25, stextborder);
            canvas.drawText("(" + String.valueOf(sround(threed)) + ")", xb25, yc35, stextborder);
            canvas.drawText("(" + String.valueOf(sround(fourc)) + ")", xc25, yb25, stextborder);
            canvas.drawText("(" + String.valueOf(sround(fourd)) + ")", xc25, yc35, stextborder);
            canvas.drawText("Ethnic_Gp:"+Ethnic_Gp, cwidth-dx5, cheight-dy5- dy3, estextborder);
            canvas.drawText("Age:"+Age, cwidth-dx5, cheight-dy4- dy3, estextborder);
            canvas.drawText("Gender:" + Gender, cwidth - dx5, cheight - dy3- dy3, estextborder);
            canvas.drawText("Selected:" + Foe, cwidth - dx5, cheight- dy2- dy3, estextborder);
        }
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
			//draw border
        canvas.drawText("Region(degree):", xa1, ya1, textborder);
        canvas.drawText(cola, xb1, yb1, textborder);
        if (mode == 0)
        canvas.drawText(colb, xc1, yc1, textborder);
       
        canvas.drawText(ra, xa2, ya2, textborder);
        canvas.drawText(sround((this.onea)), xb2, yb2, textborder);
        if (mode == 0)
        canvas.drawText(sround((this.twoa)), xc2, yc2, textborder);      
        canvas.drawText(rb, xa3, ya3, textborder);
        canvas.drawText(sround((this.oneb)), xb3, yb3, textborder);
        if (mode == 0)
        {       
        canvas.drawText(sround((this.twob)), xc3, yc3, textborder);
        }
       
        canvas.drawText("ROM:", xa4, ya4, textborder);
        canvas.drawText(sround((this.roma)), xb4, yb4, textborder);
        if (mode == 0)
        canvas.drawText(sround((this.romb)), xc4, yc4, textborder);
			//draw line
			canvas.drawLine(0, cheight / 2, cwidth, cheight / 2, textPaint);
        //testing by eddie
        float AX = (float) (Math.sin(30));
         //   canvas.drawLine(0,cheight,cwidth/2,0,textPaint);
        //   canvas.drawLine(0,cheight,cwidth,0,textPaint);
        //   canvas.drawLine(0,cheight,cwidth*((float) Math.sqrt(3)/2),0,textPaint);

       // canvas.drawLine(0,0,cwidth/2,cheight / 2,textPaint);
    //    canvas.drawLine(0,0,cwidth,cheight / 2,textPaint);
     //   canvas.drawLine(0,0,cwidth*((float) Math.sqrt(3)/2),cheight / 2,textPaint);
        float Sx, Sy, Ex, Ey,Slope=0;
        Sx = cwidth/2;
        Sy = cheight/2;
        Slope = dy5/(float)Math.cos(45 * Math.PI / 180);
        Ex=Slope*(float)Math.cos(45 * Math.PI / 180);
        Ey=Slope*(float)Math.sin(45 * Math.PI / 180);
        canvas.drawLine(Sx, Sy,Sx+Ex, Sy-Ey,textPaint);

        Slope = dy5/(float)Math.cos(30 * Math.PI / 180);
        Ex=Slope*(float)Math.cos(30 * Math.PI / 180);
        Ey=Slope*(float)Math.sin(30 * Math.PI / 180);
        canvas.drawLine(Sx, Sy,Sx+Ex, Sy-Ey,textPaint);

        Slope = dy5/(float)Math.sin(60 * Math.PI / 180);
        Ex=Slope*(float)Math.cos(60 * Math.PI / 180);
        Ey=Slope*(float)Math.sin(60 * Math.PI / 180);
        canvas.drawLine(Sx, Sy,Sx+Ex, Sy-Ey,textPaint);

        textPaint.setStyle(Paint.Style.STROKE);
                 canvas.drawRect(cwidth / 2, cheight / 2,cwidth / 2+dy5, cheight/2-dy5, textPaint);
			//draw radio button white
			int scaledx = getResources().getDimensionPixelSize(R.dimen.white_x);	
            int scaledy = getResources().getDimensionPixelSize(R.dimen.white_y);
            canvas.drawText("Bkgd", scaledx, scaledy, paint);
            canvas.drawText("Bkgd", scaledx, scaledy, textborder);


	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		String touchx = String.valueOf(event.getX());
		calibrating = true;		
	//	Toast.makeText(this.getContext(), touchx, 10000).show();
	    return false;
	}
    public drawviewthread getThread() {
        return drawer;
    }
    
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

		myButton1.setTag("btn1");
		myButton1.setText("Button added dynamically!");
		//alParent.addView(b);
		myButton1.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	Toast.makeText(ct, "abc", Toast.LENGTH_SHORT).show();
            }  
        });        
		 if (!drawer.isAlive()) {
	            drawer = new drawviewthread(this);
	        }
	        drawer.setRunning(true);
	        drawer.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
		 boolean retry = true;
	        drawer.setRunning(false);
	        while (retry) {
	            try {
	                drawer.join();
	                retry = false;
	                // free resources
	                System.gc();
	            } catch (InterruptedException e) {
	                // we will try it again and again...
	            }
	        }
	        Log.i("thread", "Thread terminated...");	    
	}
}
