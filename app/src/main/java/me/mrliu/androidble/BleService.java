package me.mrliu.androidble;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
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
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<>();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    private static final String TAG = BleService.class.getSimpleName();

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

    }

    public class BleBinder extends Binder {
        public static final int REQUEST_ENABLE_BT = 001;

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
        public void scanBleDevices(Context context) {
            if (mBluetoothAdapter != null) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(context, "蓝牙未打开", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (bluetoothDevices.size()>0) {
                    bluetoothDevices.clear();
                }
                if (mScanCallback != null) {
                    mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                    mBluetoothLeScanner.startScan(mScanCallback);
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
                    }
                }, SCAN_PERIOD);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public class MyScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (!bluetoothDevices.contains(result.getDevice())) {
                bluetoothDevices.add(result.getDevice());
                Log.e(TAG, bluetoothDevices.toString());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public class MyLeScanCallback implements BluetoothAdapter.LeScanCallback {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (!bluetoothDevices.contains(device)) {
                bluetoothDevices.add(device);
                Log.e(TAG, bluetoothDevices.toString());
            }
        }
    }

    interface BleScanCallback {
        List onScanResult();
    }
}
