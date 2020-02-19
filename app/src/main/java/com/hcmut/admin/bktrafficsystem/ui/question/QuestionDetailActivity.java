package com.hcmut.admin.bktrafficsystem.ui.question;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.hcmut.admin.bktrafficsystem.R;

public class QuestionDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        String title = getIntent().getStringExtra("TITLE");
        String url = getIntent().getStringExtra("URL");
        TextView tvTitle = findViewById(R.id.title_question);
        tvTitle.setText(title);
        findViewById(R.id.ivBack).setOnClickListener(this);
        WebView wv;
        wv = findViewById(R.id.webView);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("file:///android_asset/" + url);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }
}
