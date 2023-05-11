package com.example.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.greenbetterymaster.R;

public class CustomTitleBlock extends RelativeLayout {
    private ConstraintLayout left, right;
    private ImageView iv_left,iv_right;
    private TextView tv_left, tv_title, tv_right;
    private int textSize;
    private EditText search_box;

    public CustomTitleBlock(Context context) {
        this(context, null);
    }

    public CustomTitleBlock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTitleBlock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.title_layout, this);
        initView();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTitleBlock, defStyleAttr, 0);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.CustomTitleBlock_leftText:
                    tv_left.setText(typedArray.getString(attr));
                    break;
                case R.styleable.CustomTitleBlock_leftTextColor:
                    tv_left.setTextColor(typedArray.getColor(attr, Color.BLACK));
                    break;
                case R.styleable.CustomTitleBlock_leftTextSize:
                    textSize = typedArray.getInteger(R.styleable.CustomTitleBlock_leftTextSize, 0);
                    tv_left.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                    break;
                case R.styleable.CustomTitleBlock_leftImage:
                    iv_left.setImageDrawable(typedArray.getDrawable(R.styleable.CustomTitleBlock_leftImage));
                    break;
                case R.styleable.CustomTitleBlock_centerText:
                    tv_title.setText(typedArray.getString(attr));
                    break;
                case R.styleable.CustomTitleBlock_centerTextColor:
                    tv_title.setTextColor(typedArray.getColor(attr, Color.BLACK));
                    break;
                case R.styleable.CustomTitleBlock_centerTextSize:
                    textSize = typedArray.getInteger(R.styleable.CustomTitleBlock_centerTextSize, 0);
                    tv_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                    break;
                case R.styleable.CustomTitleBlock_rightText:
                    tv_right.setText(typedArray.getString(attr));
                    break;
                case R.styleable.CustomTitleBlock_rightTextColor:
                    tv_right.setTextColor(typedArray.getColor(attr, Color.BLACK));
                    break;
                case R.styleable.CustomTitleBlock_rightTextSize:
                    textSize = typedArray.getInteger(R.styleable.CustomTitleBlock_rightTextSize, 0);
                    tv_right.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                    break;
                case R.styleable.CustomTitleBlock_rightImage:
                    iv_right.setImageDrawable(typedArray.getDrawable(R.styleable.CustomTitleBlock_rightImage));
                    break;
                case R.styleable.CustomTitleBlock_searchBox:
                    boolean aBoolean = typedArray.getBoolean(R.styleable.CustomTitleBlock_searchBox, false);
                    if (aBoolean) {
                        search_box.setVisibility(View.VISIBLE);
                    }else {
                        search_box.setVisibility(View.INVISIBLE);
                    }
                    break;
                case R.styleable.CustomTitleBlock_searchBoxHint:
                    String sHint = typedArray.getString(R.styleable.CustomTitleBlock_searchBoxHint);
                    search_box.setHint(sHint);
                    break;
                case R.styleable.CustomTitleBlock_searchBoxWidth:
                    float dimension = typedArray.getDimension(R.styleable.CustomTitleBlock_searchBoxWidth, 0);
                    search_box.setWidth((int) dimension);
                    break;
            }
        }
        typedArray.recycle();
    }

    public void setOnLeftClickListener(OnClickListener onClickListener){
        left.setOnClickListener(onClickListener);
    }

    public void setOnRightClickListener(OnClickListener onClickListener){
        right.setOnClickListener(onClickListener);
    }

    public void setOnSearchBoxClickListener(OnClickListener onClickListener){
        search_box.setOnClickListener(onClickListener);
    }

    public String getTitle(){
        return tv_title.getText().toString();
    }

    public String getSearchContent(){
        return search_box.getText().toString();
    }

    public void setSearchContent(String text){
        search_box.setText(text);
    }

    public void setTitle(String title){
        tv_title.setText(title);
    }

    public void setTitle(int titleId){
        tv_title.setText(titleId);
    }

    private void initView() {
        tv_left = findViewById(R.id.tv_left);
        tv_title = findViewById(R.id.tv_centent);
        iv_left = findViewById(R.id.iv_left);
        iv_right = findViewById(R.id.iv_right);
        tv_right = findViewById(R.id.tv_right);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
    }
}