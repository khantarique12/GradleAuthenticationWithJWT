package GradleAuthenticationWithJWT;

import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServlet;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;


@SuppressWarnings("serial")
@Path("/auth")
public class AuthResource extends HttpServlet{
	@Autowired
	JwtGeneratorValidator jwtgenval;
    /**
	 * 
	 */
	//http://localhost:8080/login/oauth2/code/google
 //http://localhost:8080/login/oauth2/code/github
	private static final OAuth2Service GOOGLE_OAUTH_SERVICE = new OAuth2Service(
            "13453443714-9t837gabp3cgdmb6s09mgg2vjh28mn0c.apps.googleusercontent.com",
            "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",
            "http://localhost:8080/auth/google/callback", 
            "https://accounts.google.com/o/oauth2/auth",
            "https://accounts.google.com/o/oauth2/token",
            "https://www.googleapis.com/oauth2/v2/userinfo"
    );
	
	private static final OAuth2Service GITHUB_OAUTH_SERVICE = new OAuth2Service(
            "efce244da773bdf6e17a",
            "688f9b987bbd497b22f0eff155b3b07ce2ad7d76",
            "http://localhost:8080/auth/github/callback",
            "https://github.com/login/oauth/authorize",
            "https://github.com/login/oauth/access_token",
            "https://api.github.com/user"
    );

	
    @GET
    @Path("/google/login")
    public String googleLogin(@Context UriInfo uriInfo, CustomAuthentication cutAuthentication) {
        String authorizationUrl = GOOGLE_OAUTH_SERVICE.getAuthorizationUrl();
        if (validateReturn(cutAuthentication)) {
        	return "Hello";
		}
        return "<html><body><h2>Login with Google</h2><a href='" + authorizationUrl + "'>Click here to login with Google</a></body></html>";
    }

    @GET
    @Path("/google/callback")
    public String googleCallback(@QueryParam("code") String code) {
        Map<String, Object> user = GOOGLE_OAUTH_SERVICE.fetchUserDetails(code);
        Date expiryDate = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
        Token tokenDetails = new Token(user.get("jwt").toString(), expiryDate);
        Authentication authentication = new CustomAuthentication();
        // Set custom data
        if (authentication instanceof CustomAuthentication) {
         CustomAuthentication customAuth = (CustomAuthentication) authentication;
         customAuth.setCustomData(tokenDetails);
         
     }
 
        return "<html><body><h2>Google User Details</h2><p>User Name: " + user.get("user") + "</p></body></html>";
    }

    @GET
    @Path("/github/login")
    public String githubLogin(@Context UriInfo uriInfo) {
        String authorizationUrl = GITHUB_OAUTH_SERVICE.getAuthorizationUrl();
        return "<html><body><h2>Login with GitHub</h2><a href='" + authorizationUrl + "'>Click here to login with GitHub</a></body></html>";
    }

    @GET
    @Path("/github/callback")
    public String githubCallback(@QueryParam("code") String code) {
    	Map<String, Object> user= GITHUB_OAUTH_SERVICE.fetchUserDetails(code);
        return "<html><body><h2>GitHub User Details</h2><p>User Name: " + user.get("user") + "</p></body></html>";
    }
    public  boolean validateReturn(CustomAuthentication headers) {
		Token jwtToken = null;
		if (headers != null) {
			jwtToken = (Token) headers.getCustomData();
		}
		if (jwtToken != null && jwtToken.getExpiryDate().after(new Date()) 
				&&  jwtgenval.validateToken(jwtToken.getToken())) {
			return true;
		} else {
			return false;
		}
	}
}

