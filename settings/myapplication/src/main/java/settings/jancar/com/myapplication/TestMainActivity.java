package settings.jancar.com.myapplication;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.lang.reflect.Method;
public class TestMainActivity extends Activity {
    private static String TAG = TestMainActivity.class.getSimpleName();
    private static String CLASS_NAME = "com.jancar.service.globaldata.GlobaldataService";
    private static String PACKAGE_NAME = "com.jancar.service";
    private static String GET_SERIALNUMBER = "getSerialNumber";
    private static String GET_CUSTOMERNAME = "getCustomerName";
    private static String GET_MANUFACTURER = "getManufacturer";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getThreeMethod(this,CLASS_NAME);
    }


    public static String getCustomerName(){
        return "JAC";
    }


    public static void getThreeMethod(Context context, String clsName) {
        try {
            Context c = context.createPackageContext(PACKAGE_NAME, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            Class clazz = c.getClassLoader().loadClass(clsName);
            Method getSerialNumber = clazz.getMethod(GET_SERIALNUMBER);
            String serial = (String) getSerialNumber.invoke(clazz);//机器唯一标识码
            android.util.Log.e(TAG, "serial: " + serial);

            Method getCustomerName = clazz.getMethod(GET_CUSTOMERNAME);
            String customerName = (String) getCustomerName.invoke(clazz);//一级厂商
            android.util.Log.e(TAG, "customerName: " + customerName);

            Method getManufacturer = clazz.getMethod(GET_MANUFACTURER);
            String manufacturer = (String) getManufacturer.invoke(clazz);//二级厂商
            android.util.Log.e(TAG, "manufacturer: " + manufacturer);
        } catch (Throwable e) {
            android.util.Log.e(TAG, "e: " + e);
            e.printStackTrace();
        }
    }
}