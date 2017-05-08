package me.mrliu.androidble.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.mrliu.androidble.utils.GattAttributes;
import me.mrliu.androidble.R;
import me.mrliu.androidble.utils.Utils;

/**
 * Created by LiuKang on 2017/5/5.
 */

public class CharacteristicsAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<BluetoothGattCharacteristic> bluetoothGattCharacteristics;
    private Context context;

    public CharacteristicsAdapter(Context context, List<BluetoothGattCharacteristic> bluetoothGattCharacteristics) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.bluetoothGattCharacteristics = bluetoothGattCharacteristics;
    }

    @Override
    public int getCount() {
        return bluetoothGattCharacteristics.size();
    }

    @Override
    public BluetoothGattCharacteristic getItem(int position) {
        return bluetoothGattCharacteristics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_ble, parent, false);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvUuid = (TextView) convertView.findViewById(R.id.tv_address);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(Utils.getProperties(context, getItem(position)));
        holder.tvUuid.setText(GattAttributes.lookup(getItem(position).getUuid().toString(), getItem(position).getUuid().toString()));
        return convertView;
    }

    private class ViewHolder{
        TextView tvName;
        TextView tvUuid;
    }
}
