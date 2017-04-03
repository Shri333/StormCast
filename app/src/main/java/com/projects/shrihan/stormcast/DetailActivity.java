package com.projects.shrihan.stormcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private TextView mDescriptionTextView;
    private TextView mMessageTextView;
    private ImageView mImageView;

    private Alert mAlert = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
                mAlert = (Alert) bundle.getSerializable("alert");
        }

        mDescriptionTextView = (TextView) findViewById(R.id.alert_desc1);
        mMessageTextView = (TextView) findViewById(R.id.alert_msg);
        mImageView = (ImageView) findViewById(R.id.imageView2);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        if (mAlert != null)
            updateUi();
    }

    private void updateUi() {
        mDescriptionTextView.setText(mAlert.getDescription());
        mMessageTextView.setText(mAlert.getMessage());
        String type = mAlert.getType();
        if (type == null) {
            mImageView.setImageResource(R.drawable.ic_hea);
            return;
        }
        switch (type.toUpperCase()) {
            case "FIR":
                mImageView.setImageResource(R.drawable.ic_fir);
                break;
            case "FLO":
                mImageView.setImageResource(R.drawable.ic_flo);
                break;
            case "FOG":
                mImageView.setImageResource(R.drawable.ic_fog);
                break;
            case "HEA":
                mImageView.setImageResource(R.drawable.ic_hea);
                break;
            case "HWW":
                mImageView.setImageResource(R.drawable.ic_hww);
                break;
            case "REC":
                mImageView.setImageResource(R.drawable.ic_rec);
                break;
            case "SEW":
                mImageView.setImageResource(R.drawable.ic_sew);
                break;
            case "SPE":
                mImageView.setImageResource(R.drawable.ic_spe);
                break;
            case "SVR":
                mImageView.setImageResource(R.drawable.ic_svr);
                break;
            case "TOR":
                mImageView.setImageResource(R.drawable.ic_tor);
                break;
            case "TOW":
                mImageView.setImageResource(R.drawable.ic_tow);
                break;
            case "VOL":
                mImageView.setImageResource(R.drawable.ic_vol);
                break;
            case "WAT":
                mImageView.setImageResource(R.drawable.ic_wat);
                break;
            case "WIN":
                mImageView.setImageResource(R.drawable.ic_win);
                break;
            case "WND":
                mImageView.setImageResource(R.drawable.ic_wnd);
                break;
            case "WRN":
                mImageView.setImageResource(R.drawable.ic_wrn);
                break;
            case "":
                mImageView.setImageResource(R.drawable.ic_hea);
                break;
        }
    }
}
