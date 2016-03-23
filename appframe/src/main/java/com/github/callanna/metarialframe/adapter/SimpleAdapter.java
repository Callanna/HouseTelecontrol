package com.github.callanna.metarialframe.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.callanna.metarialframe.R;
import com.github.callanna.metarialframe.data.Book;
import com.github.callanna.metarialframe.util.OSUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2015/12/21.
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {
    private Context mContext;
    private final int mBackground;
    private List<Book> mBooks = new ArrayList<Book>();
    private final TypedValue mTypedValue = new TypedValue();

    private static final int ANIMATED_ITEMS_COUNT = 4;

    private boolean animateItems = false;
    private int lastAnimatedPosition = -1;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SimpleAdapter(Context context) {
        this.mContext = context;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView ivBook;
        public TextView tvTitle;
        public TextView tvDesc;

        public int position;

        public ViewHolder(View v) {
            super(v);
            ivBook = (ImageView) v.findViewById(R.id.ivBook);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvDesc = (TextView) v.findViewById(R.id.tvDesc);
        }
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void runEnterAnimation(View view, int position) {
        if (!animateItems || position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(OSUtil.getScreenHeight(mContext));
            view.animate()
                    .translationY(0)
                    .setStartDelay(100 * position)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }


    public void updateItems(List<Book> books, boolean animated) {
        animateItems = animated;
        lastAnimatedPosition = -1;
        mBooks.addAll(books);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mBooks.clear();
        notifyDataSetChanged();
    }


    @Override
    public SimpleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        Book book = mBooks.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvDesc.setText("");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mBooks.size();
    }


    public Book getBook(int pos) {
        return mBooks.get(pos);
    }

    public void add(Book s) {
        mBooks.add(s);
        notifyDataSetChanged();
    }

    public void clear() {
        mBooks.clear();
        notifyDataSetChanged();
    }


}
