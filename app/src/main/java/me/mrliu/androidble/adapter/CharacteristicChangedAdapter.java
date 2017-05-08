package me.mrliu.androidble.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.mrliu.androidble.R;
import me.mrliu.androidble.utils.Utils;

/**
 * Created by LiuKang on 2017/5/8.
 */

public class CharacteristicChangedAdapter extends BaseAdapter {
    private List<BluetoothGattCharacteristic> characteristics;
    private LayoutInflater inflater;

    public CharacteristicChangedAdapter(Context context) {
        characteristics = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public void addItem(BluetoothGattCharacteristic characteristic) {
        characteristics.add(characteristic);
    }

    public void clearItems() {
        if (characteristics.size()>0) {
            characteristics.clear();
        }
    }

    @Override
    public int getCount() {
        return characteristics.size();
    }

    @Override
    public BluetoothGattCharacteristic getItem(int position) {
        return characteristics.get(position);
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
            convertView = inflater.inflate(R.layout.layout_item_message_left, parent, false);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_msg_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(Utils.ByteArraytoHex(getItem(position).getValue()));
        return convertView;
    }

    private class ViewHolder{
        TextView tv;
    }
}
