package me.mrliu.androidble.utils;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.util.Log;

import me.mrliu.androidble.R;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static String getProperties(Context context, BluetoothGattCharacteristic item){
        String proprties;
        String read = null, write = null, notify = null;

        /**
         * Checking the various GattCharacteristics and listing in the ListView
         */
        if (getGattCharacteristicsProperties(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_READ)) {
            read = context.getString(R.string.gatt_services_read);
        }
        if (getGattCharacteristicsProperties(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_WRITE)
                | getGattCharacteristicsProperties(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
            write = context.getString(R.string.gatt_services_write);
        }
        if (getGattCharacteristicsProperties(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            notify = context.getString(R.string.gatt_services_notify);
        }
        if (getGattCharacteristicsProperties(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            notify = context.getString(R.string.gatt_services_indicate);
        }
        // Handling multiple properties listing in the ListView
        if (read != null) {
            proprties = read;
            if (write != null) {
                proprties = proprties + " & " + write;
            }
            if (notify != null) {
                proprties = proprties + " & " + notify;
            }
        } else {
            if (write != null) {
                proprties = write;

                if (notify != null) {
                    proprties = proprties + " & " + notify;
                }
            } else {
                proprties = notify;
            }
        }

        return proprties;
    }

    // Return the properties of mGattCharacteristics
    public static boolean getGattCharacteristicsProperties(int characteristics,int characteristicsSearch) {
        Log.e(TAG, (characteristics & characteristicsSearch)+";"+characteristicsSearch);
        if ((characteristics & characteristicsSearch) == characteristicsSearch) {
            return true;
        }
        return false;
    }



    public static byte[] hexStringToByteArray(String s) {
        if (s.length() % 2 != 0) {
            StringBuilder stringBuilder = new StringBuilder(s);
            stringBuilder.insert(s.length()-1,"0");
            s = stringBuilder.toString();
        }


        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String ByteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String bs = String.format("%02X ", b);
            /*if (bs.trim().equals("0A")){
                sb.append("0D 0A");
            }else
                sb.append(bs);*/
            sb.append(bs);
        }
        return sb.toString();
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}