package com.github.callanna.metarialframe.view.listviewpullload;

/**
 * @author bian.xd
 */
public interface PullCallback {

    void onLoadMore();

    void onRefresh();

    boolean isLoading();

    boolean hasLoadedAllItems();

}
