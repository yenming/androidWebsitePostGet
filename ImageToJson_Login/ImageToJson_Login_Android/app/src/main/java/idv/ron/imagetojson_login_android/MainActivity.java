package idv.ron.imagetojson_login_android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private static final int REQUEST_TAKE_PICTURE = 0;
    private static final int REQUEST_LOGIN = 1;
    private Bitmap picture;
    private ProgressDialog progressDialog;
    private DataUploadTask dataUploadTask;
    private ImageView ivTakePicture;
    private Button btLogout;
    private File out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ivTakePicture = (ImageView) findViewById(R.id.ivTakePicture);
        btLogout = (Button) findViewById(R.id.btLogout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 從偏好設定檔中取得登入狀態來決定是否顯示「登出」
        SharedPreferences pref = getSharedPreferences(MyData.PREF_FILE,
                MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if (login) {
            btLogout.setVisibility(View.VISIBLE);
        } else {
            btLogout.setVisibility(View.INVISIBLE);
        }
    }


    public void onLogoutClick(View view) {
        SharedPreferences pref = getSharedPreferences(MyData.PREF_FILE,
                MODE_PRIVATE);
        pref.edit().putBoolean("login", false).apply();
        view.setVisibility(View.INVISIBLE);
    }

    // 點擊ImageView會拍照
    public void onTakePictureClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定存檔路徑
        out = Environment.getExternalStorageDirectory();
        out = new File(out, "photo.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
        if (isIntentAvailable(this, intent)) {
            startActivityForResult(intent, REQUEST_TAKE_PICTURE);
        } else {
            Toast.makeText(this, R.string.msg_NoCameraApp, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    // 按下上傳按鈕
    public void onUploadClick(View view) {
        if (picture == null) {
            Toast.makeText(this, R.string.msg_NotUploadWithoutPicture, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Intent loginIntent = new Intent(this, LoginDialogActivity.class);
        startActivityForResult(loginIntent, REQUEST_LOGIN);
    }

    public boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 手機拍照App拍照完成後可以取得照片圖檔
                case REQUEST_TAKE_PICTURE:
                    // picture = (Bitmap) data.getExtras().get("data"); //只取得小圖
                    picture = downSize(out.getPath());
                    ivTakePicture.setImageBitmap(picture);
                    break;
                // 也可取得自行設計登入畫面的帳號密碼
                case REQUEST_LOGIN:
                    SharedPreferences pref = getSharedPreferences(MyData.PREF_FILE,
                            MODE_PRIVATE);
                    String name = pref.getString("user", "");
                    String password = pref.getString("password", "");
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    picture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    byte[] image = out.toByteArray();

                    dataUploadTask = new DataUploadTask();
                    dataUploadTask.execute(MyData.URL, name, password, image);
                    break;
            }
        }
    }

    private Bitmap downSize(String path) {
        Bitmap picture = BitmapFactory.decodeFile(path);
        // 設定寬度不超過width，並利用Options.inSampleSize來縮圖
        int width = 512;
        if (picture.getWidth() > width) {
            Options options = new Options();
            // 若原始照片寬度無法整除width，則inSampleSize + 1
            options.inSampleSize = picture.getWidth() % width == 0 ? picture
                    .getWidth() / width : picture.getWidth() / width + 1;
            picture = BitmapFactory.decodeFile(out.getPath(), options);
            System.gc();
        }
        String text = picture.getWidth() + "x" + picture.getHeight();
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        return picture;
    }

    class DataUploadTask extends AsyncTask<Object, Integer, User> {
        Gson gson = new Gson();

        @Override
        // invoked on the UI thread immediately after the task is executed.
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        // invoked on the background thread immediately after onPreExecute()
        protected User doInBackground(Object... params) {
            String url = params[0].toString();
            String name = params[1].toString();
            String password = params[2].toString();
            byte[] image = (byte[]) params[3];
            User loginUser = new User(name, password, image);
            String jsonStr = gson.toJson(loginUser);
            List<NameValuePair> requestParameters = new ArrayList<>();
            requestParameters.add(new BasicNameValuePair("jsonStr", jsonStr));

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            try {
                post.setEntity(new UrlEncodedFormEntity(requestParameters,
                        HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                Log.e("exception", e.toString());
            }

            String entity = "";
            try {
                HttpResponse response = client.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity responseEntity = response.getEntity();
                    entity = EntityUtils.toString(responseEntity);
                } else {
                    Log.d("statusCode", Integer.toString(statusCode));
                }
            } catch (Exception e) {
                Log.e("exception", e.toString());
            }

            return gson.fromJson(entity, User.class);
        }

        @Override
        /*
         * invoked on the UI thread after the background computation finishes.
		 * The result of the background computation is passed to this step as a
		 * parameter.
		 */
        protected void onPostExecute(User user) {
            progressDialog.cancel();
            String name = user.getName();
            String password = user.getPassword();
            byte[] image = user.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0,
                    image.length);
            TextView tvResultUser = (TextView) findViewById(R.id.tvResultUser);
            ImageView ivResultImage = (ImageView) findViewById(R.id.ivResponseImage);
            tvResultUser.setText("name: " + name + "\npassword: " + password);
            ivResultImage.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onPause() {
        if (dataUploadTask != null) {
            dataUploadTask.cancel(true);
        }
        super.onPause();
    }

    @Override
    // 螢幕轉向之前，先將資料儲存至Bundle，方便轉向完畢後取出
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (picture != null) {
            outState.putParcelable("picture", picture);
        }
    }

    @Override
    // 螢幕轉向完畢後，將轉向之前存的資料取出
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Bitmap picture = savedInstanceState.getParcelable("picture");
        if (picture != null) {
            this.picture = picture;
            ivTakePicture.setImageBitmap(picture);
        }
    }

}
