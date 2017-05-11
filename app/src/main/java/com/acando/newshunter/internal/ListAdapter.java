package com.acando.newshunter.internal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.acando.newshunter.Global;
import com.acando.newshunter.UtilNetwork;
import com.acando.newshunter.content.Article;
import com.acando.newshunter.R;
import com.acando.newshunter.content.Source;
import com.acando.newshunter.database.table.SourceTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.CustomViewHolder> {

    private List<Article> mArticles;
    private Context mContext;

    public ListAdapter(Context context, List<Article> articles) {
        this.mArticles = articles;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
        return new CustomViewHolder(view);
    }

    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final Article article = mArticles.get(position);

        holder.title.setText(article.title);
        holder.desc.setText(article.desc);

        Source source = SourceTable.get(mContext, article.source);
        holder.source.setText(source.name);

        Calendar cal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:mm");
        cal.setTimeInMillis(article.date);
        holder.date.setText(sdf.format(cal.getTime()));

        Bitmap bitmap = ((Global)((ListActivity) mContext).getApplication()).getBitmapFromMemCache(article.imageURL);
        if (bitmap == null && article.imageURL != null && !article.imageURL.equals("")) {
            holder.image.setImageBitmap(null);
            ((ListActivity) mContext).mImageTask.addItem(article, holder.image);
        } else {
            holder.image.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return (null != mArticles ? mArticles.size() : 0);
    }

    public void insertItems(ArrayList<Article> items) {
        mArticles.addAll(items);
        notifyDataSetChanged();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected final ImageView image;
        protected final TextView title, desc, date, source;

        public CustomViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.image);
            this.title = (TextView) view.findViewById(R.id.title);
            this.desc = (TextView) view.findViewById(R.id.desc);
            this.date = (TextView) view.findViewById(R.id.date);
            this.source = (TextView) view.findViewById(R.id.source);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra("url", mArticles.get(getAdapterPosition()).link);
            mContext.startActivity(intent);
        }
    }
}
