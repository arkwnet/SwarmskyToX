package jp.arkw.swarmskytox;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private String host;
    private String userId;
    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getSharedPreferences();
        listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        arrayAdapter = new ArrayAdapter<>(
            getApplicationContext(),
            android.R.layout.simple_list_item_1,
            arrayList
        );
        listView.setAdapter(arrayAdapter);
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplication(), SettingsActivity.class);
        intent.putExtra("host", host);
        intent.putExtra("userId", userId);
        startActivityForResult(intent, 1);
        return true;
    }

    private void update() {
        if (!host.equals("") || !userId.equals("")) {
            try {
                JSONObject request = new JSONObject();
                request.put("userId", userId);
                request.put("limit", 5);
                new SendPostAsyncTask() {
                    @Override
                    protected void onPostExecute(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            arrayList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String text = jsonObject.getString("text");
                                if (text.indexOf("swarmapp") != -1 && text.length() >= 1) {
                                    text = text.replace("[swarmapp](", "");
                                    text = text.substring(0, text.length() - 1);
                                    arrayList.add(text);
                                }
                            }
                            arrayAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                        }
                    }
                }.execute( new SendPostTaskParams(
                        "https://" + host + "/api/users/notes",
                        request.toString()
                ));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            showAlert("Misskeyのホスト名とユーザIDを設定してください。");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int index, long i) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String messsage = Uri.encode(arrayList.get(index));
        intent.setData(Uri.parse("twitter://post?message=" + messsage));
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            Editor editor = sharedPreferences.edit();
            editor.putString("host", intent.getStringExtra("host"));
            editor.putString("userId", intent.getStringExtra("userId"));
            editor.apply();
            getSharedPreferences();
            update();
        }
    }

    private void getSharedPreferences() {
        host = sharedPreferences.getString("host", "");
        userId = sharedPreferences.getString("userId", "");
    }

    private void showAlert(String text) {
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(text)
                .setPositiveButton("OK", null)
                .show();
    }
}