package kotov.invisible.taitihotel.MyUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PhoneInfo {

    public static String getUniqueId() {

        String ret;

        String pseudoId = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.HARDWARE.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10;

        String values =
                Build.BOARD + Build.BRAND +
                        Build.HARDWARE + Build.DEVICE +
                        Build.DISPLAY + Build.HOST +
                        Build.ID + Build.MANUFACTURER +
                        Build.MODEL + Build.PRODUCT +
                        Build.TAGS + Build.TYPE +
                        Build.USER;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(values.getBytes());
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashText = bigInt.toString(16);
            while (hashText.length() < 32)
                hashText = "0" + hashText;

            ret = hashText;
        } catch (NoSuchAlgorithmException e) {
            ret = pseudoId;
            e.printStackTrace();
        }

        return ret;
    }

    public static String getPhoneName() {
        return Build.MODEL;
    }

    public static String getPhoneNum(Activity a) {

        String phoneNum = "null";

        // M is for Marshmallow!
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionCheck = a.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck == PERMISSION_GRANTED)
                phoneNum = ((TelephonyManager) a.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        } else {
            phoneNum = ((TelephonyManager) a.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        }

        return phoneNum;
    }
}
