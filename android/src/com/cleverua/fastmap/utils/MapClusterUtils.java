package com.cleverua.fastmap.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Alex Kulakovsky
 * Date: 4/9/13
 * Time: 12:23 PM
 * Email: akulakovsky@cleverua.com
 */
public class MapClusterUtils {

    private static final double TWO_IN_POWER_30 = Math.pow(2, 30);

    public static String quadTree (double lat, double lng){
        double  xx = Math.sin(lat * Math.PI / 180);
        double  yy = 0.5 - Math.log((1 + xx) / (1 - xx)) / (4 * Math.PI);

        String latStr = StringUtils.getBinaryString((int) (yy * TWO_IN_POWER_30), 30);
        String lngStr = StringUtils.getBinaryString((int) ((lng + 180) / 360 * TWO_IN_POWER_30), 30);

        StringBuilder quadKey = new StringBuilder();

        Log.d(TAG, "Input lat:" + lat + ", length = " + latStr.length());
        Log.d(TAG, "Input lng:" + lng + ", length = " + lngStr.length());

        for (int i = latStr.length()-1; i >= 0; i--){
            int digit = 0;

            digit = Character.toString(latStr.charAt(i)).equals("1") ? digit + 2 : digit;
            digit = Character.toString(lngStr.charAt(i)).equals("1") ? digit + 1 : digit;

            quadKey.insert(0, String.valueOf(digit));

        }

        return quadKey.toString();
    }

    public static int getQtreeLength(double lngDiff){
        double[] lngSteps = getLngSteps();

        for (int i = 0; i < 30; i++){
            if (lngDiff >= lngSteps[i]){
                return i;
            }
        }
        return 30;
    }

    private static double[] getLngSteps(){

        double earth = 360;
        List<Double> steps = new ArrayList<Double>();
        for (int i = 0; i < 30; i++){
            steps.add(earth);
            earth = earth / 2;
        }

        return DoubleUtils.toPrimitive(steps);
    }

    private static final String TAG = MapClusterUtils.class.getSimpleName();
}
