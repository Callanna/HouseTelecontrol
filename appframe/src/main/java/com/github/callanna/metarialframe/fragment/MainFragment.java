package com.github.callanna.metarialframe.fragment;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.callanna.metarialframe.R;
import com.github.callanna.metarialframe.activity.BookDetailActivity;
import com.github.callanna.metarialframe.activity.MainTwoDemo;
import com.github.callanna.metarialframe.adapter.SimpleAdapter;
import com.github.callanna.metarialframe.base.BaseFragment;
import com.github.callanna.metarialframe.data.Book;
import com.github.callanna.metarialframe.util.LogUtil;
import com.github.callanna.metarialframe.util.ToastUtil;
import com.github.callanna.metarialframe.view.listviewpullload.PullCallback;
import com.github.callanna.metarialframe.view.listviewpullload.PullToLoadView;
import com.github.callanna.metarialframe.view.listviewpullload.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2015/12/21.
 */
public class MainFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private SimpleAdapter mAdapter;
    private FloatingActionButton mFabButton;
    private RecyclerView mRecyclerView;

    @Override
    protected void onBaseFragmentCreate(Bundle savedInstanceState) {
        setMyContentView(R.layout.fragment_books);
        mAdapter = new SimpleAdapter(getContext());

        mRecyclerView = (RecyclerView)findViewById(R.id.listview);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(mAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,R.color.colorPrimary,R.color.primary_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doSearch();
            }
        });
        setUpFAB();
    }

    private void setUpFAB() {
        swipeRefreshLayout.setRefreshing(true);
        mFabButton = (FloatingActionButton) findViewById(R.id.fab_normal);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO MarteialDialog
                ToastUtil.show(context,"TODO Dialog");
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void startFABAnimation() {
       // mFabButton.animate().setDuration(300).rotationY(360).x(100).y(100).start();

       // ObjectAnimator.ofInt(mFabButton,"bgcolor", Color.BLUE,Color.GREEN);

        mFabButton.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(500)
                .setDuration(300)
                .start();
    }
    private void doSearch() {
        mAdapter.clearItems();
        List<Book> books = new ArrayList<>();
        books.add(new Book("花千骨1"));
        books.add(new Book("花千骨2"));
        books.add(new Book("花千骨3"));
        mAdapter.updateItems(books, true);
        swipeRefreshLayout.setRefreshing(false);
        startFABAnimation();
        mAdapter.updateItems(books, true);
    }

    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Book book = mAdapter.getBook(position);
            Intent intent = new Intent(getActivity(), BookDetailActivity.class);
            intent.putExtra("book", book);
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            view.findViewById(R.id.ivBook), getString(R.string.transition_book_img));

            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        }
    };

}
