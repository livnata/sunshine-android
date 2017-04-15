package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    String mDetaildForcast="";
    public static final String EXTRA_MSG = "extra_msg";

    public DetailActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        mDetaildForcast =(String) getActivity().getIntent().getSerializableExtra(EXTRA_MSG);
        String detailTexts;
        if (savedInstanceState != null ){
            mDetaildForcast= (String) savedInstanceState.getSerializable(EXTRA_MSG);
        }
        TextView detailText = (TextView) getView().findViewById(R.id.detail_text);
        detailText.setText(mDetaildForcast);
    }
}
