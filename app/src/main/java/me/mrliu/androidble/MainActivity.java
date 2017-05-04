package me.mrliu.androidble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BluetoothLeScanner bleScanner;
    private List<ScanFilter> scanFilters;
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<>();

    private Button btnOpenOrClose;
    private TextView tvDevice;
    private ListView listView;

    private static final String TAG = MainActivity.class.getSimpleName();

    private BleService.BleBinder mService;
    private ServiceConnection mServiceConnection;
    private Intent serviceIntent;
    private BleReceiver mBleReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        //getBluetoothAdapter();

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = (BleService.BleBinder) service;
                if (mService != null) {
                    Type mType = mService.initBluetooth();
                    switch (mType) {
                        case NONE:
                            Toast.makeText(MainActivity.this, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
                            btnOpenOrClose.setText("该设备不支持蓝牙");
                            btnOpenOrClose.setEnabled(false);
                            break;
                        case ON:
                            Toast.makeText(MainActivity.this, "蓝牙处于打开状态", Toast.LENGTH_SHORT).show();
                            btnOpenOrClose.setText("关闭蓝牙");
                            break;
                        case OFF:
                            Toast.makeText(MainActivity.this, "蓝牙处于关闭状态", Toast.LENGTH_SHORT).show();
                            btnOpenOrClose.setText("打开蓝牙");
                            break;
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        serviceIntent = new Intent(this, BleService.class);
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        //startService(serviceIntent);
        mBleReceiver = new BleReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBleReceiver, mFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        unregisterReceiver(mBleReceiver);
    }

    private void setViews() {
        tvDevice = (TextView) findViewById(R.id.tv_device);
        btnOpenOrClose = (Button) findViewById(R.id.btn_open_bt);
        btnOpenOrClose.setOnClickListener(this);
        findViewById(R.id.btn_start_scan).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_bt:
                mService.openOrCloseBluetooth(this);
                break;

            case R.id.btn_start_scan:
                mService.scanBleDevices(this);
                /*if (mBluetoothAdapter != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
                        scanFilters = new ArrayList<>();
                        //scanFilters.add(new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString("0000fee0-0000-1000-8000-00805f9b34fb")).build());
                        ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
                        bleScanner.startScan(scanFilters, scanSettings, new MyScanCallback());
                    }
                }*/
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BleService.BleBinder.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //Toast.makeText(this, "蓝牙打开成功", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "蓝牙未打开", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class BleReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(MainActivity.this, "蓝牙已打开", Toast.LENGTH_SHORT).show();
                            btnOpenOrClose.setText("关闭蓝牙");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(MainActivity.this, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                            btnOpenOrClose.setText("打开蓝牙");
                            break;
                    }
                    break;
            }
        }
    }



    @SuppressLint("NewApi")
    private class MyScanCallback extends ScanCallback{
        public MyScanCallback() {
            tvDevice.setText("");
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result != null) {
                if (!bluetoothDevices.contains(result.getDevice())) {
                    bluetoothDevices.add(result.getDevice());
                    for (BluetoothDevice device : bluetoothDevices) {
                        Log.e(TAG, device.getName()+", "+device.getAddress());
                    }
                }
            }
        }
    }
}
