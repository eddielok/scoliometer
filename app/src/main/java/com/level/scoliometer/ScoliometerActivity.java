package com.level.scoliometer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.level.scoliometer.models.MSKITEM;
import com.level.scoliometer.models.tblMSKGROUP;
import com.level.scoliometer.utils.*;

public class ScoliometerActivity extends Activity implements DownloadResultReceiver.Receiver, OrientationListener {

    private static Context CONTEXT;
    public Panel _panel;
    private int count = 0;
    private ArrayAdapter arrayAdapter = null;
    private DownloadResultReceiver mReceiver;
    private static final double MAX_SINUS = Math.sin(Math.PI / 2);
    private static final int DIALOG_EXPORT_ID = 1;
    private static final int DIALOG_MODE_ID = 2;
    private static final int DIALOG_DISPLAY_ID = 3;
    private static final int DIALOG_FONT_ID = 4;
    private static final int TOAST_DURATION = Toast.LENGTH_SHORT;
    private boolean calibrating;
    private static final String SAVED_PITCH = "scoliometer.pitch";
    private static final String SAVED_ROLL = "scoliometer.roll";
    private int mode = 0;
    private int displays = 0;
    private int font = 0;
    private int onblank = 0;
    private int spine = 0;
    private Toast toasts;
    private boolean notfetched =true;
    //cameria object
    private Camera mCamera;
    cameraPreview cv;
    public drawView dv;
    FrameLayout alParent;
    //create toast handler
    private Handler thandler = null;
    private MSKITEM mskitem = null;
    public static final String PREFS_Thoracic = "Thoracic";
    public static final String PREFS_Lumbar = "Lumbar";
    public static final String PREFS_Standing = "Standing";
    public static final String PREFS_Flexion = "Flexion";
    public static final String PREFS_Extension = "Extension";
    public static final String PREFS_Standing_MEAN = "StandingM";
    public static final String PREFS_Standing_SD = "StandingSD";
    public static final String PREFS_Flexion_MEAN = "FlexionM";
    public static final String PREFS_Flexion_SD = "FlexionSD";
    public String vfilename = "version.json";
    public String sfilename = "MSKROM.json";
    public String tempversion= "";
    private SharedPreference sharedPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        getversion();
        dv = new drawView(this);
        _panel = (Panel) findViewById(R.id.level);
        OrientationManager.resetCalibration();
        calibrating = false;
        CONTEXT = this;
        //add button
        Button btnrestart = (Button) findViewById(R.id.restart);
        btnrestart.setTextColor(Color.BLACK);
        btnrestart.setText("Restart");
        OnClickListener btnrestart_listener = new OnClickListener (){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                _panel.resetall();
                OrientationManager.resetCalibration();
                count=0;
                SetresultfromPREFS();
                SetalldvfromPREFS();
                Toast.makeText(CONTEXT, "Calibration Clear.", TOAST_DURATION).show();
            }
        };
        btnrestart.setOnClickListener(btnrestart_listener);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (OrientationManager.isSupported()) {
            OrientationManager.startListening(this);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (OrientationManager.isListening()) {
            OrientationManager.stopListening();
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.Export:
                showDialog(DIALOG_EXPORT_ID);
                return true;
            case R.id.Mode:
                showDialog(DIALOG_MODE_ID);
                return true;
            case R.id.Display:
                showDialog(DIALOG_DISPLAY_ID);
                return true;
            case R.id.Font:
                showDialog(DIALOG_FONT_ID);
                return true;
        }
        return false;
    }
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch(id) {
            case DIALOG_EXPORT_ID:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText input = new EditText(this);
                builder.setView(input);
                builder.setTitle(R.string.export_title)
                        .setIcon(null)
                        .setCancelable(true)
                        .setPositiveButton(R.string.export, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Editable val = input.getText();
                                generateNoteOnSD("Export_" + val.toString() + ".txt", val.toString());
                                ScoliometerActivity.this.dismissDialog(DIALOG_EXPORT_ID);
                                calibrating = true;
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ScoliometerActivity.this.dismissDialog(DIALOG_EXPORT_ID);
                            }
                        })
                        .setNeutralButton(R.string.reset, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                OrientationManager.resetCalibration();
                                ScoliometerActivity.this.saveCalibration(0, 0, true);
                            }
                        })
                        .setMessage(R.string.export_message);
                dialog = builder.create();
                break;
            case DIALOG_MODE_ID:
                AlertDialog.Builder mode_builder = new AlertDialog.Builder(this);
                mode_builder.setTitle(R.string.mode_title)

                        .setNegativeButton(R.string.djm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                _panel.setspine(false);
                                dv.setspine(false);
                                if (displays == 0) {
                                    _panel.resetall();
                                    OrientationManager.resetCalibration();
                                    count = 0;
                                    _panel.setmode(0);//for panel usage
                                    if (font == 1) {
                                        _panel.setfonta();
                                        font = 0;
                                    }
                                    mode = 0;//for activity usage
                                } else if (displays == 1) {
                                    mode = 0;
                                    dv.resetall();
                                    OrientationManager.resetCalibration();
                                    count = 0;
                                    dv.setmode(0);
                                    if (font == 1) dv.setfonta();
                                    dv.postInvalidate();        //refresh drawview
                                }
                            }
                        })
                        .setNeutralButton(R.string.sjm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                _panel.setspine(false);
                                if (displays == 0) {
                                    _panel.resetall();
                                    OrientationManager.resetCalibration();
                                    count = 0;
                                    _panel.setmode(1);//for panel usage
                                    if (font == 1) {
                                        _panel.setfonta();
                                        font = 0;
                                    }
                                    mode = 1;//for activity usage
                                } else if (displays == 1) {
                                    mode = 1;
                                    dv.resetall();
                                    OrientationManager.resetCalibration();
                                    count = 0;
                                    dv.setmode(1);
                                    if (font == 1) {
                                        dv.setfontc();

                                    }
                                    dv.postInvalidate();
                                }
                            }
                        })
                        .setMessage(R.string.jmtitle);
                dialog = mode_builder.create();
                break;
            case DIALOG_DISPLAY_ID:
                AlertDialog.Builder display_builder = new AlertDialog.Builder(this);
                display_builder.setTitle(R.string.display_title)
                        .setNegativeButton(R.string.general_display, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (displays != 0) {
                                    OrientationManager.resetCalibration();
                                    displays = 0;
                                    setContentView(R.layout.main);
                                    _panel = (Panel) findViewById(R.id.level);
                                    OrientationManager.resetCalibration();
                                    calibrating = false;

                                    _panel.setonea(dv.getonea());
                                    _panel.setoneb(dv.getoneb());
                                    _panel.settwoa(dv.gettwoa());
                                    _panel.settwob(dv.gettwob());


                                    _panel.setspine(dv.getspine());
                                    _panel.setEthnic_Gp(dv.getEthnic_Gp());
                                    _panel.setGender(dv.getGender());
                                    _panel.setAge(dv.getAge());
                                    _panel.setfoe(dv.getfoe());

                                    _panel.setthreec(dv.getthreec());
                                    _panel.setthreed(dv.getthreed());
                                    _panel.setfourc(dv.getfourc());
                                    _panel.setfourd(dv.getfourd());
                                    if (mode == 0) {
                                        _panel.setmode(0);//for panel usage
                                        if (font == 0) _panel.setfonta();
                                        else _panel.setfontb();
                                    } else {
                                        _panel.setmode(1);//for panel usage
                                        if (font == 0) _panel.setfonta();
                                        else _panel.setfontc();
                                    }
                                    if (dv.getonea() != 0 & dv.getoneb() != 0)
                                        _panel.setroma();
                                    if (dv.gettwoa() != 0 & dv.gettwob() != 0)
                                        _panel.setromb();

                                    //add button
                                    Button btnrestart = (Button) findViewById(R.id.restart);

                                    btnrestart.setText("Restart");
                                    OnClickListener btnrestart_listener = new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // TODO Auto-generated method stub
                                            _panel.resetall();
                                            OrientationManager.resetCalibration();
                                            count = 0;
                                            Toast.makeText(CONTEXT, "Calibration Clear.", TOAST_DURATION).show();
                                            SetresultfromPREFS();
                                            SetalldvfromPREFS();
                                        }
                                    };
                                    btnrestart.setOnClickListener(btnrestart_listener);
                                }
                            }
                        })

                        .setNeutralButton(R.string.camera_display, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (displays != 1) {
                                            displays = 1;
                                            OrientationManager.resetCalibration();
                                            // Try to get the camera
                                            Camera c = getCameraInstance();
                                            // If the camera was received, create the app
                                            if (c != null) {

                                                alParent = new FrameLayout(getContext());
                                                alParent.setLayoutParams(new LayoutParams(
                                                        LayoutParams.FILL_PARENT,
                                                        LayoutParams.FILL_PARENT));

                                                // Create a new camera view and add it to the layout
                                                cv = new cameraPreview(getContext(), c);

                                                alParent.addView(cv);
                                                // Create a new draw view and add it to the layout

                                                dv = new drawView(getContext());
                                                dv.setonea(_panel.getonea());
                                                dv.setoneb(_panel.getoneb());
                                                dv.settwoa(_panel.gettwoa());
                                                dv.settwob(_panel.gettwob());

                                                dv.setspine(_panel.getspine());
                                                dv.setEthnic_Gp(_panel.getEthnic_Gp());
                                                dv.setGender(_panel.getGender());
                                                dv.setAge(_panel.getAge());
                                                dv.setfoe(_panel.getfoe());

                                                dv.setthreec(_panel.getthreec());
                                                dv.setthreed(_panel.getthreed());
                                                dv.setfourc(_panel.getfourc());
                                                dv.setfourd(_panel.getfourd());

                                                if (mode == 0) {
                                                    dv.setmode(0);//for panel usage
                                                    if (font == 0) dv.setfonta();
                                                    else dv.setfontb();
                                                } else {
                                                    dv.setmode(1);//for panel usage
                                                    if (font == 0) dv.setfonta();
                                                    else dv.setfontc();
                                                }
                                                if (_panel.getonea() != 0 & _panel.getoneb() != 0)
                                                    dv.setroma();
                                                if (_panel.gettwoa() != 0 & _panel.gettwob() != 0)
                                                    dv.setromb();
                                                OrientationManager.resetCalibration();
                                                calibrating = false;
                                                alParent.addView(dv);
                                                //add radio button
                                                final RadioButton btn = new RadioButton(CONTEXT);
                                                int radioSize = getResources().getDimensionPixelSize(R.dimen.radio_size);
                                                //  btn.setTextColor(Color.WHITE);
                                                //  btn.setTextSize(radioSize);
                                                // btn.setText("White");


                                                OnClickListener first_radio_listener = new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        // TODO Auto-generated method stub
                                                        onblank++;
                                                        dv.setblank(true);
                                                        alParent.setBackgroundColor(Color.WHITE);
                                                        if (onblank > 1) {
                                                            btn.setChecked(false);
                                                            onblank = 0;
                                                            alParent.setBackgroundColor(Color.TRANSPARENT);
                                                            dv.setblank(false);
                                                        }
                                                    }
                                                };
                                                btn.setOnClickListener(first_radio_listener);
                                                //add button
                                                final Button btnrestart = new Button(CONTEXT);
                                                btnrestart.setTextColor(Color.BLUE);
                                                btnrestart.setText("Restart");
                                                btnrestart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                                OnClickListener btnrestart_listener = new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        // TODO Auto-generated method stub
                                                        dv.resetall();
                                                        OrientationManager.resetCalibration();
                                                        count = 0;
                                                        SetresultfromPREFS();
                                                        SetalldvfromPREFS();
                                                        dv.postInvalidate();
                                                        Toast.makeText(CONTEXT, "Calibration Clear.", TOAST_DURATION).show();
                                                    }
                                                };
                                                btnrestart.setOnClickListener(btnrestart_listener);
                                                RelativeLayout rt = new RelativeLayout(CONTEXT);
                                                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(100, 70);
                                                p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);//TableRow tr3=new TableRow(CONTEXT);
                                                int scaledx = getResources().getDimensionPixelSize(R.dimen.radio_x);
                                                int scaledy = getResources().getDimensionPixelSize(R.dimen.radio_y);
                                                rt.addView(btn, scaledx, scaledy);
                                                rt.addView(btnrestart, p);

                                                alParent.addView(rt);
                                                // Set the layout as the apps content view
                                                setContentView(alParent);
                                            }
                                            // If the camera was not received, close the app
                                            else {
                                                Toast toast = Toast.makeText(getApplicationContext(),
                                                        "Unable to find camera. Closing.", Toast.LENGTH_SHORT);
                                                toast.show();
                                                //finish();
                                            }


                                        }
                                    }
                                }
                        )
                        .setMessage(R.string.jmtitle);
                dialog = display_builder.create();
                break;
            case DIALOG_FONT_ID:
                AlertDialog.Builder font_builder = new AlertDialog.Builder(this);
                font_builder.setTitle(R.string.font_title)
                        .setNegativeButton(R.string.fonta, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                font = 0;
                                if (displays == 0)
                                {
                                    _panel.setfonta();
                                    _panel.setspine(false);
                                } else if (displays == 1){
                                    dv.setfonta();
                                    dv.setspine(false);
                                    dv.postInvalidate();
                                }
                            }
                        })
                        .setNeutralButton(R.string.fontb, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                LinearLayout layout = new LinearLayout(ScoliometerActivity.this);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                //   final EditText eg = new EditText(ScoliometerActivity.this);
                                //   eg.setHint("Ethnic Group");
                                //   layout.addView(eg);
                                final Spinner eg = new Spinner(ScoliometerActivity.this);
                                List<CharSequence> arreg = new ArrayList<CharSequence>();
                                arreg.add("Chinese");
                                arreg.add("Caucasian");
                                ArrayAdapter adaptereg = new ArrayAdapter<CharSequence>(ScoliometerActivity.this, android.R.layout.simple_spinner_item, arreg);
                                adaptereg.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                eg.setAdapter(adaptereg);
                                layout.addView(eg);


                                final Spinner age = new Spinner(ScoliometerActivity.this);
                                List<CharSequence> arrage = new ArrayList<CharSequence>();
                                arrage.add("1-9");
                                arrage.add("10-19");
                                arrage.add("20-29");
                                arrage.add("30-39");
                                arrage.add("40-49");
                                arrage.add("50-59");
                                arrage.add("60-69");
                                arrage.add("70-79");
                                arrage.add("80-89");
                                arrage.add("90+");
                                ArrayAdapter adapterage = new ArrayAdapter<CharSequence>(ScoliometerActivity.this, android.R.layout.simple_spinner_item, arrage);
                                adapterage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                age.setAdapter(adapterage);
                                layout.addView(age);

                                final Spinner gender = new Spinner(ScoliometerActivity.this);
                                List<CharSequence> arrgender = new ArrayList<CharSequence>();
                                arrgender.add("M");
                                arrgender.add("F");
                                ArrayAdapter adapter = new ArrayAdapter<CharSequence>(ScoliometerActivity.this, android.R.layout.simple_spinner_item, arrgender);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                gender.setAdapter(adapter);
                                layout.addView(gender);

                                final Spinner FoE = new Spinner(ScoliometerActivity.this);
                                List<CharSequence> arrFoE = new ArrayList<CharSequence>();
                                arrFoE.add("Extension");
                                arrFoE.add("Flexion");
                                arrFoE.add("Standing");
                                ArrayAdapter adapter2 = new ArrayAdapter<CharSequence>(ScoliometerActivity.this, android.R.layout.simple_spinner_item, arrFoE);
                                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                FoE.setAdapter(adapter2);
                                layout.addView(FoE);

                                AlertDialog.Builder tb2 = new AlertDialog.Builder(ScoliometerActivity.this);
                                tb2.setView(layout);
                                tb2.setTitle("Spine Mode")
                                        //.setMessage("Saved!")
                                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        if (checkPrefs()) notfetched = true;
                                                        // eg.setText("China");
                                                        //  age.setText("10");
                                                        _panel.setspine(true);
                                                        _panel.setAge(age.getSelectedItem().toString());
                                                        _panel.setEthnic_Gp(eg.getSelectedItem().toString());
                                                        _panel.setGender(gender.getSelectedItem().toString());
                                                        _panel.setfoe(FoE.getSelectedItem().toString());

                                                        dv.setspine(true);
                                                        dv.setAge(age.getSelectedItem().toString());
                                                        dv.setEthnic_Gp(eg.getSelectedItem().toString());
                                                        dv.setGender(gender.getSelectedItem().toString());
                                                        dv.setfoe(FoE.getSelectedItem().toString());
                                                        clearPrefs();
                                                        notfetched = true;
                                                        if (notfetched) {
                                                            //eddie
                                                            String jsons = Utils.readFromFile(sfilename, ScoliometerActivity.this);
                                                            int age1 = 0;
                                                            int age2 = 0;
                                                            switch (age.getSelectedItemPosition()) {
                                                                case (0):
                                                                    age1 = 1;
                                                                    age2 = 9;
                                                                    break;
                                                                case (1):
                                                                    age1 = 10;
                                                                    age2 = 19;
                                                                    break;
                                                                case (2):
                                                                    age1 = 20;
                                                                    age2 = 29;
                                                                    break;
                                                                case (3):
                                                                    age1 = 30;
                                                                    age2 = 39;
                                                                    break;
                                                                case (4):
                                                                    age1 = 40;
                                                                    age2 = 49;
                                                                    break;
                                                                case (5):
                                                                    age1 = 50;
                                                                    age2 = 59;
                                                                    break;
                                                                case (6):
                                                                    age1 = 60;
                                                                    age2 = 69;
                                                                    break;
                                                                case (7):
                                                                    age1 = 70;
                                                                    age2 = 79;
                                                                    break;
                                                                case (8):
                                                                    age1 = 80;
                                                                    age2 = 89;
                                                                    break;
                                                                case (9):
                                                                    age1 = 90;
                                                                    age2 = 999;
                                                                    break;
                                                            }
                                                            Gson gson = new Gson();
                                                            try {

                                                                // Type listType = new TypeToken<List<String>>() {}.getType();
                                                                //  Type MSKITEMType = new TypeToken<List<MSKITEM>>() {}.getType();
                                                                //  String tson = gson.toJson(jsons, MSKITEMType);
                                                                List<MSKITEM> MSKITEMs = new ArrayList<MSKITEM>();
                                                                MSKITEMs = Arrays.asList(gson.fromJson(jsons, MSKITEM[].class));

                                                                if (MSKITEMs == null) {
                                                                    Toast.makeText(ScoliometerActivity.this, R.string.noresult, Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    for (int i = 0; i < MSKITEMs.size() - 1; i++) {
                                                                        if (MSKITEMs.get(i).getAgeGroup1().equals(String.valueOf(age1)))
                                                                            if (MSKITEMs.get(i).getAgeGroup2().equals(String.valueOf(age2)))
                                                                                if (MSKITEMs.get(i).getEthnic_Gp().equals(eg.getSelectedItem().toString()))
                                                                                    if (MSKITEMs.get(i).getGender().equals(gender.getSelectedItem().toString())) {
                                                                                        if (MSKITEMs.get(i).getPosture_Movement().equals(PREFS_Standing) && MSKITEMs.get(i).getRegion().equals(PREFS_Thoracic)) {
                                                                                            sharedPreference.save(ScoliometerActivity.this, MSKITEMs.get(i).getMean(), PREFS_Standing_MEAN, PREFS_Thoracic);
                                                                                            sharedPreference.save(ScoliometerActivity.this, MSKITEMs.get(i).getStandard_Deviation(), PREFS_Standing_SD, PREFS_Thoracic);
                                                                                        } else if (MSKITEMs.get(i).getPosture_Movement().equals(PREFS_Standing) && MSKITEMs.get(i).getRegion().equals(PREFS_Lumbar)) {
                                                                                            sharedPreference.save(ScoliometerActivity.this, MSKITEMs.get(i).getMean(), PREFS_Standing_MEAN, PREFS_Lumbar);
                                                                                            sharedPreference.save(ScoliometerActivity.this, MSKITEMs.get(i).getStandard_Deviation(), PREFS_Standing_SD, PREFS_Lumbar);
                                                                                        } else if (MSKITEMs.get(i).getPosture_Movement().equals(PREFS_Flexion) && MSKITEMs.get(i).getRegion().equals(PREFS_Thoracic)) {
                                                                                            sharedPreference.save(ScoliometerActivity.this, MSKITEMs.get(i).getMean(), PREFS_Flexion_MEAN, PREFS_Thoracic);
                                                                                            sharedPreference.save(ScoliometerActivity.this, MSKITEMs.get(i).getStandard_Deviation(), PREFS_Flexion_SD, PREFS_Thoracic);
                                                                                        } else if (MSKITEMs.get(i).getPosture_Movement().equals(PREFS_Flexion) && MSKITEMs.get(i).getRegion().equals(PREFS_Lumbar)) {
                                                                                            sharedPreference.save(ScoliometerActivity.this, MSKITEMs.get(i).getMean(), PREFS_Flexion_MEAN, PREFS_Lumbar);
                                                                                            sharedPreference.save(ScoliometerActivity.this, MSKITEMs.get(i).getStandard_Deviation(), PREFS_Flexion_SD, PREFS_Lumbar);
                                                                                        } else if (MSKITEMs.get(i).getPosture_Movement().equals(PREFS_Extension) && MSKITEMs.get(i).getRegion().equals(PREFS_Thoracic)) {
                                                                                            sharedPreference.save(ScoliometerActivity.this, MSKITEMs.get(i).getMean(), PREFS_Flexion_MEAN, PREFS_Thoracic);
                                                                                            sharedPreference.save(ScoliometerActivity.this, MSKITEMs.get(i).getStandard_Deviation(), PREFS_Flexion_SD, PREFS_Thoracic);
                                                                                        } else if (MSKITEMs.get(i).getPosture_Movement().equals(PREFS_Extension) && MSKITEMs.get(i).getRegion().equals(PREFS_Lumbar)) {
                                                                                            sharedPreference.save(ScoliometerActivity.this, MSKITEMs.get(i).getMean(), PREFS_Flexion_MEAN, PREFS_Lumbar);
                                                                                            sharedPreference.save(ScoliometerActivity.this, MSKITEMs.get(i).getStandard_Deviation(), PREFS_Flexion_SD, PREFS_Lumbar);
                                                                                        }

                                                                                    }
                                                                    }
                                                                }
                                                                notfetched = false;
                                                                Thread.sleep(200);
                                                                SetresultfromPREFS();
                                                                SetalldvfromPREFS();
                                                                if (FoE.getSelectedItem().toString() == PREFS_Extension) {
                                                                    dv.setfontSE();
                                                                    _panel.setfontSE();
                                                                } else {
                                                                    dv.setfontb();
                                                                    _panel.setfontb();
                                                                }
                                                                dv.postInvalidate();
                                                                _panel.postInvalidate();
                                                            } catch (Exception e) {
                                                                Toast.makeText(ScoliometerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                            //  ScoliometerActivity Sactivity = new ScoliometerActivity();

                                                            // Sactivity._panel.setthreec((Sactivity._panel.getonea() - Double.parseDouble(mskitem.getMean())));
                                                            //  Sactivity._panel.postInvalidate();
                                                        } //else {
                                                        //  SetresultfromPREFS();
                                                        //  SetalldvfromPREFS();
                                                        //  dv.postInvalidate();
                                                        //  _panel.postInvalidate();
                                                        // }
                                                    }
                                                }
                                        )
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                //ScoliometerActivity.this.dismissDialog;
                                            }
                                        })
                                        .show();
                                font = 1;
                                if (displays == 0) {
                                    if (mode == 0)
                                        _panel.setfontb();
                                    else
                                        _panel.setfontc();
                                } else if (displays == 1) {
                                    if (mode == 0)
                                        dv.setfontb();
                                    else
                                        dv.setfontc();
                                    dv.postInvalidate();
                                }
                            }
                        })
                        .setMessage(R.string.fonttitle);
                dialog = font_builder.create();
                break;
            default:
                dialog = null;
        }
        return dialog;
    }
    public void generateNoteOnSD(String sFileName, String sBody){
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "Scoliometer");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("Name: " + sBody);
            writer.append("\r\n");
            writer.append("Posture A (One): " + _panel.getonea());
            writer.append("\r\n");
            writer.append("Posture A (Two): " + _panel.getoneb());
            writer.append("\r\n");
            writer.append("ROM one: " + _panel.getroma());
            writer.append("\r\n");
            writer.append("Posture B (One): " + _panel.gettwoa());
            writer.append("\r\n");
            writer.append("Posture B (Two): " + _panel.gettwob());
            writer.append("\r\n");
            writer.append("ROM Two: " + _panel.getromb());
            writer.flush();
            writer.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }
    public static Context getContext() {
        return CONTEXT;
    }
    private void saveCalibration(float pitch, float roll, boolean reset) {
        Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        if (pitch > -0.1 && pitch <= 0) pitch = 0;
        editor.putFloat(SAVED_PITCH, pitch);
        editor.putFloat(SAVED_ROLL, roll);
        int id = R.string.calibrate_failed;

        if (editor.commit()) {
            OrientationManager.setCalibration(pitch, roll);
            if (reset) {
                id = R.string.calibrate_restored;
            } else {
                if (mode == 0)
                {
                    if (count == 0 | count == 2 | count == 4 | count == 6 )
                    {
                        id = R.string.calibrate_saved;

                        final Toast toast = Toast.makeText(this, id, Toast.LENGTH_SHORT);
                        //eddie
                        thandler = new Handler(CONTEXT.getMainLooper());
                        thandler.post(toastThread);
                        Thread timeThread = new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                stopToast();

                            }
                        };
                        timeThread.start();
                    } else stopToast();
                }
                else if (mode == 1)
                {
                    if (count == 0 | count == 2 )
                    {{id = R.string.calibrate_saved;

                        final Toast toast = Toast.makeText(this, id, Toast.LENGTH_SHORT);
                        toast.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 100);
                    }
                    }
                }

            }

        }

    }
    @Override
    public void onOrientationChanged(float azimuth,
                                     float pitch, float roll, float croll) {
        if (displays == 1)
        {
            if ( dv.getcalibrating()) {
                if (mode == 0) {
                    dv.setcalibrating(false);

                    saveCalibration(pitch, roll, false);
                    switch(count) {
                        case 1:
                            dv.setonea(-croll);
                            if (dv.isspine) SetdvfromPREFS(1);
                            break;
                        case 3:
                            dv.settwoa(-croll);
                            if (dv.isspine) SetdvfromPREFS(2);
                            break;
                        case 5:
                            dv.setoneb(-croll);
                            if (dv.isspine) SetdvfromPREFS(3);
                            break;
                        case 7:
                            dv.settwob(-croll);
                            if (dv.isspine) SetdvfromPREFS(4);
                            dv.setroma();
                            dv.setromb();
                            break;
                        case 8:
                            count = -1;
                            dv.resetall();
                            if (dv.isspine) SetalldvfromPREFS();
                            OrientationManager.resetCalibration();
                            break;
                    }
                    count += 1;
                    dv.postInvalidate();
                }
                else if (mode == 1)
                {
                    if ( dv.getcalibrating()) {
                        dv.setcalibrating(false);
                        saveCalibration(pitch, roll, false);
                        switch(count) {
                            case 1:
                                dv.setonea(-croll);
                                if (dv.isspine) SetdvfromPREFS(1);
                                break;
                            case 3:
                                dv.setoneb(-croll);
                                dv.setroma();
                                if (dv.isspine) SetdvfromPREFS(3);
                                break;
                            case 4:
                                count = -1;
                                dv.resetall();
                                OrientationManager.resetCalibration();
                                if (dv.isspine) SetalldvfromPREFS();
                                break;
                        }
                        count += 1;
                        dv.postInvalidate();
                    }
                }
            }
            if (dv != null)
            {
                dv.setroll(-roll);
            }
        }
        else if ( _panel.getcalibrating()) {
            if (mode == 0) {
                _panel.setcalibrating(false);
                saveCalibration(pitch, roll, false);

                switch(count) {
                    case 1:
                        _panel.setonea(-croll);
                        if (_panel.isspine) SetPartresultfromPREFS(1);
                        break;
                    case 3:
                        _panel.settwoa(-croll);
                        if (_panel.isspine) SetPartresultfromPREFS(2);
                        break;
                    case 5:
                        _panel.setoneb(-croll);
                        if (_panel.isspine) SetPartresultfromPREFS(3);
                        break;
                    case 7:
                        _panel.settwob(-croll);
                        if (_panel.isspine) SetPartresultfromPREFS(4);
                        _panel.setroma();
                        _panel.setromb();
                        break;
                    case 8:
                        count = -1;
                        _panel.resetall();
                        OrientationManager.resetCalibration();
                        if (_panel.isspine) SetresultfromPREFS();
                        break;
                }
                count += 1;
            }
            else if (mode == 1)
            {
                if ( _panel.getcalibrating()) {
                    _panel.setcalibrating(false);
                    saveCalibration(pitch, roll, false);
                    switch(count) {
                        case 1:
                            _panel.setonea(-croll);
                            if (_panel.isspine) SetPartresultfromPREFS(1);
                            break;
                        case 3:
                            _panel.setoneb(-croll);
                            _panel.setromc();
                            if (_panel.isspine) SetPartresultfromPREFS(3);

                            break;
                        case 4:
                            count = -1;
                            _panel.resetall();
                            OrientationManager.resetCalibration();
                            if (_panel.isspine) SetresultfromPREFS();
                            break;
                    }
                    count += 1;
                }
            }
        }
        if (_panel != null)
        {
            _panel.setpitch(pitch);
            _panel.setroll(roll);
            if (count == 1 || count == 3 || count == 5 || count == 7)
                _panel.setcroll(croll);
            else
                _panel.setcroll(roll);
            _panel.setazimuth(azimuth);
        }
    }
    /* This method is strait for the Android API */
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;

        try {

            c = Camera.open();// attempt to get a Camera instance

        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
            //Toast.makeText(CONTEXT, "Fa, TOAST_DURATION).show();
        }
        return c; // returns null if camera is unavailable
    }
    private Runnable toastThread = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int id = R.string.calibrate_saved;
            toasts = Toast.makeText(CONTEXT, id, Toast.LENGTH_SHORT);
            toasts.show();
            //thandler.postDelayed(toastThread, 300);


        }

    };
    public void stopToast() {
        thandler.removeCallbacks(toastThread);
        toasts.cancel();
    }

    public void setcount(int c) {
        this.count = c;
    }

    public void SetresultfromPREFS() {
        sharedPreference = new SharedPreference();
        String MEAN="";
        String SD="";
        MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_MEAN, PREFS_Thoracic);
        SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_SD, PREFS_Thoracic);
        if (SD.equals("0")) _panel.setthreec(0);
        else _panel.setthreec((_panel.getonea() - Double.parseDouble(MEAN)) / Double.parseDouble(SD));

        MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_MEAN, PREFS_Thoracic);
        SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_SD, PREFS_Thoracic);
        if (SD.equals("0")) _panel.setthreed(0);
        else _panel.setthreed((_panel.getoneb()-Double.parseDouble(MEAN))/Double.parseDouble(SD));

        MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_MEAN, PREFS_Lumbar);
        SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_SD, PREFS_Lumbar);
        if (SD.equals("0")) _panel.setfourc(0);
        else _panel.setfourc((_panel.gettwoa()-Double.parseDouble(MEAN))/Double.parseDouble(SD));

        MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_MEAN, PREFS_Lumbar);
        SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_SD, PREFS_Lumbar);
        if (SD.equals("0")) _panel.setfourd(0);
        else _panel.setfourd((_panel.gettwob()-Double.parseDouble(MEAN))/Double.parseDouble(SD));
    }
    public void SetalldvfromPREFS() {
        sharedPreference = new SharedPreference();
        String MEAN="";
        String SD="";
        MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_MEAN, PREFS_Thoracic);
        SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_SD, PREFS_Thoracic);
        if (SD.equals("0")) dv.setthreec(0);
        else dv.setthreec((dv.getonea() - Double.parseDouble(MEAN)) / Double.parseDouble(SD));

        MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_MEAN, PREFS_Thoracic);
        SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_SD, PREFS_Thoracic);
        if (SD.equals("0")) dv.setthreed(0);
        else dv.setthreed((dv.getoneb()-Double.parseDouble(MEAN))/Double.parseDouble(SD));

        MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_MEAN, PREFS_Lumbar);
        SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_SD, PREFS_Lumbar);
        if (SD.equals("0")) dv.setfourc(0);
        else dv.setfourc((dv.gettwoa()-Double.parseDouble(MEAN))/Double.parseDouble(SD));

        MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_MEAN, PREFS_Lumbar);
        SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_SD, PREFS_Lumbar);
        if (SD.equals("0")) dv.setfourd(0);
        else dv.setfourd((dv.gettwob()-Double.parseDouble(MEAN))/Double.parseDouble(SD));
    }
    public void SetPartresultfromPREFS(int PrefId) {
        sharedPreference = new SharedPreference();
        String MEAN="";
        String SD="";
        switch (PrefId) {
            case 1:
                MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_MEAN, PREFS_Thoracic);
                SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_SD, PREFS_Thoracic);
                if (SD.equals("0")) _panel.setthreec(0);
                else _panel.setthreec((_panel.getonea() - Double.parseDouble(MEAN)) / Double.parseDouble(SD));
                break;
            case 3:
                MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_MEAN, PREFS_Thoracic);
                SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_SD, PREFS_Thoracic);
                if (SD.equals("0")) _panel.setthreed(0);
                else _panel.setthreed((_panel.getoneb()-Double.parseDouble(MEAN))/Double.parseDouble(SD));
                break;
            case 2:
                MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_MEAN, PREFS_Lumbar);
                SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_SD, PREFS_Lumbar);
                if (SD.equals("0")) _panel.setfourc(0);
                else _panel.setfourc((_panel.gettwoa()-Double.parseDouble(MEAN))/Double.parseDouble(SD));
                break;
            case 4:
                MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_MEAN, PREFS_Lumbar);
                SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_SD, PREFS_Lumbar);
                if (SD.equals("0")) _panel.setfourd(0);
                else _panel.setfourd((_panel.gettwob()-Double.parseDouble(MEAN))/Double.parseDouble(SD));
                break;
        }
    }
    public void SetdvfromPREFS(int PrefId) {
        sharedPreference = new SharedPreference();
        String MEAN="";
        String SD="";
        switch (PrefId) {
            case 1:
                MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_MEAN, PREFS_Thoracic);
                SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_SD, PREFS_Thoracic);
                if (SD.equals("0")) dv.setthreec(0);
                else dv.setthreec((dv.getonea() - Double.parseDouble(MEAN)) / Double.parseDouble(SD));
                break;
            case 3:
                MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_MEAN, PREFS_Thoracic);
                SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_SD, PREFS_Thoracic);
                if (SD.equals("0")) _panel.setthreed(0);
                else dv.setthreed((dv.getoneb()-Double.parseDouble(MEAN))/Double.parseDouble(SD));
                break;
            case 2:
                MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_MEAN, PREFS_Lumbar);
                SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_SD, PREFS_Lumbar);
                if (SD.equals("0")) dv.setfourc(0);
                else dv.setfourc((dv.gettwoa()-Double.parseDouble(MEAN))/Double.parseDouble(SD));
                break;
            case 4:
                MEAN = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_MEAN, PREFS_Lumbar);
                SD = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_SD, PREFS_Lumbar);
                if (SD.equals("0")) dv.setfourd(0);
                else dv.setfourd((dv.gettwob()-Double.parseDouble(MEAN))/Double.parseDouble(SD));
                break;
        }
    }
    public void clearPrefs() {
        sharedPreference = new SharedPreference();
        sharedPreference.save(ScoliometerActivity.this, "0", PREFS_Standing_MEAN, PREFS_Thoracic);
        sharedPreference.save(ScoliometerActivity.this, "0", PREFS_Standing_SD, PREFS_Thoracic);
        sharedPreference.save(ScoliometerActivity.this, "0", PREFS_Flexion_MEAN, PREFS_Lumbar);
        sharedPreference.save(ScoliometerActivity.this, "0", PREFS_Flexion_SD, PREFS_Lumbar);
        sharedPreference.clearSharedPreference(ScoliometerActivity.this, PREFS_Thoracic);
        sharedPreference.clearSharedPreference(ScoliometerActivity.this, PREFS_Lumbar);
        _panel.setthreec(0);
        _panel.setthreed(0);
        _panel.setfourc(0);
        _panel.setfourd(0);
        dv.setthreec(0);
        dv.setthreed(0);
        dv.setfourc(0);
        dv.setfourd(0);
    }
    public Boolean checkPrefs() {
        sharedPreference = new SharedPreference();
        String Temp1 = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_MEAN, PREFS_Thoracic);
        String Temp2 = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_SD, PREFS_Thoracic);

        String Temp3 =sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_MEAN, PREFS_Thoracic);
        String Temp4 = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_SD, PREFS_Thoracic);

        String Temp5 =sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_MEAN, PREFS_Lumbar);
        String Temp6 =sharedPreference.getValue(ScoliometerActivity.this, PREFS_Standing_SD, PREFS_Lumbar);

        String Temp7 =sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_MEAN, PREFS_Lumbar);
        String Temp8 = sharedPreference.getValue(ScoliometerActivity.this, PREFS_Flexion_SD, PREFS_Lumbar);
        return (Temp1.equals("0") && Temp2.equals("0") && Temp3.equals("0") && Temp4.equals("0") && Temp5.equals("0") && Temp6.equals("0") && Temp7.equals("0") && Temp8.equals("0") );
    }
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadService.STATUS_RUNNING:

                setProgressBarIndeterminateVisibility(true);
                break;
            case DownloadService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */
                setProgressBarIndeterminateVisibility(false);
                // clearPrefs();
                try {
                    String[] results = resultData.getStringArray("result");
                    sharedPreference = new SharedPreference();
                    // Save the parse result in SharedPreference
                    if (results[2].equalsIgnoreCase(PREFS_Thoracic)) {
                        if (results[3].equalsIgnoreCase(PREFS_Standing)) {
                            sharedPreference.save(ScoliometerActivity.this, results[0], PREFS_Standing_MEAN, PREFS_Thoracic);
                            sharedPreference.save(ScoliometerActivity.this, results[1], PREFS_Standing_SD, PREFS_Thoracic);
                        } else {
                            sharedPreference.save(ScoliometerActivity.this, results[0], PREFS_Flexion_MEAN, PREFS_Thoracic);
                            sharedPreference.save(ScoliometerActivity.this, results[1], PREFS_Flexion_SD, PREFS_Thoracic);
                        }
                    } else {
                        if (results[3].equalsIgnoreCase(PREFS_Standing)) {

                            sharedPreference.save(ScoliometerActivity.this, results[0], PREFS_Standing_MEAN, PREFS_Lumbar);
                            sharedPreference.save(ScoliometerActivity.this, results[1], PREFS_Standing_SD, PREFS_Lumbar);
                        } else {

                            sharedPreference.save(ScoliometerActivity.this, results[0], PREFS_Flexion_MEAN, PREFS_Lumbar);
                            sharedPreference.save(ScoliometerActivity.this, results[1], PREFS_Flexion_SD, PREFS_Lumbar);
                        }
                    }
                    notfetched = false;
                    Thread.sleep(200);
                    SetresultfromPREFS();
                    SetalldvfromPREFS();
                    dv.postInvalidate();
                    _panel.postInvalidate();
                    if (checkPrefs()) Toast.makeText(this, "No data fetched from Server", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(this, "Database Updated", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    break;
                }

                break;
            case DownloadService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, "Server Connection Failed. Unable to get Z-score.", Toast.LENGTH_LONG).show();
                break;
        }
    }
    public void getversion() {

        if (Utils.isOnline(ScoliometerActivity.this)) {
            RequestTask task = new RequestTask(vfilename, ScoliometerActivity.this);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Constants.vURL);
            task.execute(stringBuilder.toString());
        } else {
            Toast.makeText(ScoliometerActivity.this, "No Internet access found.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    public void getMSKROMdetail() {
        vfilename = "MSKROM.json";
        if (Utils.isOnline(ScoliometerActivity.this)) {
            RequestTask task = new RequestTask(vfilename, ScoliometerActivity.this);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Constants.cURL);
            task.execute(stringBuilder.toString());
        } else {
            Toast.makeText(ScoliometerActivity.this, "No Internet access found.",
                    Toast.LENGTH_SHORT).show();
        }
    }

}