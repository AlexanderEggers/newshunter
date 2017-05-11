package com.acando.newshunter.internal;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.acando.newshunter.Global;
import com.acando.newshunter.content.Article;
import com.acando.newshunter.R;
import com.acando.newshunter.databinding.ListItemBinding;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Article> {

    public ListAdapter(Context context, List<Article> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article item = getItem(position);

        if(convertView == null) {
            ListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.list_item, null, false);
            convertView = binding.getRoot();
        }

        ListItemBinding binding = DataBindingUtil.bind(convertView);
        binding.setEntry(item);

        Bitmap bitmap = ((Global)((ListActivity) getContext()).getApplication()).getBitmapFromMemCache(item.imageURL);
        if (bitmap == null && item.imageURL != null && !item.imageURL.equals("")) {
            ((ImageView) convertView.findViewById(R.id.image)).setImageBitmap(null);
            ((ListActivity) getContext()).mImageTask.addItem(item, (ImageView) convertView.findViewById(R.id.image));
        } else {
            ((ImageView) convertView.findViewById(R.id.image)).setImageBitmap(bitmap);
        }

        ((TextView) convertView.findViewById(R.id.date)).setText(item.date + "");

        return convertView;
    }
}
