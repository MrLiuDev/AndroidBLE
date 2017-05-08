package me.mrliu.androidble.adapter;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.mrliu.androidble.utils.GattAttributes;
import me.mrliu.androidble.R;

/**
 * Created by LiuKang on 2017/5/5.
 */

public class ServicesAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<BluetoothGattService> bluetoothGattServices;

    public ServicesAdapter(Context context, List<BluetoothGattService> bluetoothGattServices) {
        mLayoutInflater = LayoutInflater.from(context);
        this.bluetoothGattServices = bluetoothGattServices;
    }

    @Override
    public int getCount() {
        return bluetoothGattServices.size();
    }

    @Override
    public BluetoothGattService getItem(int position) {
        return bluetoothGattServices.get(position);
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
            holder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(GattAttributes.lookup(getItem(position).getUuid().toString(), "Unknown Service"));
        holder.tvAddress.setText(getItem(position).getUuid().toString());
        return convertView;
    }

    private class ViewHolder{
        TextView tvName;
        TextView tvAddress;
    }
}
