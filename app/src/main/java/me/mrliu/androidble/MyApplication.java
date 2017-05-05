package me.mrliu.androidble;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * Created by LiuKang on 2017/5/5.
 */

public class MyApplication extends Application {
    public static BluetoothDevice device;
    public static BluetoothGattService bluetoothGattService;
    public static List<BluetoothGattService> bluetoothGattServices;
    public static BluetoothGattCharacteristic bluetoothGattCharacteristic;
    public static List<BluetoothGattCharacteristic> bluetoothGattCharacteristics;
}
