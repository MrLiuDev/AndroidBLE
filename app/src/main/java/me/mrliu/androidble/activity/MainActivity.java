package me.mrliu.androidble.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import me.mrliu.androidble.BleService;
import me.mrliu.androidble.MyApplication;
import me.mrliu.androidble.R;
import me.mrliu.androidble.utils.Type;
import me.mrliu.androidble.adapter.BleAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BleService.OnBleScanCallbackListener, AdapterView.OnItemClickListener, BleService.OnConnectionStateChangeListener, BleService.OnServicesDiscoveredListener {
    private Button btnOpenOrClose;
    private ListView listView;

    private static final String TAG = MainActivity.class.getSimpleName();

    private BleService mService;
    private ServiceConnection mServiceConnection;
    private Intent serviceIntent;
    private BleReceiver mBleReceiver;
    private BleAdapter mBleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        setListView();

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((BleService.BleBinder)service).getService();
                if (mService != null) {
                    mService.setOnBleScanCallbackListener(MainActivity.this);
                    mService.setOnConnectionStateChangeListener(MainActivity.this);
                    mService.setOnServicesDiscoveredListener(MainActivity.this);
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

    private void setListView() {
        mBleAdapter = new BleAdapter(this);
        listView.setAdapter(mBleAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        unregisterReceiver(mBleReceiver);
    }

    private void setViews() {
        listView = (ListView) findViewById(R.id.list_view);
        btnOpenOrClose = (Button) findViewById(R.id.btn_open_bt);
        btnOpenOrClose.setOnClickListener(this);
        findViewById(R.id.btn_start_scan).setOnClickListener(this);
        findViewById(R.id.btn_connection_state).setOnClickListener(this);
        findViewById(R.id.btn_disconnect).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_bt:
                mService.openOrCloseBluetooth(this);
                break;

            case R.id.btn_start_scan:
                if (mService.isScanning()) {
                    mService.stopScanBleDevices();
                } else {
                    mBleAdapter.clear();
                    mService.scanBleDevices(this);
                }

                break;

            case R.id.btn_connection_state:
                switch (mService.getConnectionState()) {
                    case BluetoothProfile.STATE_CONNECTED:
                        Toast.makeText(this, "已连接", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothProfile.STATE_CONNECTING:
                        Toast.makeText(this, "连接中", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        Toast.makeText(this, "已断开", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTING:
                        Toast.makeText(this, "断开中", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;

            case R.id.btn_disconnect:
                mService.disconnect();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BleService.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //Toast.makeText(this, "蓝牙打开成功", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "蓝牙未打开", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onScanResult(final ScanResult result) {
        Log.e(TAG, "onScanResult");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mBleAdapter.contains(result.getDevice())) {
                    mBleAdapter.addDevice(result.getDevice());
                }
            }
        });
    }

    @Override
    public void onLeScan(final BluetoothDevice device, int rssi) {
        Log.e(TAG, "onLeScan");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mBleAdapter.contains(device)) {
                    mBleAdapter.addDevice(device);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        connectBleDevice(mBleAdapter.getItem(position));
        MyApplication.device = mBleAdapter.getItem(position);
    }

    private void connectBleDevice(final BluetoothDevice device) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mService.connectBleDevice(device, MainActivity.this);
            }
        }, 500);
        if (mService.isScanning()) {
            mService.stopScanBleDevices();
        }
        if (mService.getConnectionState() == BluetoothProfile.STATE_CONNECTED) {
            mService.disconnect();
        }

    }

    @Override
    public void onConnectionStateChange(final int mConnectionState) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, mConnectionState+"", Toast.LENGTH_SHORT).show();
                if (mConnectionState == BluetoothProfile.STATE_CONNECTED) {
                    mService.discoverServices();
                }
            }
        });
    }

    @Override
    public void onServicesDiscovered(final List<BluetoothGattService> bluetoothGattServices) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyApplication.bluetoothGattServices = bluetoothGattServices;
                Intent intent = new Intent(MainActivity.this, ServicesActivity.class);
                startActivity(intent);
            }
        });
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

}