//import com.github.scribejava.apis.GoogleApi20;
//import com.github.scribejava.core.builder.ServiceBuilder;
//import com.github.scribejava.core.model.*;
//import com.github.scribejava.core.oauth.OAuth20Service;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.UriInfo;
//import java.io.IOException;
//import java.util.concurrent.ExecutionException;
//
//@Path("/")
//public class AuthResource {
//
//    private static final String CLIENT_ID = "13453443714-9t837gabp3cgdmb6s09mgg2vjh28mn0c.apps.googleusercontent.com";
//    private static final String CLIENT_SECRET = "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";
//    private static final String CALLBACK_URL = "http://localhost:8080/google/callback";
//
//    private static final OAuth20Service OAUTH_SERVICE;
//
//    static {
//        OAUTH_SERVICE = new ServiceBuilder(CLIENT_ID)
//                .apiSecret(CLIENT_SECRET)
//                //.scope("profile")
//                .callback(CALLBACK_URL)
//                .build(GoogleApi20.instance());
//    }
//
//    @GET
//    @Path("/google/auth")
//    public String googleAuth(@Context UriInfo uriInfo) {
//        String authorizationUrl = OAUTH_SERVICE.getAuthorizationUrl();
//
//        return "<html><body>" +
//                "<h2>Login with Google</h2>" +
//                "<a href='" + authorizationUrl + "'>Click here to login with Google</a>" +
//                "</body></html>";
//    }
//
//    @GET
//    @Path("/google/callback")
//    public String googleCallback(@QueryParam("code") String code) {
//        try {
//            OAuth2AccessToken accessToken = OAUTH_SERVICE.getAccessToken(code);
//            OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v2/userinfo");
//            OAUTH_SERVICE.signRequest(accessToken, request);
//            Response response = OAUTH_SERVICE.execute(request);
//
//            Gson gson = new Gson();
//            JsonObject userInfo = gson.fromJson(response.getBody(), JsonObject.class);
//
//            return "<html><body>" +
//                    "<h2>Google User Info</h2>" +
//                    "<p>User ID: " + userInfo.get("id").getAsString() + "</p>" +
//                    "<p>Name: " + userInfo.get("name").getAsString() + "</p>" +
//                    "</body></html>";
//        } catch (IOException | InterruptedException | ExecutionException e) {
//            throw new RuntimeException("Failed to retrieve Google user info.", e);
//        }
//    }
//}
//import com.github.scribejava.apis.GoogleApi20;
//import com.github.scribejava.core.builder.ServiceBuilder;
//import com.github.scribejava.core.model.*;
//import com.github.scribejava.core.oauth.OAuth20Service;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.UriInfo;
//import java.io.IOException;
//import java.util.concurrent.ExecutionException;
//
//@Path("/api")
//public class AuthResource {
//
//    private static final String CLIENT_ID = "13453443714-9t837gabp3cgdmb6s09mgg2vjh28mn0c.apps.googleusercontent.com";
//    private static final String CLIENT_SECRET = "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";
//    private static final String CALLBACK_URL = "http://localhost:8080/google/callback";
//
//    private static final OAuth20Service OAUTH_SERVICE;
//
//    static {
//        OAUTH_SERVICE = new ServiceBuilder(CLIENT_ID)
//                .apiSecret(CLIENT_SECRET)
//                .callback(CALLBACK_URL)
//                .build(GoogleApi20.instance());
//    }
//
//    
//    @GET
//    @Path("/google/auth")
//    public String googleAuth(@Context UriInfo uriInfo) {
//        String authorizationUrl = OAUTH_SERVICE.getAuthorizationUrl();
//
//        return "<html><body>" +
//                "<h2>Login with Google</h2>" +
//                "<a href='" + authorizationUrl + "'>Click here to login with Google</a>" +
//                "</body></html>";
//    }
//
//    @GET
//    @Path("/google/callback")
//    public String googleCallback(@QueryParam("code") String code) {
//        try {
//            OAuth2AccessToken accessToken = OAUTH_SERVICE.getAccessToken(code);
//            OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v2/userinfo");
//            OAUTH_SERVICE.signRequest(accessToken, request);
//            Response response = OAUTH_SERVICE.execute(request);
//
//            Gson gson = new Gson();
//            JsonObject userInfo = gson.fromJson(response.getBody(), JsonObject.class);
//
//            return "<html><body>" +
//                    "<h2>Google User Info</h2>" +
//                    "<p>User ID: " + userInfo.get("id").getAsString() + "</p>" +
//                    "<p>Name: " + userInfo.get("name").getAsString() + "</p>" +
//                    "</body></html>";
//        } catch (IOException | InterruptedException | ExecutionException e) {
//            throw new RuntimeException("Failed to retrieve Google user info.", e);
//        }
//    }
//}
//


