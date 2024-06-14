package jp.arkw.swarmskytox;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private String host;
    private String userId;
    private String token;
    private ListView listView;
    private ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    private static final String[] simpleAdapterKey = {"text", "createdAt"};
    private static final int[] simpleAdapterId = {R.id.text, R.id.created_at};
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.list, simpleAdapterKey, simpleAdapterId);
        listView.setAdapter(simpleAdapter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("読み込み中…");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
        if (item.getItemId() == R.id.action_button_refresh) {
            update();
        } else if (item.getItemId() == R.id.action_button_settings) {
            Intent intent = new Intent(getApplication(), SettingsActivity.class);
            intent.putExtra("host", host);
            intent.putExtra("userId", userId);
            intent.putExtra("token", token);
            startActivityForResult(intent, 1);
        }
        return true;
    }

    private void update() {
        host = sharedPreferences.getString("host", "");
        userId = sharedPreferences.getString("userId", "");
        token = sharedPreferences.getString("token", "");
        arrayList.clear();
        simpleAdapter.notifyDataSetChanged();
        if (!host.equals("") && !userId.equals("")) {
            progressDialog.show();
            try {
                JSONObject request = new JSONObject();
                request.put("userId", userId);
                request.put("limit", 5);
                if (!token.equals("")) {
                    request.put("i", token);
                }
                new SendPostAsyncTask() {
                    @Override
                    protected void onPostExecute(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String text = jsonObject.getString("text");
                                if (text.indexOf("swarmapp") != -1 || text.indexOf("foursquare") != -1 || text.indexOf("ifttt") != -1) {
                                    if (text.indexOf("[swarmapp](") != -1) {
                                        text = text.replace("[swarmapp](", "");
                                        text = text.substring(0, text.length() - 1);
                                    }
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date date = simpleDateFormat.parse(jsonObject.getString("createdAt"));
                                    long unixCreatedAt = date.getTime() / 1000;
                                    long unixNow = System.currentTimeMillis() / 1000;
                                    String dateText = "";
                                    if (unixNow < unixCreatedAt) {
                                        dateText = "未来";
                                    } else if (unixNow == unixCreatedAt) {
                                        dateText = "たった今";
                                    } else {
                                        long unixDiff = unixNow - unixCreatedAt;
                                        if (unixDiff < 60) {
                                            dateText = unixDiff + "秒前";
                                        } else if (unixDiff >= 60 && unixDiff < 60 * 60) {
                                            dateText = (unixDiff / 60) + "分前";
                                        } else {
                                            dateText = "1時間以上前";
                                        }
                                    }
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("text", text);
                                    hashMap.put("createdAt", dateText);
                                    arrayList.add(hashMap);
                                }
                            }
                            simpleAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                        } catch (ParseException e) {
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
        String messsage = Uri.encode((String) arrayList.get(index).get("text"));
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
            editor.putString("token", intent.getStringExtra("token"));
            editor.apply();
            update();
        }
    }

    private void showAlert(String text) {
        new AlertDialog.Builder(this, R.style.Dialog_Theme_SwarmskyToX)
                .setTitle("")
                .setMessage(text)
                .setPositiveButton("OK", null)
                .show();
    }
}