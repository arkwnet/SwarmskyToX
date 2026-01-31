package jp.arkw.swarmskytox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean isSave;
    private EditText editTextHost;
    private EditText editTextUserId;
    private EditText editTextToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        isSave = false;
        TextView textViewVersion = findViewById(R.id.text_view_version);
        textViewVersion.setText("Version " + getResources().getString(R.string.versionName));
        Intent intent = getIntent();
        editTextHost = findViewById(R.id.edit_text_host);
        editTextUserId = findViewById(R.id.edit_text_userid);
        editTextToken = findViewById(R.id.edit_text_token);
        editTextHost.setText(intent.getStringExtra("host"));
        editTextUserId.setText(intent.getStringExtra("userId"));
        editTextToken.setText(intent.getStringExtra("token"));
        findViewById(R.id.button_save).setOnClickListener(this);
        findViewById(R.id.text_view_url_web).setOnClickListener(this);
        findViewById(R.id.text_view_url_github).setOnClickListener(this);
        findViewById(R.id.text_view_privacy).setOnClickListener(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_settings));
        }
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.button_save) {
            isSave = true;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("host", editTextHost.getText().toString());
            editor.putString("userId", editTextUserId.getText().toString());
            editor.putString("token", editTextToken.getText().toString());
            editor.apply();
            showAlert(getString(R.string.alert_save));
        } else if (v.getId() == R.id.text_view_url_web) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_url_web)));
            startActivity(intent);
        } else if (v.getId() == R.id.text_view_url_github) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_url_github)));
            startActivity(intent);
        } else if (v.getId() == R.id.text_view_privacy) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_url_privacy)));
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuButton){
        boolean result = true;
        int buttonId = menuButton.getItemId();
        if (buttonId == android.R.id.home) {
            if (isSave) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                setResult(RESULT_OK, intent);
            }
            finish();
        } else {
            result = super.onOptionsItemSelected(menuButton);
        }
        return result;
    }

    private void showAlert(String text) {
        new AlertDialog.Builder(this, R.style.Dialog_Theme_SwarmskyToX)
            .setTitle("")
            .setMessage(text)
            .setPositiveButton("OK", null)
            .show();
    }
}