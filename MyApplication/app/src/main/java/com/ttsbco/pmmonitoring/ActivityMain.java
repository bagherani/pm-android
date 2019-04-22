package com.ttsbco.pmmonitoring;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ttsbco.adapters.PowerMeterAdapter;
import com.ttsbco.config.AppConfig;
import com.ttsbco.models.PowerMeterJsonModel;
import com.ttsbco.services.AppController;
import com.ttsbco.services.CheckConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ActivityMain extends AppCompatActivity
{
    SwipeRefreshLayout mSwipeRefresh;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    ArrayList<PowerMeterJsonModel> lstData;
    TextView txtWarn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadSettings();

        txtWarn = (TextView) findViewById(R.id.txtWarning);

        lstData = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager LLayoutManager;
        LLayoutManager = new LinearLayoutManager(ActivityMain.this);
        LLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(LLayoutManager);
        mAdapter = new PowerMeterAdapter(this, lstData);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.main_swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorPrimary);
        Toast.makeText(ActivityMain.this, "در حال دریافت اطلاعات", Toast.LENGTH_SHORT).show();

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                getStatusFromServer();
            }
        });

        CheckConnection.Init(this);

        new Handler().postDelayed(
                (new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mSwipeRefresh.setRefreshing(true);

                    }
                }), 400);

        new Handler().postDelayed(
                (new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getStatusFromServer();
                    }
                }), 2000);

    }

    private void loadSettings()
    {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        AppConfig.siteURL = prefs.getString("server", "");
        AppConfig.port = prefs.getInt("port", 80);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Intent intent = new Intent(ActivityMain.this, ActivitySettings.class);
                startActivityForResult(intent, 0);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void showWarn(String message)
    {
        mSwipeRefresh.setRefreshing(false);
        txtWarn.setVisibility(View.VISIBLE);
        txtWarn.setText(message);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void hideWarn()
    {
        txtWarn.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void getStatusFromServer()
    {
        if (AppConfig.siteURL == null || AppConfig.siteURL.isEmpty())
        {
            mSwipeRefresh.setRefreshing(false);
            showWarn("لطفا در بخش تنظیمات آدرس سرور را وارد کنید");
            return;
        }

        final Handler handler = new Handler();

        handler.post(new Runnable()
        {
            public void run()
            {
                if (!CheckConnection.IsConnected)
                {
                    mSwipeRefresh.setRefreshing(false);
                    showWarn("شما متصل به اینترنت نیستید");
                    return;
                }
                if (isFinishing())
                {
                    mSwipeRefresh.setRefreshing(false);
                    return;
                }
                try
                {
                    StringRequest request = new StringRequest(Request.Method.GET, AppConfig.getSiteURL(), new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            mSwipeRefresh.setRefreshing(false);
                            try
                            {
                                JSONObject result = new JSONObject(response);

                                if (!result.getBoolean("isSuccess"))
                                {
                                    showWarn(new String(result.getString("message").getBytes("ISO-8859-1"), "UTF-8"));
                                }
                                else
                                {
                                    lstData.clear();
                                    JSONArray jsonArrayPowerMeters = result.getJSONArray("data");

                                    for (int i = 0; i < jsonArrayPowerMeters.length(); i++)
                                    {
                                        JSONObject jsonPowerMeter = jsonArrayPowerMeters.getJSONObject(i);
                                        if (jsonPowerMeter != null)
                                        {
                                            PowerMeterJsonModel powerMeter = new PowerMeterJsonModel();
                                            powerMeter.setHasError(jsonPowerMeter.getBoolean("hasError"));
                                            powerMeter.setMessage(new String(jsonPowerMeter.getString("message").getBytes("ISO-8859-1"), "UTF-8"));
                                            powerMeter.setName(new String(jsonPowerMeter.getString("name").getBytes("ISO-8859-1"), "UTF-8"));
                                            powerMeter.setId(jsonPowerMeter.getInt("id"));
                                            powerMeter.setPowerMeter(true);
                                            lstData.add(powerMeter);

                                            try
                                            {
                                                JSONArray jsonArrayRegister = jsonPowerMeter.getJSONArray("registers");

                                                for (int j = 0; j < jsonArrayRegister.length(); j++)
                                                {
                                                    JSONObject jsonRegister = jsonArrayRegister.getJSONObject(j);
                                                    if (jsonRegister != null)
                                                    {
                                                        PowerMeterJsonModel register = new PowerMeterJsonModel();
                                                        register.setId(powerMeter.getId());
                                                        register.setName(new String(jsonRegister.getString("name").getBytes("ISO-8859-1"), "UTF-8"));
                                                        register.setVal(jsonRegister.getDouble("value"));
                                                        register.setMax(jsonRegister.getDouble("max"));
                                                        register.setMin(jsonRegister.getDouble("min"));
                                                        register.setAddresss(jsonRegister.getInt("address"));
                                                        register.setPowerMeterName(powerMeter.getName());
                                                        register.setPowerMeter(false);
                                                        lstData.add(register);
                                                    }
                                                }
                                            }
                                            catch (Exception exx)
                                            {
                                            }

                                        }
                                    }
                                    hideWarn();
                                }

                            }

                            catch (Exception ex)
                            {
                                Log.d("receive Error1", ex.getMessage());
                                showWarn("اطلاعات دریافتی نامعتبر");
                            }
                            finally
                            {
                                mSwipeRefresh.setRefreshing(false);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            showWarn("درخواست اطلاعات با مشکل مواجه شد");
                        }
                    });


                    // set request timeout & max retry
                    int socketTimeout = AppConfig.socketTimeOut;
                    int maxRetries = AppConfig.maxRetries;
                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, maxRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    request.setRetryPolicy(policy);

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(request);

                }
                catch (Exception e)
                {
                    Log.d("receive Error2", e.getMessage());
                    showWarn("خطا در دریافت اطلاعات رخ داد");
                }
                finally
                {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == 0)
        {
            loadSettings();
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج دوباره کلید بازگشت را بزنید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}
