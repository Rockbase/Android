package com.rockbase.unplugged.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.rockbase.unplugged.R;

public class CreditsActivity extends Activity implements View.OnClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.credits);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_logo:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse((String) getText(R.string.rockbase_url)));
                startActivity(i);
                break;
        }
    }
}
