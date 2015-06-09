package com.pinodex.loadcentral;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

public final class TelephonyInfo {

    private static TelephonyInfo telephonyInfo;

    private String imsiSIM1;
    private String imsiSIM2;

    public boolean isDualSIM() {
        return imsiSIM2 != null;
    }

    private TelephonyInfo() { }

    public static TelephonyInfo getInstance(Context context) {
        if (telephonyInfo != null) {
            return telephonyInfo;
        }

        telephonyInfo = new TelephonyInfo();

        TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

        telephonyInfo.imsiSIM1 = telephonyManager.getDeviceId();
        telephonyInfo.imsiSIM2 = null;

        String[] imeiGuesses = {
                "getDeviceId",
                "getDeviceIdDs",
                "getDeviceIdGemini",
                "getSimSerialNumberGemini"
        };

        for (String guess: imeiGuesses) {
            Boolean status = true;

            try {
                telephonyInfo.imsiSIM1 = getDeviceIdBySlot(context, guess, 0);
                telephonyInfo.imsiSIM2 = getDeviceIdBySlot(context, guess, 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();
                status = false;
            }

            if (status) {
                break;
            }
        }

        return telephonyInfo;
    }

    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
        String imsi = null;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if(ob_phone != null){
                imsi = ob_phone.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return imsi;
    }

    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }
}