package com.zoe.driibo.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zoe.driibo.R;
import com.zoe.driibo.ui.MainActivity;
import com.zoe.driibo.ui.adapter.DrawerAdapter;

public class DrawerFragment extends Fragment {
	private ListView mListView;

    private DrawerAdapter mAdapter;

    private MainActivity mActivity;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View contentView = inflater.inflate(R.layout.fragment_drawer, null);
        mListView = (ListView) contentView.findViewById(R.id.listView);
        mAdapter = new DrawerAdapter(mListView);
        mListView.setAdapter(mAdapter);
        mListView.setItemChecked(0, true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.setItemChecked(position, true);
//                mActivity.setCategory(Category.values()[position]);
            }
        });
        return contentView;
    }
}
