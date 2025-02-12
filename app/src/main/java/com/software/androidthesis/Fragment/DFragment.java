package com.software.androidthesis.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.software.androidThesis.R;
import com.software.androidthesis.Activity.UserEditActivity;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/1/7 11:43
 * @Decription:
 */
public class DFragment extends Fragment {
    private Button userEditBtn;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_d, container, false);
        userEditBtn = view.findViewById(R.id.userEditBtn);

        userEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserEditActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
