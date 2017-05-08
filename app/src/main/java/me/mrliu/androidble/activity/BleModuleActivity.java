package me.mrliu.androidble.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import me.mrliu.androidble.BleService;
import me.mrliu.androidble.MyApplication;
import me.mrliu.androidble.R;
import me.mrliu.androidble.adapter.CharacteristicChangedAdapter;
import me.mrliu.androidble.utils.Utils;

/**
 * 仅针对公司开发所用的 ble 模块进行蓝牙通信测试
 */
public class BleModuleActivity extends AppCompatActivity implements View.OnClickListener, BleService.OnCharacteristicChangedListener {
    private static final String TAG = BleModuleActivity.class.getSimpleName();
    private ListView listView;
    private EditText editText;
    private Button button;

    private String hexString;
    private CharacteristicChangedAdapter adapter;

    private List<BluetoothGattCharacteristic> characteristics;
    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_module);
        initCharacteristics();
        setViews();

        adapter = new CharacteristicChangedAdapter(this);
        listView.setAdapter(adapter);
    }

    private void initCharacteristics() {
        MyApplication.mService.setOnCharacteristicChangedListener(this);
        characteristics = MyApplication.bluetoothGattCharacteristics;
        for (BluetoothGattCharacteristic characteristic : characteristics) {
            if (characteristic.getProperties() == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
                notifyCharacteristic = characteristic;
                Log.e(TAG, "notify:" + characteristic.getProperties() + " ; " + BluetoothGattCharacteristic.PROPERTY_NOTIFY);
                MyApplication.mService.setCharacteristicNotification(notifyCharacteristic, true);
                continue;
            }
            if (characteristic.getProperties() == BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) {
                writeCharacteristic = characteristic;
                Log.e(TAG, "write:" + characteristic.getProperties() + " ; " + BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE);
                continue;
            }
        }
    }

    private void setViews() {
        listView = (ListView) findViewById(R.id.list_view);
        editText = (EditText) findViewById(R.id.edit_text);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                hexString = editText.getText().toString();
                hexString = hexString.replace(" ", "");
                if (!TextUtils.isEmpty(hexString)) {
                    MyApplication.mService.writeCharacteristic(writeCharacteristic, Utils.hexStringToByteArray(hexString));
                } else {
                    Toast.makeText(this, "请输入文本内容" ,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onCharacteristicChanged(final BluetoothGattCharacteristic characteristic) {
        Log.e(TAG, "onCharacteristicChanged:"+Utils.ByteArraytoHex(characteristic.getValue()));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addItem(characteristic);
                adapter.notifyDataSetChanged();
                listView.setSelection(adapter.getCount()-1);
            }
        });
    }
}
