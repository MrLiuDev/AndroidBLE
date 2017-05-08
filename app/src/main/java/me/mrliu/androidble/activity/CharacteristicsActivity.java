package me.mrliu.androidble.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import me.mrliu.androidble.utils.GattAttributes;
import me.mrliu.androidble.MyApplication;
import me.mrliu.androidble.R;
import me.mrliu.androidble.adapter.CharacteristicsAdapter;

public class CharacteristicsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private TextView tvName, tvUuid, tvCharacteristics;
    private ListView listView;
    private List<BluetoothGattCharacteristic> characteristics;
    private BluetoothGattService gattService;
    private CharacteristicsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characteristics);
        gattService = MyApplication.bluetoothGattService;
        characteristics = MyApplication.bluetoothGattCharacteristics;
        setViews();
        adapter = new CharacteristicsAdapter(this, characteristics);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void setViews() {
        tvName = (TextView) findViewById(R.id.tv_name);
        tvUuid = (TextView) findViewById(R.id.tv_uuid);
        tvCharacteristics = (TextView) findViewById(R.id.tv_characteristics);
        listView = (ListView) findViewById(R.id.lv_characteristics);
        tvName.setText(GattAttributes.lookup(gattService.getUuid().toString(), "Unknown Service"));
        tvUuid.setText(gattService.getUuid().toString());
        tvCharacteristics.setText(characteristics.size()+"");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyApplication.bluetoothGattCharacteristic = adapter.getItem(position);
        //MyApplication.bluetoothGattCharacteristic.getProperties();
        if (MyApplication.bluetoothGattService.getUuid().toString().equals(GattAttributes.USR_SERVICE)) {
            startActivity(new Intent(this, BleModuleActivity.class));
        } else {
            startActivity(new Intent(this, GattDetailActivity.class));
        }
    }
}
