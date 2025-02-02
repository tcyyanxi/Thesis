package com.software.androidthesis.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.software.androidThesis.R;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 11:41
 * @Decription:
 */
public class AFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a, container, false);
        return view;
    }

}
