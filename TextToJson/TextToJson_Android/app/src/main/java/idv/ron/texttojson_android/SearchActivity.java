package idv.ron.texttojson_android;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
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
import com.google.gson.reflect.TypeToken;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends ActionBarActivity {
    private final static String TAG = "SearchActivity";
    private ProgressDialog progressDialog;
    private RetrieveBookTask retrieveBookTask;
    private RetrieveCategoryTask retrieveCategoryTask;
    private Spinner spCategory;
    private ListView lvBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        spCategory = (Spinner) findViewById(R.id.spCategory);
        lvBook = (ListView) findViewById(R.id.lvBook);

        retrieveCategoryTask = new RetrieveCategoryTask();
        retrieveCategoryTask.execute(MyData.URL);
        List<String> items = null;
        try {
            items = retrieveCategoryTask.get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
    }

    class RetrieveCategoryTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SearchActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String url = params[0];
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> requestParameters = new ArrayList<>();
            requestParameters.add(new BasicNameValuePair("param", "category"));
            try {
                post.setEntity(new UrlEncodedFormEntity(requestParameters,
                        HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, e.toString());
            }

            String entity = "";
            try {
                HttpResponse response = client.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity responseEntity = response.getEntity();
                    entity = EntityUtils.toString(responseEntity);
                } else {
                    Log.d(TAG, Integer.toString(statusCode));
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();

            return gson.fromJson(entity, listType);
        }

        @Override
        protected void onPostExecute(List<String> result) {
            progressDialog.cancel();
        }
    }

    public void onSearchClick(View v) {
        retrieveBookTask = new RetrieveBookTask();
        Object item = spCategory.getSelectedItem();
        if (item == null || item.toString().trim().length() <= 0) {
            Toast.makeText(this, "book category cannot be retrieved",
                    Toast.LENGTH_SHORT).show();
        } else {
            String category = item.toString().trim();
            retrieveBookTask.execute(MyData.URL, category);
        }

    }

    public class RetrieveBookTask extends
            AsyncTask<String, Integer, List<Book>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SearchActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<Book> doInBackground(String... params) {
            String url = params[0];
            String category = params[1];
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            List<NameValuePair> requestParameters = new ArrayList<>();
            requestParameters.add(new BasicNameValuePair("param", category));
            try {
                post.setEntity(new UrlEncodedFormEntity(requestParameters,
                        HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, e.toString());
            }

            String entity = "";
            try {
                HttpResponse response = client.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity responseEntity = response.getEntity();
                    entity = EntityUtils.toString(responseEntity);
                } else {
                    Log.d(TAG, Integer.toString(statusCode));
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Book>>() {
            }.getType();
            return gson.fromJson(entity, listType);
        }

        @Override
        protected void onPostExecute(List<Book> result) {
            progressDialog.cancel();
            showResult(result);
        }
    }

    public void showResult(List<Book> result) {
        final BookListAdapter adapter = new BookListAdapter(this, result);
        lvBook.setAdapter(adapter);
        lvBook.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.expand(position);
                lvBook.setItemChecked(position, true);
            }
        });
    }

    private class BookListAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<Book> bookList;
        private boolean[] bookDetailExpanded;

        public BookListAdapter(Context context, List<Book> bookList) {
            this.layoutInflater = LayoutInflater.from(context);
            this.bookList = bookList;
            this.bookDetailExpanded = new boolean[bookList.size()];
        }

        @Override
        public int getCount() {
            return bookList.size();
        }

        @Override
        public Object getItem(int position) {
            return bookList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return bookList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.book_listview_item, parent, false);
            }
            TextView tvBookTitle = (TextView) convertView
                    .findViewById(R.id.tvBookTitle);
            TextView tvBookDetail = (TextView) convertView
                    .findViewById(R.id.tvBookDetail);
            Book book = bookList.get(position);

            tvBookTitle.setText(book.getName() + "  $" + book.getPrice());
            tvBookDetail.setText("Author: " + book.getAuthor() + "  Type: "
                    + book.getType());
            tvBookDetail
                    .setVisibility(bookDetailExpanded[position] ? View.VISIBLE
                            : View.GONE);
            return convertView;
        }

        public void expand(int position) {
            // 被點擊的資料列才會彈出內容，其他資料列的內容會自動縮起來
            // for (int i=0; i<newsExpanded.length; i++) {
            // newsExpanded[i] = false;
            // }
            // newsExpanded[position] = true;

            bookDetailExpanded[position] = !bookDetailExpanded[position];
            notifyDataSetChanged();
        }

    }

    @Override
    protected void onPause() {
        if (retrieveCategoryTask != null) {
            retrieveCategoryTask.cancel(true);
        }

        if (retrieveBookTask != null) {
            retrieveBookTask.cancel(true);
        }

        super.onPause();
    }

}
