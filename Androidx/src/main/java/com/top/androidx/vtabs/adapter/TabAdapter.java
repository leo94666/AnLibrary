package com.top.androidx.vtabs.adapter;


import com.top.androidx.vtabs.widget.TabView;


public interface TabAdapter {
    int getCount();

    TabView.TabBadge getBadge(int position);

    TabView.TabIcon getIcon(int position);

    TabView.TabTitle getTitle(int position);

    int getBackground(int position);
}
