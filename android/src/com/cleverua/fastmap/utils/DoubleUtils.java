package com.cleverua.fastmap.utils;

import java.util.List;

/**
 * Created by: Alex Kulakovsky
 * Date: 4/9/13
 * Time: 12:26 PM
 * Email: akulakovsky@cleverua.com
 */
public class DoubleUtils {

    public static double[] toPrimitive(List<Double> list){
        double[] primitive = new double[list.size()];

        for (int i = 0; i < list.size(); i++){
            primitive[i] = list.get(i);
        }

        return primitive;
    }
}

