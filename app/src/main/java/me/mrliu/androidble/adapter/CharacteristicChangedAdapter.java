package me.mrliu.androidble.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.mrliu.androidble.R;

/**
 * Created by LiuKang on 2017/5/8.
 */

public class CharacteristicChangedAdapter extends BaseAdapter {
    private List<String> characteristicValues;
    private LayoutInflater inflater;

    public CharacteristicChangedAdapter(Context context) {
        characteristicValues = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public void addItem(String characteristicValue) {
        characteristicValues.add(characteristicValue);
        notifyDataSetChanged();
    }

    public void clearItems() {
        if (characteristicValues.size()>0) {
            characteristicValues.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return characteristicValues.size();
    }

    @Override
    public String getItem(int position) {
        return characteristicValues.get(position);
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
        holder.tv.setText(getItem(position));
        return convertView;
    }

    private class ViewHolder{
        TextView tv;
    }
}