//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.ws.rs.GET;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.QueryParam;
//import jakarta.ws.rs.core.Context;
//import jakarta.ws.rs.core.Response;
//
//import java.net.URI;
//
//@Path("/auth")
//public class AuthResource {
//    private static final String GOOGLE_CLIENT_ID = "13453443714-9t837gabp3cgdmb6s09mgg2vjh28mn0c.apps.googleusercontent.com";
//    private static final String GOOGLE_CLIENT_SECRET = "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";
//    private static final String GOOGLE_REDIRECT_URI = "http://localhost:8080/auth/google/callback";
//    private static final String GOOGLE_AUTHORIZATION_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
//    private static final String GOOGLE_TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";
//    private static final String GOOGLE_USER_INFO_ENDPOINT = "https://www.googleapis.com/oauth2/v3/userinfo";
//
//    private static final String GITHUB_CLIENT_ID = "your-github-client-id";
//    private static final String GITHUB_CLIENT_SECRET = "your-github-client-secret";
//    private static final String GITHUB_REDIRECT_URI = "http://localhost:8080/auth/github/callback";
//    private static final String GITHUB_AUTHORIZATION_ENDPOINT = "https://github.com/login/oauth/authorize";
//    private static final String GITHUB_TOKEN_ENDPOINT = "https://github.com/login/oauth/access_token";
//    private static final String GITHUB_USER_INFO_ENDPOINT = "https://api.github.com/user";
//
//    @GET
//    @Path("/google")
//    public Response redirectToGoogleAuthorizationPage() {
//        OAuth2Service oauth2Service = new OAuth2Service(
//                GOOGLE_CLIENT_ID,
//                GOOGLE_CLIENT_SECRET,
//                GOOGLE_REDIRECT_URI,
//                GOOGLE_AUTHORIZATION_ENDPOINT,
//                GOOGLE_TOKEN_ENDPOINT,
//                GOOGLE_USER_INFO_ENDPOINT,
//                null
//        );
//
//        String authorizationUrl = oauth2Service.getAuthorizationUrl();
//
//        return Response.seeOther(URI.create(authorizationUrl)).build();
//    }
//
//    @GET
//    @Path("/google/callback")
//    public Response handleGoogleCallback(@QueryParam("code") String code, @Context HttpServletRequest request) {
//        OAuth2Service oauth2Service = new OAuth2Service(
//                GOOGLE_CLIENT_ID,
//                GOOGLE_CLIENT_SECRET,
//                GOOGLE_REDIRECT_URI,
//                GOOGLE_AUTHORIZATION_ENDPOINT,
//                GOOGLE_TOKEN_ENDPOINT,
//                GOOGLE_USER_INFO_ENDPOINT,
//                null
//        );
//
//        User user = oauth2Service.fetchUserDetails(code);
//
//        // Store user information in session or perform any other required actions
//
//        return Response.seeOther(URI.create("/")).build();
//    }
//
//    @GET
//    @Path("/github")
//    public Response redirectToGitHubAuthorizationPage() {
//        OAuth2Service oauth2Service = new OAuth2Service(
//                GITHUB_CLIENT_ID,
//                GITHUB_CLIENT_SECRET,
//                GITHUB_REDIRECT_URI,
//                GITHUB_AUTHORIZATION_ENDPOINT,
//                GITHUB_TOKEN_ENDPOINT,
//                GITHUB_USER_INFO_ENDPOINT,
//                null
//        );
//
//        String authorizationUrl = oauth2Service.getAuthorizationUrl();
//
//        return Response.seeOther(URI.create(authorizationUrl)).build();
//    }
//
//    @GET
//    @Path("/github/callback")
//    public Response handleGitHubCallback(@QueryParam("code") String code, @Context HttpServletRequest request) {
//        OAuth2Service oauth2Service = new OAuth2Service(
//                GITHUB_CLIENT_ID,
//                GITHUB_CLIENT_SECRET,
//                GITHUB_REDIRECT_URI,
//                GITHUB_AUTHORIZATION_ENDPOINT,
//                GITHUB_TOKEN_ENDPOINT,
//                GITHUB_USER_INFO_ENDPOINT,
//                null
//        );
//
//        User user = oauth2Service.fetchUserDetails(code);
//
//        // Store user information in session or perform any other required actions
//
//        return Response.seeOther(URI.create("/")).build();
//    }
//}