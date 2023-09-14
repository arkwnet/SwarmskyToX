package jp.arkw.swarmskytox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Set;

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
    }

    public void onClick(View v) {
        if (v.getId() == R.id.button_ok) {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra("host", editTextHost.getText().toString());
            intent.putExtra("userId", editTextUserId.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}