package me.mrliu.androidble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

public class Utils {
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

        if ((characteristics & characteristicsSearch) == characteristicsSearch) {
            return true;
        }
        return false;
    }
}