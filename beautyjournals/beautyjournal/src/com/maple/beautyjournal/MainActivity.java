package com.maple.beautyjournal;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.base.BaseFragmentActivity;
import com.maple.beautyjournal.broadcast.BootCompleteBroadcast;
import com.maple.beautyjournal.entitiy.Product;
import com.maple.beautyjournal.fragment.PersonCenterFragment;
import com.maple.beautyjournal.fragment.ProductCategoryFragment;
import com.maple.beautyjournal.provider.Beauty;
import com.maple.beautyjournal.provider.DatabaseHelper;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends BaseFragmentActivity {

    private FragmentManager fragmentManager;
    private ImageView searchBar;
    private ImageView home_page;
    private ImageView shoping_city;
    private ImageView person_center;
    private ImageView shoping_car;
    private LinearLayout bottom ;
    private LinearLayout home_page_layout;
    private LinearLayout shoping_city_layout;
    private TextView home_page_text,shoping_city_text;
    private int colorInt,otherColorInt;
    private TextView shoping_car_amount_pro;
    List<Product> products = new ArrayList<Product>();
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_home_2);
        initData();
        initCompoment();
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }

        fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        HomeFragment homeFragment=new HomeFragment();
        transaction.replace(R.id.content,homeFragment);
        transaction.commit();
        bottom = (LinearLayout)findViewById(R.id.bottom) ;
        //HideBottom();
        Intent intent = new Intent(BootCompleteBroadcast.ACTION_APPBOOTCOMPLETED);
        sendBroadcast(intent);
        initOtherData();
        home_page_layout=(LinearLayout)findViewById(R.id.home_page_layout);
        shoping_city_layout=(LinearLayout)findViewById(R.id.shoping_city_layout);
        home_page_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                home_page.setBackgroundResource(R.drawable.tab_home_2);
                home_page_text.setTextColor(colorInt);
                shoping_city.setBackgroundResource(R.drawable.tab_store_2);
                shoping_city_text.setTextColor(otherColorInt);
                fragmentManager=getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                HomeFragment homeFragment=new HomeFragment();
                transaction.replace(R.id.content,homeFragment);
                transaction.commit();
                return false;
            }
        });
        shoping_city_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                home_page.setBackgroundResource(R.drawable.home_page_2);
                home_page_text.setTextColor(otherColorInt);
                shoping_city.setBackgroundResource(R.drawable.shoping_city_2);
                shoping_city_text.setTextColor(colorInt);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment product_cate = Fragment.instantiate(MainActivity.this, ProductCategoryFragment.class.getName(), null);
                // Fragment product_cate2 = Fragment.instantiate(MainActivity.this, ProductCategoryNewFragment.class.getName(), null);
                ft.replace(R.id.content, product_cate);
                ft.addToBackStack(null);
                ft.commit();
                getSupportFragmentManager().executePendingTransactions();
                return false;
            }
        });
    }

    public void HideBottom(){
        bottom.setVisibility(View.GONE);
    }

    private void initOtherData(){
        new GetCategoryTask().execute();
        new  GetFunctionTask().execute();
        new GetDataTask().execute();
        getCityMap();
    }
    @Override
    protected void onStart(){
        super.onStart();

        products = SettingsUtil.getProductsInKart(MainActivity.this);
        shoping_car_amount_pro.setText(""+products.size());

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null&&bundle.get("key").equals("gotoproductpage")){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment product_cate = Fragment.instantiate(MainActivity.this, ProductCategoryFragment.class.getName(), null);
            // Fragment product_cate2 = Fragment.instantiate(MainActivity.this, ProductCategoryNewFragment.class.getName(), null);
            ft.replace(R.id.content, product_cate);
            ft.addToBackStack(null);
            ft.commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }
    private void initCompoment(){

        searchBar=(ImageView)findViewById(R.id.search);
        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent search=new Intent();
                search.setClass(MainActivity.this,SearchActivity.class);
                startActivity(search);
                return false;
            }
        });

        home_page=(ImageView)findViewById(R.id.home_page);
        shoping_city=(ImageView)findViewById(R.id.shoping_city);
        person_center=(ImageView)findViewById(R.id.person_center);
        shoping_car=(ImageView)findViewById(R.id.shopingcar);
        home_page_text=(TextView)findViewById(R.id.home_page_text);
        shoping_city_text=(TextView)findViewById(R.id.shoping_city_text);
        shoping_car_amount_pro=(TextView)findViewById(R.id.shoping_car_amount_pro);
        products = SettingsUtil.getProductsInKart(MainActivity.this);
        shoping_car_amount_pro.setText(""+products.size());
        shoping_car.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(new Intent(MainActivity.this,ShopingCarActivity.class));
                return false;
            }
        });


        person_center.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fragmentManager=getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                PersonCenterFragment personCenterFragment=new PersonCenterFragment();
                transaction.replace(R.id.content,personCenterFragment);
                transaction.commit();

                return false;
            }
        });

        colorInt=home_page_text.getCurrentTextColor();
        otherColorInt=shoping_city_text.getCurrentHintTextColor();
    }

    private void initData() {

        new Thread(new Runnable() {
            @Override
            public void run() {


                String filename = MainActivity.this.getDatabasePath(DatabaseHelper.DB_NAME).getAbsolutePath();

                /**
                 * android系统sqlite数据库路径，数据库文件：DatabaseHelper.DB_NAME
                 * add by snail.
                 * 2014/5/2.
                 */
                //String filename = MainActivity.this.getDatabasePath(DatabaseHelper.DB_NAME).getAbsolutePath();

                Log.d("XXX",filename);

                File file = new File(filename);
                if (file.exists()) {

                    return;
                }
                file.getParentFile().mkdirs();
                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    is = getAssets().open("beauty.db");
                    fos = new FileOutputStream(filename);
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                        if (is != null) {
                            is.close();
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }


    @Override
    public void onBackPressed() {
        if(this.mOnBackPressedListener != null){
            this.mOnBackPressedListener.doBack();
        }else{
            FragmentManager fm = getSupportFragmentManager();
            if (!fm.popBackStackImmediate()) {
                new AlertDialog.Builder(this).setTitle(R.string.quit_app_prompt)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setNegativeButton(android.R.string.cancel, null).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult, " + requestCode + ", " + resultCode + ", " + data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //内部类，获取Data
    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String url = URLConstant.BRAND_LIST_URL;
            Context context = MainActivity.this;
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(jsonObject)) {
                    JSONArray array = jsonObject.getJSONArray("info");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject brand = array.getJSONObject(i);
                        ContentValues cv = new ContentValues();
                        cv.put(Beauty.Brand.NAME, brand.getString("name"));
                        cv.put(Beauty.Brand.BRAND_ID, brand.getString("id"));
                        cv.put(Beauty.Brand.PINYIN, brand.getString("pinyin"));
                        cv.put(Beauty.Brand.FIRST_CHAR, brand.getString("pinyin"));
                        Uri uri = context.getContentResolver().insert(Beauty.Brand.CONTENT_URI, cv);
                        if (uri != null) {
                            Log.d("MainActivity", "inserted: " + uri);
                        } else {
                            Log.d("MainActivity", "error inserting: " + cv);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //内部类，获取Category
    private class GetCategoryTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String url = URLConstant.CATEGORY_LIST_URL;
            Context context = MainActivity.this;
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(jsonObject)) {
                    JSONObject info = jsonObject.getJSONObject("info");
                    String cat1Id = "", cat2Id = "", cat3Id = "", cat1Name = "", cat2Name = "", cat3Name = "";
                    Iterator<String> keys = info.keys();
                    while (keys.hasNext()) {
                        cat1Id = keys.next();
                        JSONObject cat1 = info.getJSONObject(cat1Id);
                        cat1Name = cat1.getString("name");
                        JSONObject cat1Sub = cat1.getJSONObject("sub");
                        Iterator<String> cat2Keys = cat1Sub.keys();
                        while (cat2Keys.hasNext()) {
                            cat2Id = cat2Keys.next();
                            JSONObject cat2 = cat1Sub.getJSONObject(cat2Id);
                            cat2Name = cat2.getString("name");
                            JSONObject cat2Sub = cat2.getJSONObject("sub");
                            Iterator<String> cat3Keys = cat2Sub.keys();
                            while (cat3Keys.hasNext()) {
                                cat3Id = cat3Keys.next();
                                cat3Name = cat2Sub.getString(cat3Id);
                                ContentValues cv = new ContentValues();
                                cv.put(Beauty.Category.CATEGORY_ID, cat1Id);
                                cv.put(Beauty.Category.NAME, cat1Name);
                                cv.put(Beauty.Category.SUB_CATEGORY_ID, cat2Id);
                                cv.put(Beauty.Category.SUB_CATEGORY, cat2Name);
                                cv.put(Beauty.Category.SUB_SUB_CATEGORY_ID, cat3Id);
                                cv.put(Beauty.Category.SUB_SUB_CATEGORY, cat3Name);
                                Uri uri = context.getContentResolver().insert(Beauty.Category.CONTENT_URI, cv);
                                if (uri != null) {
                                    Log.d("MainActivity", "inserted: " + uri);
                                } else {
                                    Log.d("MainActivity", "error inserting: " + cv);
                                }
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    /**
     * 获取商品功效列表
     * 从网络获取信息，缓存到本地数据库.
     * add by snail.
     * 2014/5/2.
     */


    private class GetFunctionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String url = URLConstant.FUNCTION_LIST_URL;
            Context context = MainActivity.this;
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(jsonObject)) {
                    JSONObject info = jsonObject.getJSONObject("info");
                    String cat1Id = "", cat2Id = "", cat1Name = "", cat2Name = "";
                    Iterator<String> keys = info.keys();
                    while (keys.hasNext()) {
                        cat1Id = keys.next();
                        JSONObject cat1 = info.getJSONObject(cat1Id);
                        cat1Name = cat1.getString("name");
                        JSONObject cat1Sub = cat1.getJSONObject("sub");
                        Iterator<String> cat2Keys = cat1Sub.keys();
                        while (cat2Keys.hasNext()) {
                            cat2Id = cat2Keys.next();
                            cat2Name = cat1Sub.getString(cat2Id);
                            ContentValues cv = new ContentValues();
                            cv.put(Beauty.Function.FUNCTION_ID, cat1Id);
                            cv.put(Beauty.Function.NAME, cat1Name);
                            cv.put(Beauty.Function.SUB_FUNCTION_ID, cat2Id);
                            cv.put(Beauty.Function.SUB_FUNCTION, cat2Name);
                            Log.d("MainActivity", "put categories to db: " + cv.toString());

                            Uri uri = context.getContentResolver().insert(Beauty.Function.CONTENT_URI, cv);
                            if (uri != null) {
                                Log.d("MainActivity", "inserted: " + uri);
                            } else {
                                Log.d("MainActivity", "error inserting: " + cv);
                            }

                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void getCityMap() {
        FileReader fr = null;
        BufferedReader bfr = null;
        try {
            File f = new File(Environment.getExternalStorageDirectory(), "area.csv");
            Log.d("MainActivity", "file is " + f.getAbsolutePath());
            if (f.exists()) {
                fr = new FileReader(f);
                bfr = new BufferedReader(fr);
                String line = bfr.readLine();
                while (line != null) {
                    String[] data = line.split(",");
                    ContentValues cv = new ContentValues();
                    cv.put(Beauty.Area.PROVINCE, data[0]);
                    cv.put(Beauty.Area.CITY, data[1]);
                    cv.put(Beauty.Area.DISTRICT, data[2]);
                    cv.put(Beauty.Area.PAY_AT_ARRIVAL, data[3].contentEquals("是") ? 1 : 0);
                    cv.put(Beauty.Area.POS, 0);
                    ;
                    Log.d("MainActivity", cv.toString());
                    getContentResolver().insert(Beauty.Area.CONTENT_URI, cv);
                    line = bfr.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bfr != null) { bfr.close(); }
                if (fr != null) { fr.close(); }
            } catch (Exception e) {

            }
        }
    }


}
