package com.software.androidthesis.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.software.androidThesis.R;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/21 0:54
 * @Decription:
 */
public class ChildFragment1 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child1, container, false);
        return view;
    }

}
