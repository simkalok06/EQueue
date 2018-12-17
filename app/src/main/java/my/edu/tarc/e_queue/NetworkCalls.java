package my.edu.tarc.e_queue;
import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkCalls {
    private RequestQueue requestQueue;
    private static Context context;

    private static NetworkCalls ourInstance = new NetworkCalls();

    public static NetworkCalls getInstance() {
        return ourInstance;
    }

    private NetworkCalls() {
    }

    public void setContext(Context targetContext){
        context = targetContext;
        requestQueue = Volley.newRequestQueue(context);
    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}