package com.ttsbco.pmmonitoring;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ttsbco.config.AppConfig;
import com.ttsbco.models.PowerMeterJsonModel;
import com.ttsbco.services.AppController;
import com.ttsbco.services.CheckConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class ActivityHistory extends AppCompatActivity
{
    TextView txtWarn, txtTitle;
    ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        txtWarn = (TextView) findViewById(R.id.txtWarning);
        txtTitle = (TextView) findViewById(R.id.txtTitle);

        progressLoading = (ProgressBar) findViewById(R.id.progressLoading);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            int address = extras.getInt("address");
            int id = extras.getInt("id");
            String title = extras.getString("title", "");
            txtTitle.setText(title);

            getDataFromServer(id, address);
        }

    }

    private void showLoading()
    {
        hideWarn();
        progressLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading()
    {
        progressLoading.setVisibility(View.GONE);
    }

    private void showWarn(String message)
    {
        hideLoading();
        txtWarn.setVisibility(View.VISIBLE);
        txtWarn.setText(message);
    }

    private void hideWarn()
    {
        txtWarn.setVisibility(View.GONE);
    }

    private void getDataFromServer(final int id, final int address)
    {
        showLoading();
        final Handler handler = new Handler();
        handler.post(new Runnable()
        {
            public void run()
            {
                if (!CheckConnection.IsConnected)
                {
                    showWarn("شما متصل به اینترنت نیستید");
                    return;
                }
                if (isFinishing())
                {
                    return;
                }
                try
                {
                    StringRequest request = new StringRequest(Request.Method.GET, AppConfig.getHistoryUrl(id, address, 0, 7), new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try
                            {
                                JSONObject result = new JSONObject(response);

                                if (!result.getBoolean("isSuccess"))
                                {
                                    showWarn(new String(result.getString("message").getBytes("ISO-8859-1"), "UTF-8"));
                                }
                                else
                                {
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
                                hideLoading();
                            }
                        }
                    }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            showWarn("درخواست اطلاعات با مشکل مواجه شد");
                            hideLoading();
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
                    hideLoading();
                }
            }
        });
    }
}
