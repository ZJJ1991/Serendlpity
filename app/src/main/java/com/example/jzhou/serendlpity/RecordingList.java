package com.example.jzhou.serendlpity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Bel on 25.02.2016.
 */
public class RecordingList extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_of_recordings, container, false);
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}