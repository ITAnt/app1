package settings.jancar.com.touchlearning;

import android.Manifest;
import android.annotation.SuppressLint;
import android.icu.util.ValueIterator;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kongqw.permissionslibrary.PermissionsManager;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    Button touch_one, touch_tow, touch_three, touch_four, touch_fives;
    float[] location_one, location_tow, location_three, location_four, location_fives;
    float[] locations_one, locations_tow, locations_three, locations_four, locations_fives;
    RelativeLayout tabMode;
    boolean isClick;
    PermissionsManager   mPermissionsManager;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        touch_one = (Button) findViewById(R.id.touch_one);
        touch_tow = (Button) findViewById(R.id.touch_tow);
        touch_three = (Button) findViewById(R.id.touch_three);
        touch_four = (Button) findViewById(R.id.touch_four);
        touch_fives = (Button) findViewById(R.id.touch_fives);
        tabMode = (RelativeLayout) findViewById(R.id.tabMode);
        //   tabMode.setOnTouchListener(this);
        touch_one.setOnTouchListener(this);
        touch_tow.setOnTouchListener(this);
        touch_three.setOnTouchListener(this);
        touch_four.setOnTouchListener(this);
        touch_fives.setOnTouchListener(this);
        location_one = new float[2];
        location_tow = new float[2];
        location_three = new float[2];
        location_four = new float[2];
        location_fives = new float[2];
        locations_one = new float[2];
        locations_tow = new float[2];
        locations_three = new float[2];
        locations_four = new float[2];
        locations_fives = new float[2];


    }

    View m;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float x2 = event.getX();
                float y2 = event.getY();
                float x = event.getX();
                float y = event.getY();
                if (m != null) {
                }
                if (isClick) {
                    switch (m.getId()) {
                        case R.id.touch_one:
                            if (x1 > touch_one.getWidth() / 2) {
                                x = x - (x1 - touch_one.getWidth() / 2);
                            } else {
                                x = x + (touch_one.getWidth() / 2 - x1);
                            }
                            if (y1 > touch_one.getHeight() / 2) {
                                y = y - (y1 - touch_one.getHeight() / 2);
                            } else {
                                y = y + (touch_one.getHeight() / 2 - y1);
                            }
                            locations_one[0] = 39;
                            locations_one[1] = 14;
                            location_one[0] = 38.961952f;
                            location_one[1] = 13.976706f;
                            isClick=false;
                            // y=y-touch_one.getHeight();
                            break;
                        case R.id.touch_tow:
                            if (x1 > touch_tow.getWidth() / 2) {
                                x = x - (x1 - touch_tow.getWidth() / 2);
                            } else {
                                x = x + (touch_tow.getWidth() / 2 - x1);
                            }
                            if (y1 > touch_tow.getHeight() / 2) {
                                y = y - (y1 - touch_tow.getHeight() / 2);
                            } else {
                                y = y + (touch_tow.getHeight() / 2 - y1);
                            }
                            locations_tow[0] = 49;
                            locations_tow[1] = 595;
                            location_tow[0] = 48.952194f;
                            location_tow[1] = 594.00995f;
                            isClick=false;
                            break;
                        case R.id.touch_three:
                            if (x1 > touch_three.getWidth() / 2) {
                                x = x - (x1 - touch_three.getWidth() / 2);
                            } else {
                                x = x + (touch_three.getWidth() / 2 - x1);
                            }
                            if (y1 > touch_three.getHeight() / 2) {
                                y = y - (y1 - touch_three.getHeight() / 2);
                            } else {
                                y = y + (touch_three.getHeight() / 2 - y1);
                            }
                            locations_three[0] = 996;
                            locations_three[1] = 15;
                            location_three[0] = 995.0283f;
                            location_three[1] = 14.975041f;
                            isClick=false;
                            break;
                        case R.id.touch_four:
                            if (x1 > touch_four.getWidth() / 2) {
                                x = x - (x1 - touch_four.getWidth() / 2);
                            } else {
                                x = x + (touch_four.getWidth() / 2 - x1);
                            }
                            if (y1 > touch_four.getHeight() / 2) {
                                y = y - (y1 - touch_four.getHeight() / 2);
                            } else {
                                y = y + (touch_four.getHeight() / 2 - y1);
                            }
                            locations_four[0] =989 ;
                            locations_four[1] =587 ;
                            location_four[0] = 988.0351f;
                            location_four[1] = 586.02325f;
                            isClick=false;
                            break;
                        case R.id.touch_fives:
                            if (x1 > touch_fives.getWidth() / 2) {
                                x = x - (x1 - touch_fives.getWidth() / 2);
                            } else {
                                x = x + (touch_fives.getWidth() / 2 - x1);
                            }
                            if (y1 > touch_fives.getHeight() / 2) {
                                y = y - (y1 - touch_fives.getHeight() / 2);
                            } else {
                                y = y + (touch_fives.getHeight() / 2 - y1);
                            }
                            locations_fives[0] = 504;
                            locations_fives[1] = 314;
                            location_fives[0] = 503.5083f;
                            location_fives[1] = 313.47754f;
                            floats.clear();
                            setClick(locations_one[0]* 65536 , locations_tow[0]* 65536 , locations_three[0]* 65536, locations_four[0]* 65536 , locations_fives[0]* 65536 ,
                                    location_one[0], location_one[1],location_tow[0], location_tow[1], location_three[0], location_three[1], location_four[0], location_four[1], location_fives[0], location_fives[1]
                            );
                            setClick(locations_one[1]* 65536 , locations_tow[1]* 65536 , locations_three[1]* 65536, locations_four[1]* 65536 , locations_fives[1]* 65536 ,
                                    location_one[0], location_one[1],location_tow[0], location_tow[1], location_three[0], location_three[1], location_four[0], location_four[1], location_fives[0], location_fives[1]
                            );
                            generate(floats.get(0), floats.get(1), floats.get(2), floats.get(3), floats.get(4), floats.get(5), 65536 + "");
                            //  setClick(locations_one[0]*65536,locations_tow[0]*65536,locations_three[0]*65536 ,location_one[0],location_one[1],location_tow[0],location_tow[1],location_three[1],location_three[1]);
                            isClick=false;
                            break;
                    }

                }
                Log.w("ACTION_UP", "x:" + x + "y:" + y);
                Log.w("ACTION_UP", "x2:" + x2 + "y2:" + y2);
                System.out.println("x:" + x + "y:" + y);

                break;
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            default:
                break;

        }
        return false;
    }

    List<String> floats = new ArrayList<>();
    public void setClick(float XL, float XL1, float XL2, float XL3, float XL4, float XT, float YT, float XT2, float YT2, float XT3, float YT3, float XT4, float YT4, float XT5, float YT5) {
        DecimalFormat df = new DecimalFormat("#");
        double[][] test = {{XT, YT, 1, XL}, {XT3, YT3, 1, XL2}, {XT5, YT5, 1, XL4}};
        ZgaussianEliminationMatrix2 c = new ZgaussianEliminationMatrix2();
        double[] res = c.caculate(test);
        for (double d : res) {
            Log.w("ACTION_UP", d + ",");
            System.out.println(df.format(d));
            floats.add(df.format(d));
        }
    }
   
    float x1;
    float y1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.w("ACTION_UP", "a:");
        x1 = event.getX();
        y1 = event.getY();
        m = v;
        isClick = true;
        return false;
    }

    public void generate(final String a, final String b, final String c, final String d, final String e, final String f, final String div) {
        File file = new File("/sys/devices/soc/soc:touch@");
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception r) {
            Log.i("error:", r + "");
        }
        File files = new File("/sys/devices/soc/soc:touch@/gt9xx_props");
        if (!files.exists()) {
            try {
                file.createNewFile();
            } catch (IOException r) {
// TODO Auto-generated catch block    
                r.printStackTrace();
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    File file = new File("/sys/devices/soc/soc:touch@/gt9xx_props");
                    String content = Integer.parseInt(a)+
                            " "+Integer.parseInt(b)+
                            " "+Integer.parseInt(c)
                            +" "+Integer.parseInt(d)
                            +" "+Integer.parseInt(e)
                            +" "+Integer.parseInt(f)
                            +" "+Integer.parseInt(div);

                    try (FileOutputStream fop = new FileOutputStream(file)) {

                        // if file doesn't exists, then create it
                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        // get the content in bytes
                        byte[] contentInBytes = content.getBytes();

                        fop.write(contentInBytes);
                        fop.flush();
                        fop.close();

                        System.out.println("Done");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();
        }
    }
}
