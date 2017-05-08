package me.mrliu.androidble.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import me.mrliu.androidble.MyApplication;
import me.mrliu.androidble.R;
import me.mrliu.androidble.adapter.ServicesAdapter;

public class ServicesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private TextView tvName, tvAddress, tvServices;
    private ListView lvServices;
    private BluetoothDevice device;
    private List<BluetoothGattService> gattServices;
    private ServicesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        device = MyApplication.device;
        gattServices = MyApplication.bluetoothGattServices;
        setViews();
        adapter = new ServicesAdapter(this, gattServices);
        lvServices.setAdapter(adapter);
        lvServices.setOnItemClickListener(this);
    }

    private void setViews() {
        tvName = (TextView) findViewById(R.id.tv_name);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvServices = (TextView) findViewById(R.id.tv_services);
        lvServices = (ListView) findViewById(R.id.lv_services);
        tvName.setText("Name: "+device.getName());
        tvAddress.setText("Mac: "+device.getAddress());
        tvServices.setText("Services: "+gattServices.size());
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothGattService gattService = adapter.getItem(position);
        MyApplication.bluetoothGattService = gattService;
        MyApplication.bluetoothGattCharacteristics = gattService.getCharacteristics();
        startActivity(new Intent(this, CharacteristicsActivity.class));
    }
}
