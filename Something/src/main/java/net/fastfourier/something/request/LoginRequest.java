package net.fastfourier.something.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import net.fastfourier.something.util.SomePreferences;

import org.jsoup.nodes.Document;

/**
 * Created by matthewshepard on 1/31/14.
 */
public class LoginRequest extends HTMLRequest<Boolean> {
    public LoginRequest(String username, String password, Response.Listener<Boolean> success, Response.ErrorListener error) {
        super("https://forums.somethingawful.com/account.php", Request.Method.POST, success, error);
        addParam("action", "login");
        addParam("username", username);
        addParam("password", password);
    }

    @Override
    public Boolean parseHtmlResponse(Request<Boolean> request, NetworkResponse response, Document document) throws Exception {
        if(SomePreferences.confirmLogin()){
            return true;
        }
        throw new SomeError("Failed to Login");
    }
}
