package settings.jancar.com.touchlearning;

import android.Manifest;
import android.annotation.SuppressLint;
import android.icu.util.ValueIterator;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kongqw.permissionslibrary.PermissionsManager;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    Button touch_one, touch_tow, touch_three, touch_four, touch_fives;
    float location_one, location_tow, location_three, location_four, location_fives;
    float location_ones, location_tows, location_threes, location_fours, location_fivess;
    float[] locations_one, locations_tow, locations_three, locations_four, locations_fives;
    RelativeLayout tabMode;
    ImageView one,tow,three,four;
    boolean isClick;
    int heigth;
    int width;
    public static final int failure = 0;
    public static final int success = 1;
    PermissionsManager mPermissionsManager;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case failure:
                    Looper.prepare();
                    anInt++;

                    Toast.makeText(MainActivity.this, "触摸学习失败", Toast.LENGTH_SHORT).show();
                    List<String> ListString = readXml("/jancar/config/pointercal.xml");
                    if (ListString.size() > 0) {
                        Log.w("ListString", ListString.toString());
                        generate(ListString.get(0), ListString.get(1), ListString.get(2), ListString.get(3), ListString.get(4),
                                ListString.get(5), ListString.get(6));

                    }
                    // MainActivity.this.finish();
                    //generate(ListString.get(0) + "", 0 + "", 0 + "", 0 + "", 1 + "", 0 + "", 1 + "");

                    Looper.loop();
                    break;
                case success:
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "触摸学习成功", Toast.LENGTH_SHORT).show();
                    MainActivity.this.finish();
                    Looper.loop();
                    break;
            }

        }
    };

    public List<String> readXml(String fileName) {
        File f = new File(fileName);
        List<String> keyWords = new ArrayList<>();
        //创建一个DocumentBuilderFactory的对象
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //创建一个DocumentBuilder的对象
        try {
            //创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();
            //通过DocumentBuilder对象的parser方法加载books.xml文件到当前项目下
            Document document = db.parse(f);
            //获取所有book节点的集合
            NodeList GISName = document.getElementsByTagName("item");
            //通过nodelist的getLength()方法可以获取bookList的长度
            //遍历每一个book节点
            for (int i = 0; i < GISName.getLength(); i++) {
                Node mNode = GISName.item(i);
                String attrs = mNode.getNodeName();
                Node s = mNode.getFirstChild();
                String attsrs = s.getNodeValue();
                keyWords.add(attsrs);
                System.out.println("=================" + attrs + "=================");
                System.out.println("=================" + attsrs + "=================");
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return keyWords;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "已进入触摸学习", Toast.LENGTH_SHORT).show();
        try {
            File a = new File("/sys/devices/soc/soc:touch@/gt9xx_props");

            Runtime.getRuntime().exec("chmod 777  " + a.getAbsolutePath());
            File c = new File("/jancar/config/pointercal.xml");
            //   String commands = "chmod 777 " + "/jancar/config/pointercal.xml";
            Runtime.getRuntime().exec("chmod 777  " + c.getAbsolutePath());

            String command = "chmod 777 " + "/sys/devices/soc/soc:touch@/gt9xx_props";
            String commands = "chmod 777 " + "/jancar/config/pointercal.xml";
            Log.i("zyl", "command = " + command);
            //Runtime runtime = Runtime.getRuntime();
            //proc.destroy();
        } catch (IOException e) {
            Log.i("zyl", "chmod fail!!!!");
            e.printStackTrace();
        }
        touch_one = (Button) findViewById(R.id.touch_one);
        touch_tow = (Button) findViewById(R.id.touch_tow);
        touch_three = (Button) findViewById(R.id.touch_three);
        touch_four = (Button) findViewById(R.id.touch_four);
        touch_fives = (Button) findViewById(R.id.touch_fives);
        tabMode = (RelativeLayout) findViewById(R.id.tabMode);
        one = (ImageView) findViewById(R.id.one);
        tow = (ImageView) findViewById(R.id.two);
        three = (ImageView) findViewById(R.id.three);
        four = (ImageView) findViewById(R.id.four);
        //   tabMode.setOnTouchListener(this);
        touch_one.setOnTouchListener(this);
        touch_tow.setOnTouchListener(this);
        touch_three.setOnTouchListener(this);
        touch_four.setOnTouchListener(this);
        touch_fives.setOnTouchListener(this);
        locations_one = new float[2];
        locations_tow = new float[2];
        locations_three = new float[2];
        locations_four = new float[2];
        locations_fives = new float[2];
        generate(1 + "", 0 + "", 0 + "", 0 + "", 1 + "", 0 + "", 1 + "");
        touch_one.setBackgroundResource(R.drawable.cbb_bg_w);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        heigth = display.getHeight();
    }

    View m;
    boolean isCorrect[] = new boolean[5];
    List<Integer> List = new ArrayList<>();
    int anInt = 0;
    int anInts = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float x2 = event.getX();
                float y2 = event.getY();
                RelativeLayout.LayoutParams relativeLayout = (RelativeLayout.LayoutParams) touch_one.getLayoutParams();
                int viewWidth = relativeLayout.width;
                int viewHeight = relativeLayout.height;
                int top = touch_one.getTop();
                if (anInt >= 5) {
                    Toast.makeText(MainActivity.this, "触摸学习失败", Toast.LENGTH_SHORT).show();
                    List<String> ListString = readXml("/jancar/config/pointercal.xml");
                    if (ListString.size() > 0) {
                        Log.w("ListString", ListString.toString());
                        generate(ListString.get(0), ListString.get(1), ListString.get(2), ListString.get(3), ListString.get(4),
                                ListString.get(5), ListString.get(6));

                    }
                    MainActivity.this.finish();
                    return false;
                }
                if (x1 > viewWidth / 2) {
                    x2 = x2 - (x1 - viewWidth / 2);
                } else {
                    x2 = x2 + (viewWidth / 2 - x1);
                }
                if (y1 > viewHeight / 2) {
                    y2 = y2 - (y1 - viewHeight / 2);
                } else {
                    y2 = y2 + (viewHeight / 2 - y1);
                }
                isCorrect[anInt] = isClick;
                switch (anInts) {
                    case 0:
                        locations_one[0] = top + viewWidth / 2;
                        locations_one[1] = top + viewHeight / 2;
                        location_one = x2;
                        location_ones = y2;
                        Log.w("ACTION_UP", "XL:" + locations_one[0] + "   YL:" + locations_one[1]);
                        Log.w("ACTION_UP", "XT:" + x2 + "   YT:" + y2);
                        anInts = 1;
                        break;
                    case 1:
                        locations_tow[0] = width - (top + viewWidth / 2);
                        locations_tow[1] = top + viewHeight / 2;
                        location_tow = x2;
                        location_tows = y2;
                        Log.w("ACTION_UP", "XL:" + locations_tow[0] + "   YL:" + locations_tow[1]);
                        Log.w("ACTION_UP", "XT:" + x2 + "   YT:" + y2);
                        anInts = 2;
                        break;
                    case 2:
                        locations_three[0] = width - (top + viewWidth / 2);
                        locations_three[1] = heigth - (top + viewHeight / 2);
                        location_three = x2;
                        location_threes = y2;
                        Log.w("ACTION_UP", "XL:" + locations_three[0] + "   YL:" + locations_three[1]);
                        Log.w("ACTION_UP", "XT:" + x2 + "   YT:" + y2);
                        anInts = 3;
                        break;
                    case 3:
                        locations_four[0] = top + viewWidth / 2;
                        locations_four[1] = heigth - (top + viewWidth / 2);
                        location_four = x2;
                        location_fours = y2;
                        Log.w("ACTION_UP", "XL:" + locations_four[0] + "   YL:" + locations_four[1]);
                        Log.w("ACTION_UP", "XT:" + x2 + "   YT:" + y2);
                        anInts = 4;
                        break;
                    case 4:
                        locations_fives[0] = width / 2;
                        locations_fives[1] = heigth / 2;
                        location_fives = x2;
                        location_fivess = y2;
                        ;
                        floats.clear();
                        Log.w("ACTION_UP", "XL:" + locations_fives[0] + "   YL:" + locations_fives[1]);
                        Log.w("ACTION_UP", "XT:" + x2 + "   YT:" + y2);
                        anInts = 1;

                        if (Math.abs(location_ones-location_tows)<12&&Math.abs(location_tow-location_three)<12&&Math.abs(location_threes-location_fours)<12){
                            float one = locations_one[0] * 65536 - locations_tow[0] * 65536;
                            float tow = locations_tow[0] * 65536 - locations_three[0] * 65536;
                            float three = locations_three[0] * 65536 - locations_four[0] * 65536;
                            float four = locations_four[0] * 65536 - locations_fives[0] * 65536;
                            float one1 = location_one - location_tow;
                            float one2 = location_ones - location_tows;
                            float tow1 = location_tow - location_three;
                            float tow2 = location_tows - location_threes;
                            float three1 = location_three - location_four;
                            float three2 = location_threes - location_fours;

                            float four1 = location_four - location_fives;
                            float four2 = location_fours - location_fivess;
                            float G = one - tow;
                            float J = tow - three;
                            float Y = three - four;
                            float one4 = one1 - tow1;
                            float one5 = one2 - tow2;
                            float tow4 = tow1 - three1;
                            float tow5 = tow2 - three2;

                            float three4 = three1 - four1;
                            float three5 = three2 - four2;

                            float O = G - J;
                            float one6 = one4 - tow4;
                            float one7 = one5 - tow5;
                            double A;
                            double B;
                            double[][] test = {{one6, one7, O}, {three4, three5, Y}};
                            ZcramerRuleMatrix gm = new ZcramerRuleMatrix(test);
                            double[] uu = new double[test.length];// 返回结果集
                            uu = gm.getResult();
                            A = uu[0];
                            B = uu[1];
                            if (O == uu[0] * one6 + one7 * uu[1]) {
                                Log.w("ACTION_UP_B", "A:" + A + "B:" + B + "C:");
                            }
                            double C1 = locations_one[0] * 65536 - A * location_one - B * location_ones;
                            float C = (float) C1;
                            Log.w("ACTION_UP_B", "A:" + A + "B:" + B + "C:" + C);

                            set(A, B, C);
                            isClick = false;
                        }else {
                         Toast.makeText(MainActivity.this, "触摸学习失败", Toast.LENGTH_SHORT).show();
                            List<String> ListString = readXml("/jancar/config/pointercal.xml");
                            if (ListString.size() > 0) {
                                Log.w("ListString", ListString.toString());
                                generate(ListString.get(0), ListString.get(1), ListString.get(2), ListString.get(3), ListString.get(4),
                                        ListString.get(5), ListString.get(6));

                            }
                            MainActivity.this.finish();
                        }
                        break;
                }
                switch (anInt) {
                    case 0:
                        touch_one.setBackgroundResource(R.drawable.cbb_bg_h);
                        touch_tow.setBackgroundResource(R.drawable.cbb_bg_w);

                        one.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        touch_tow.setBackgroundResource(R.drawable.cbb_bg_h);
                        touch_three.setBackgroundResource(R.drawable.cbb_bg_w);
                        one.setVisibility(View.GONE);
                        tow.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        tow.setVisibility(View.GONE);
                        three.setVisibility(View.VISIBLE);
                        touch_three.setBackgroundResource(R.drawable.cbb_bg_h);
                        touch_four.setBackgroundResource(R.drawable.cbb_bg_w);
                        break;
                    case 3:
                        three.setVisibility(View.GONE);
                        four.setVisibility(View.VISIBLE);
                        //four.setVisibility(View.GONE);
                        //float.setVisibility(View.VISIBLE);
                        touch_four.setBackgroundResource(R.drawable.cbb_bg_h);
                        touch_fives.setBackgroundResource(R.drawable.cbb_bg_w);
                        break;
                    case 4:

                        break;
                }

                anInt++;
                //  Log.w("ACTION_UP", "x2:" + x2 + "y2:" + y2);


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

    public void set(double A, double B, double C) {

        float one = locations_one[1] * 65536 - locations_tow[1] * 65536;
        float tow = locations_tow[1] * 65536 - locations_three[1] * 65536;
        float three = locations_three[1] * 65536 - locations_four[1] * 65536;
        float four = locations_four[1] * 65536 - locations_fives[1] * 65536;

        float one1 = location_one - location_tow;
        float one2 = location_ones - location_tows;


        float tow1 = location_tow - location_three;
        float tow2 = location_tows - location_threes;


        float three1 = location_three - location_four;
        float three2 = location_threes - location_fours;

        float four1 = location_four - location_fives;
        float four2 = location_fours - location_fivess;


        float G = one - tow;
        float J = tow - three;
        float Y = three - four;

        float one4 = one1 - tow1;
        float one5 = one2 - tow2;

        float tow4 = tow1 - three1;
        float tow5 = tow2 - three2;

        float three4 = three1 - four1;
        float three5 = three2 - four2;

        float O = G - J;
        float one6 = one4 - tow4;
        float one7 = one5 - tow5;
        double D;
        double E;
        double[][] test = {{one6, one7, O}, {three4, three5, Y}};

        ZcramerRuleMatrix gm = new ZcramerRuleMatrix(test);
        double[] uu = new double[test.length];// 返回结果集
        uu = gm.getResult();
        for (int i = 0; i < uu.length; i++) {
            System.out.println(uu[i] + ",");
            // Log.w("ACTION_UP", uu[i] + ",");
                               /* System.out.println(df.format(uu[i]));
                                floats.add(df.format(uu[i]));*/
        }
        D = uu[0];
        E = uu[1];
       /* if (O == uu[0] * one6 + one7 * uu[1]) {
            Log.w("ACTION_UP_B", "D:" + D + "E:" + E + "F:");
        }*/
        double F1 = locations_one[1] * 65536 - D * location_one - E * location_ones;
        double F = F1;
     /*   Log.w("ACTION_UP_B", "D:" + D + "F:" + F + "E:" + E);
        double xT = (D * location_one + E * location_ones + F) / 65536;
        double xT1 = (D * location_tow + E * location_tows + F) / 65536;
        double xT2 = (D * location_three + E * location_threes + F) / 65536;
        double xT3 = (D * location_four + E * location_fours + F) / 65536;
        double xT4 = (D * location_fives + E * location_fivess + F) / 65536;
        Log.w("ACTION_UP_B", "xT:" + xT + "  xT1:" + xT1 + "  xT2:" + xT2 + "  xT3:" + xT3 + "  xT4:" + xT4);*/
        Log.w("ACTION_UP_B", "D:" + D + "  E:" + E + "  F:" + F);

        BigDecimal bigDecimalA = new BigDecimal(A);
        BigDecimal bigDecimalB = new BigDecimal(B);
        BigDecimal bigDecimalC = new BigDecimal(C);
        BigDecimal bigDecimalD = new BigDecimal(D);
        BigDecimal bigDecimalE = new BigDecimal(E);
        BigDecimal bigDecimalF = new BigDecimal(F);
        generate(bigDecimalA.setScale(0, BigDecimal.ROUND_HALF_UP) + "", bigDecimalB.setScale(0, BigDecimal.ROUND_HALF_UP) + "", bigDecimalC.setScale(0, BigDecimal.ROUND_HALF_UP) + "",
                bigDecimalD.setScale(0, BigDecimal.ROUND_HALF_UP) + "", bigDecimalE.setScale(0, BigDecimal.ROUND_HALF_UP) + "", bigDecimalF.setScale(0, BigDecimal.ROUND_HALF_UP) + "", 65536 + "");
    }

    List<String> floats = new ArrayList<>();
    float x1;
    float y1;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Log.w("ACTION_UP", "a:");
        m = v;
        isClick = true;
        x1 = event.getX();
        y1 = event.getY();
        Log.w("ACTION_UP", "a:x1=" + x1 + "y1=" + y1);
        return false;
    }

    //默认是没有换行的
    public void initSettings(final String a, final String b, final String c, final String d, final String e, final String f, final String div) {
        final File file = new File("/jancar/config/");
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception r) {
            Log.i("error:", r + "");
        }
        final File files = new File("/jancar/config/pointercal.xml");
        if (!files.exists()) {
            try {
                file.createNewFile();
            } catch (IOException r) {
// TODO Auto-generated catch block    
                r.printStackTrace();
            }
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(files);
                    XmlSerializer serializer = Xml.newSerializer();
                    serializer.setOutput(fos, "UTF-8");
                    serializer.startDocument("UTF-8", true);
                    serializer.startTag(null, "config");

                    // server
                    serializer.startTag(null, "item");
                    serializer.attribute(null, "id", "A");
                    serializer.text(a);
                    serializer.endTag(null, "item");
                    // hid
                    serializer.startTag(null, "item");
                    serializer.attribute(null, "id", "B");
                    // serializer.attribute(null, "value", b);
                    serializer.text(b);
                    serializer.endTag(null, "item");
                    // room
                    serializer.startTag(null, "item");
                    serializer.attribute(null, "id", "C");
                    serializer.text(c);
                    serializer.endTag(null, "item");
                    // room
                    serializer.startTag(null, "item");
                    serializer.attribute(null, "id", "D");
                    serializer.text(d);
                    serializer.endTag(null, "item");

                    // room
                    serializer.startTag(null, "item");
                    serializer.attribute(null, "id", "E");
                    serializer.text(e);
                    serializer.endTag(null, "item");

                    // room
                    serializer.startTag(null, "item");
                    serializer.attribute(null, "id", "F");
                    serializer.text(f);
                    serializer.endTag(null, "item");

                    // room
                    serializer.startTag(null, "item");
                    serializer.attribute(null, "id", "Div");
                    serializer.text(div);
                    serializer.endTag(null, "item");


                    serializer.endTag(null, "config");
                    serializer.endDocument();
                    serializer.flush();
                } catch (FileNotFoundException e) {
                    Message message = new Message();
                    message.what = failure;
                    handler.handleMessage(message);
                } catch (IllegalArgumentException e) {
                    Message message = new Message();
                    message.what = failure;
                    handler.handleMessage(message);
                } catch (IllegalStateException e) {
                    Message message = new Message();
                    message.what = failure;
                    handler.handleMessage(message);
                } catch (IOException e) {
                    Message message = new Message();
                    message.what = failure;
                    handler.handleMessage(message);
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Message message = new Message();
                message.what = success;
                handler.handleMessage(message);

            }
        }).start();

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
                    String content = Integer.parseInt(a) +
                            " " + Integer.parseInt(b) +
                            " " + Integer.parseInt(c)
                            + " " + Integer.parseInt(d)
                            + " " + Integer.parseInt(e)
                            + " " + Integer.parseInt(f)
                            + " " + Integer.parseInt(div);

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
                        if (anInt == 5) {
                            initSettings(a, b, c,
                                    d, e, f, 65536 + "");

                        }

                        System.out.println("Done");

                    } catch (IOException e) {
                        if (anInt == 5) {
                            Message message = new Message();
                            message.what = failure;
                            handler.handleMessage(message);
                        }
                        //  e.printStackTrace();
                    }
                }

            }).start();

        }
    }
}
