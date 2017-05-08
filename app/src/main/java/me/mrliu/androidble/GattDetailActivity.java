package me.mrliu.androidble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class GattDetailActivity extends AppCompatActivity {
    private static final String TAG = GattDetailActivity.class.getSimpleName();
    private LinearLayout llWrite, llOther;
    private ImageButton ibOption;

    private BluetoothGattCharacteristic characteristic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gatt_detail);

        characteristic = MyApplication.bluetoothGattCharacteristic;
        Log.e(TAG, Utils.getProperties(this, characteristic));

        setViews();

    }

    private void setViews() {
        llWrite = (LinearLayout) findViewById(R.id.ll_write);
        llOther = (LinearLayout) findViewById(R.id.ll_other);
        ibOption = (ImageButton) findViewById(R.id.ib_option);
        ibOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llWrite.getVisibility() == View.VISIBLE) {
                    llWrite.setVisibility(View.GONE);
                } else {
                    llWrite.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
