package jp.arkw.swarmskytox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextHost;
    private EditText editTextUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();
        editTextHost = findViewById(R.id.edit_text_host);
        editTextUserId = findViewById(R.id.edit_text_userid);
        editTextHost.setText(intent.getStringExtra("host"));
        editTextUserId.setText(intent.getStringExtra("userId"));
        findViewById(R.id.button_ok).setOnClickListener(this);
        findViewById(R.id.text_view_url_web).setOnClickListener(this);
        findViewById(R.id.text_view_url_github).setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.button_ok) {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra("host", editTextHost.getText().toString());
            intent.putExtra("userId", editTextUserId.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        } else if (v.getId() == R.id.text_view_url_web) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_url_web)));
            startActivity(intent);
        } else if (v.getId() == R.id.text_view_url_github) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_url_github)));
            startActivity(intent);
        }
    }
}