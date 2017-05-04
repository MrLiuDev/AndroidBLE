package me.mrliu.androidble;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuKang on 2017/5/4.
 */

public class BleService extends Service {
    private BleBinder mBinder = new BleBinder();
    private Handler mHandler;
    private static final int SCAN_PERIOD = 15000;
    private boolean mScanning;
    private MyScanCallback mScanCallback;
    private MyLeScanCallback mLeScanCallback;
    private OnBleScanCallback onBleScanCallback;
    private BluetoothGattCallback mBluetoothGattCallback;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private List<ScanFilter> scanFilters;

    private static final String TAG = BleService.class.getSimpleName();
    public static final int REQUEST_ENABLE_BT = 001;


    public void setOnBleScanCallback(OnBleScanCallback onBleScanCallback) {
        this.onBleScanCallback = onBleScanCallback;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScanCallback = new MyScanCallback();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            mLeScanCallback = new MyLeScanCallback();
        }
        mBluetoothGattCallback = new MyBluetoothGattCallback();

    }

    public Type initBluetooth() {
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        if (mBluetoothAdapter == null) {
            return Type.NONE;
        } else if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            return Type.OFF;
        } else if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            return Type.ON;
        }
        return Type.NONE;
    }

    public void openOrCloseBluetooth(Activity mActivity) {
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                mBluetoothAdapter.disable();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scanBleDevices(final Context context) {
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(context, "蓝牙未打开", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mScanCallback != null) {
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                scanFilters = new ArrayList<>();
                //scanFilters.add(new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString("0000fee0-0000-1000-8000-00805f9b34fb")).build());
                ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
                mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);
            } else if (mLeScanCallback != null){
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
            mHandler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    if (mBluetoothLeScanner != null) {
                        mBluetoothLeScanner.stopScan(mScanCallback);
                    } else {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                    Toast.makeText(context, "扫描结束", Toast.LENGTH_SHORT).show();
                }
            }, SCAN_PERIOD);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public class MyScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            onBleScanCallback.onScanResult(result);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public class MyLeScanCallback implements BluetoothAdapter.LeScanCallback {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            onBleScanCallback.onLeScan(device, rssi);
        }
    }

    public void connectBleDevice(BluetoothDevice device, Context context) {
        device.connectGatt(context, false, mBluetoothGattCallback);
    }

    public class MyBluetoothGattCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            //status = BluetoothGatt.STATE_CONNECTED;

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }
    }

    public interface OnBleScanCallback {
        void onScanResult(ScanResult result);
        void onLeScan(BluetoothDevice device, int rssi);
    }

    public class BleBinder extends Binder {

        public BleService getService() {
            return BleService.this;
        }
    }
}
