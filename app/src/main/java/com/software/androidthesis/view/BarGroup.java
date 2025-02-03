package com.software.androidthesis.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.software.androidThesis.R;
import com.software.androidthesis.entity.BarEntity;
import com.software.androidthesis.util.DensityUtil;

import java.util.List;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/2 21:34
 * @Decription:自定义View容器
 */

public class BarGroup extends LinearLayout {
    private List<BarEntity> datas;
    public BarGroup(Context context) {
        super(context);
        init();
    }

    public BarGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);

    }

    public void setDatas(List<BarEntity> datas) {
        if (datas != null) {
            this.datas = datas;
        }
    }

    public void setHeight(float maxValue,int height) {
        if (datas != null) {
            for (int i = 0; i < datas.size(); i++) {
                /*通过柱状图的最大值和相对比例计算出每条柱状图的高度*/
//                float barHeight = datas.get(i).getAllcount()/maxValue*height;
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_column_bar, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DensityUtil.dip2px(getContext(),30),height);
//                view.setLayoutParams(lp);
                ((BarView) view.findViewById(R.id.barView)).setData(datas.get(i));
                (view.findViewById(R.id.barView)).setLayoutParams(lp);
                ((TextView)view.findViewById(R.id.title)).setText(getFeedString(datas.get(i).getTitle()));
//                DecimalFormat mFormat=new DecimalFormat("##.#");
//                ((TextView)view.findViewById(R.id.percent)).setText(mFormat.format(datas.get(i).getAllcount()));
                addView(view);
            }
        }
    }

    /*字符串換行*/
    private String getFeedString(String text) {
        StringBuilder sb = new StringBuilder(text);
        int length = sb.length();
        for (int i = 6; i < length; i += 7) {
            sb.insert(i, "\n");
            length++; // Adjust length to account for the inserted newline
        }
        return sb.toString();
    }
}
