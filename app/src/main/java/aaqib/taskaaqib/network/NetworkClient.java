package aaqib.taskaaqib.network;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import aaqib.taskaaqib.listener.NetworkCallListener;
import aaqib.taskaaqib.models.ApiResponse;
import aaqib.taskaaqib.util.AppUtil;


/**
 * A simple network class to make GET requests
 */
public class NetworkClient {

    /**
     * Executes GET requests
     *
     * @param httpURL  The GET URL
     * @param headers  An arrayMap of headers (if any) , can be null
     * @param listener An interface to give callbacks {@link NetworkCallListener}
     */
    public static void executeGet(
            String httpURL,
            ArrayMap<String, String> headers,
            NetworkCallListener listener) {
        ExecuteTask executeTask = new ExecuteTask(httpURL, headers, listener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            executeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            executeTask.execute();
        }
    }

    /**
     * An asyncTask to execute GET requests
     * {@link ApiResponse}
     */
    public static class ExecuteTask extends AsyncTask<String, Void, ApiResponse> {

        String mHttpURL;
        ArrayMap<String, String> mHeaders;
        NetworkCallListener mListener;

        public ExecuteTask(String httpURL, ArrayMap<String, String> headers, NetworkCallListener listener) {
            mHttpURL = httpURL;
            mHeaders = headers;
            mListener = listener;
        }

        @Override
        protected void onPreExecute() {
            if (mListener != null) {
                mListener.onCallStart();
            }
        }

        @Override
        protected ApiResponse doInBackground(String... params) {
            ApiResponse apiResponse = null;
            if (!TextUtils.isEmpty(mHttpURL)) {
                try {
                    InputStream is = null;
                    try {
                        URL url = new URL(mHttpURL);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(10000);
                        conn.setConnectTimeout(15000);
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        if (mHeaders != null && mHeaders.size() > 0) {
                            String key;
                            for (int i = 0; i < mHeaders.size(); i++) {
                                key = mHeaders.keyAt(i);
                                conn.setRequestProperty(key, mHeaders.get(key));
                            }
                        }
                        conn.connect();
                        apiResponse = new ApiResponse();
                        apiResponse.setUrl(mHttpURL);
                        int responseCode = conn.getResponseCode();
                        apiResponse.setCode(responseCode);
                        if (responseCode >= 200 && responseCode < 205) {
                            apiResponse.setSuccess(true);
                            is = conn.getInputStream();
                            apiResponse.setResponse(AppUtil.readStream(is));
                        } else {
                            apiResponse.setSuccess(false);
                            is = conn.getErrorStream();
                            apiResponse.setError(AppUtil.readStream(is));
                        }
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return apiResponse;
        }

        @Override
        protected void onPostExecute(ApiResponse result) {
            if (mListener != null) {
                mListener.onResponse(result);
            }
            mListener = null;
        }

    }
}
