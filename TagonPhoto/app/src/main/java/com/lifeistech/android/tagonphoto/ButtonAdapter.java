package com.lifeistech.android.tagonphoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

/**
 * Created by hideki on 2017/04/26.
 */

public class ButtonAdapter extends ArrayAdapter<Button> {
    List<Button> mButtons;

    public ButtonAdapter(Context context, int layoutResourceId, List<Button> objects) {
        super(context, layoutResourceId, objects);
        mButtons = objects;
    }

    @Override
    public int getCount(){
        return  mButtons.size();
    }

    @Override
    public Button getItem(int position) {
        return mButtons.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_memo_button, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Button item = getItem(position);

        if (item != null){
            viewHolder.newImageButton.setText(item.content);
        }

        return convertView;
    }

    private class ViewHolder {
        Button newImageButton;
        Button whitNoteButton;

        public ViewHolder(View v) {
            newImageButton = (Button) v.findViewById(R.id.newImage);
            whitNoteButton = (Button) v.findViewById(R.id.whiteNote);
        }

    }
}
