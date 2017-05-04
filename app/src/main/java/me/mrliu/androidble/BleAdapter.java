package me.mrliu.androidble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuKang on 2017/5/4.
 */

public class BleAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;

    private List<BluetoothDevice> bluetoothDevices;

    public BleAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        bluetoothDevices = new ArrayList<>();
    }

    public void addDevice(BluetoothDevice bluetoothDevice) {
        bluetoothDevices.add(bluetoothDevice);
        notifyDataSetChanged();
    }

    public boolean contains(BluetoothDevice bluetoothDevice) {
        return bluetoothDevices.contains(bluetoothDevice);
    }

    public void clear() {
        if (bluetoothDevices.size()>0) {
            bluetoothDevices.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return bluetoothDevices.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return bluetoothDevices.get(position);
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
        holder.tvName.setText(getItem(position).getName());
        holder.tvAddress.setText(getItem(position).getAddress());
        return convertView;
    }

    private class ViewHolder{
        TextView tvName;
        TextView tvAddress;
    }
}
