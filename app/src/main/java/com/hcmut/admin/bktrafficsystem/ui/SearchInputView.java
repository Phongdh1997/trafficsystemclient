package com.hcmut.admin.bktrafficsystem.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.hcmut.admin.bktrafficsystem.R;

public class SearchInputView extends CardView {
    private ImageView imgClearText;
    private ImageView imgBack;
    private AutoCompleteTextView txtSearchInput;

    private OnClickListener backClickListener;

    public SearchInputView(@NonNull Context context) {
        super(context);
        initView();
    }

    public SearchInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SearchInputView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_search_input, this, false);
        addView(view);

        imgClearText = findViewById(R.id.imgClearText);
        imgBack = findViewById(R.id.imgBack);
        txtSearchInput = findViewById(R.id.txtSearchInput);
    }

    public void updateView() {
        if (txtSearchInput.getText().toString().equals("")) {
            handleBackAndClearView(false);
        } else {
            handleBackAndClearView(true);
        }
    }

    /**
     * Handle Back and Clear button when have or haven't search result
     */
    public void handleBackAndClearView(boolean isHaveSearchResult) {
        if (isHaveSearchResult) {
            imgBack.setImageResource(R.drawable.ic_arrow_back);
            imgClearText.setVisibility(View.VISIBLE);
            imgBack.setOnClickListener(backClickListener);
        } else {
            imgBack.setImageResource(R.drawable.ic_search);
            imgClearText.setVisibility(View.GONE);
            imgBack.setOnClickListener(null);
            txtSearchInput.setText("");
        }
    }

    public void setImgBackEvent(OnClickListener onClickListener) {
        this.backClickListener = onClickListener;
    }

    public void setImgClearTextEvent(OnClickListener onClickListener) {
        imgClearText.setOnClickListener(onClickListener);
    }

    public void setTxtSearchInputEvent(OnFocusChangeListener onFocusChangeListener) {
        txtSearchInput.setOnFocusChangeListener(onFocusChangeListener);
    }

    public void setTxtSearchInputText(String text) {
        txtSearchInput.setText(text);
    }
}
