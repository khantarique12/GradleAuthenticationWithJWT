package GradleAuthenticationWithJWT;

import java.util.HashMap;
import java.util.Map;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class OAuth2Service {
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String authorizationEndpoint;
    private final String tokenEndpoint;
    private final String userInfoEndpoint;
    private final OAuth20Service oauthService;

    public OAuth2Service(
            String clientId,
            String clientSecret,
            String redirectUri,
            String authorizationEndpoint,
            String tokenEndpoint,
            String userInfoEndpoint
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.authorizationEndpoint = authorizationEndpoint;
        this.tokenEndpoint = tokenEndpoint;
        this.userInfoEndpoint = userInfoEndpoint;

        oauthService = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .callback(redirectUri)
                .defaultScope("profile email") // Add 'email' scope
                .build(new DefaultApi20() {
                    @Override
                    public String getAuthorizationBaseUrl() {
                    	System.out.println(authorizationEndpoint);
                        return authorizationEndpoint;
                    }

                    @Override
                    public String getAccessTokenEndpoint() {
                    	System.out.println(tokenEndpoint);
                        return tokenEndpoint;
                    }
                });
    }

    public String getAuthorizationUrl() {
        return oauthService.getAuthorizationUrl();
    }

    public Map<String, Object> fetchUserDetails(String code) {
        try {
        	Map<String, Object> map = new HashMap<>();
        	JwtGeneratorValidator jwtgenval = new JwtGeneratorValidator();
            OAuth2AccessToken accessToken = oauthService.getAccessToken(code);
            System.out.println(accessToken);
            OAuthRequest request = new OAuthRequest(Verb.GET, userInfoEndpoint);
            oauthService.signRequest(accessToken, request);
            Response response = oauthService.execute(request);
            JsonObject json = new Gson().fromJson(response.getBody(), JsonObject.class);

           
            String userName = null;
            String email = null;
           
            if (json.has("name")) {
                userName = json.get("name").getAsString();
                System.out.println(userName);
            }
            if (json.has("email")) {
                email = json.get("email").getAsString();
                System.out.println(email);
            }
            String token = jwtgenval.generateToken(userName); 
            System.out.println(token);
          
            User user = null;
            // Add null check before creating the User object
            if (userName != null || email != null ) {
            	map.put("user", user);
                map.put("jwt", token);
                return map;
            } else {
                // Handle the case where any of the required fields are null
                System.out.println("User details are incomplete");
                map.put("user", user);
                map.put("jwt", token);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}







//import com.github.scribejava.apis.GoogleApi20;
//import com.github.scribejava.core.builder.ServiceBuilder;
//import com.github.scribejava.core.model.*;
//import com.github.scribejava.core.oauth.OAuth20Service;
//import com.google.gson.JsonObject;
//
//import java.io.IOException;
//import java.util.concurrent.ExecutionException;
//
//import org.eclipse.jetty.http.MetaData.Response;
//
//public class OAuth2Service {
//  private static final String CLIENT_ID = "13453443714-9t837gabp3cgdmb6s09mgg2vjh28mn0c.apps.googleusercontent.com";
//  private static final String CLIENT_SECRET = "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";
//  private static final String CALLBACK_URL = "http://localhost:8080/google/callback";
//
//  private static final OAuth20Service OAUTH_SERVICE;
//
//  static {
//      OAUTH_SERVICE = new ServiceBuilder(CLIENT_ID)
//              .apiSecret(CLIENT_SECRET)
//              //.scope("profile")
//              .callback(CALLBACK_URL)
//              .build(GoogleApi20.instance());
//  }
//
//  public String getAuthorizationUrl() {
//      return OAUTH_SERVICE.getAuthorizationUrl();
//  }
//
//  public User fetchUserDetails(String code) {
//      try {
//          OAuth2AccessToken accessToken = OAUTH_SERVICE.getAccessToken(code);
//          OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v2/userinfo");
//          OAUTH_SERVICE.signRequest(accessToken, request);
//          com.github.scribejava.core.model.Response response = OAUTH_SERVICE.execute(request);
//
//          JsonObject userInfo = response.getJson().getAsJsonObject();
//          String userId = userInfo.get("id").getAsString();
//          String userName = userInfo.get("name").getAsString();
//
//          return new User(userId, userName);
//      } catch (IOException | InterruptedException | ExecutionException e) {
//          throw new RuntimeException("Failed to fetch user details.", e);
//      }
//  }
//}

//import com.github.scribejava.core.builder.ServiceBuilder;
//import com.github.scribejava.core.builder.api.DefaultApi20;
//import com.github.scribejava.core.model.*;
//import com.github.scribejava.core.oauth.OAuth20Service;
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//
//public class OAuth2Service {
//    private final String clientId;
//    private final String clientSecret;
//    private final String redirectUri;
//    private final String authorizationEndpoint;
//    private final String tokenEndpoint;
//    private final String userInfoEndpoint;
//    private final String userEmailsEndpoint;
//    private final OAuth20Service oauthService;
//
//    public OAuth2Service(
//            String clientId,
//            String clientSecret,
//            String redirectUri,
//            String authorizationEndpoint,
//            String tokenEndpoint,
//            String userInfoEndpoint,
//            String userEmailsEndpoint
//    ) {
//        this.clientId = clientId;
//        this.clientSecret = clientSecret;
//        this.redirectUri = redirectUri;
//        this.authorizationEndpoint = authorizationEndpoint;
//        this.tokenEndpoint = tokenEndpoint;
//        this.userInfoEndpoint = userInfoEndpoint;
//        this.userEmailsEndpoint = userEmailsEndpoint;
//
//        oauthService = new ServiceBuilder(clientId)
//                .apiSecret(clientSecret)
//                .callback(redirectUri)
//                .defaultScope("profile email")
//                .build(new DefaultApi20() {
//                    @Override
//                    public String getAuthorizationBaseUrl() {
//                        return authorizationEndpoint;
//                    }
//
//                    @Override
//                    public String getAccessTokenEndpoint() {
//                        return tokenEndpoint;
//                    }
//                });
//    }
//
//    public String getAuthorizationUrl() {
//        return oauthService.getAuthorizationUrl();
//    }
//
//    public User fetchUserDetails(String code) {
//        try {
//            OAuth2AccessToken accessToken = oauthService.getAccessToken(code);
//            OAuthRequest request = new OAuthRequest(Verb.GET, userInfoEndpoint);
//            oauthService.signRequest(accessToken, request);
//            Response response = oauthService.execute(request);
//            JsonObject json = new Gson().fromJson(response.getBody(), JsonObject.class);
//
//            String userId = null;
//            String userName = null;
//            String userEmail = null;
//
//            if (json.has("id")) {
//                userId = json.get("id").getAsString();
//            }
//            if (json.has("name")) {
//                userName = json.get("name").getAsString();
//            }
//
//            // Fetch user's email address from the userEmailsEndpoint
//            request = new OAuthRequest(Verb.GET, userEmailsEndpoint);
//            oauthService.signRequest(accessToken, request);
//            response = oauthService.execute(request);
//            JsonArray emailsArray = new Gson().fromJson(response.getBody(), JsonArray.class);
//            if (emailsArray.size() > 0) {
//                JsonObject emailObject = emailsArray.get(0).getAsJsonObject();
//                if (emailObject.has("value")) {
//                    userEmail = emailObject.get("value").getAsString();
//                }
//            }
//
//            return new User(userId, userName, userEmail);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}

//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.github.scribejava.core.builder.ServiceBuilder;
//import com.github.scribejava.core.builder.api.DefaultApi20;
//import com.github.scribejava.core.model.*;
//import com.github.scribejava.core.oauth.OAuth20Service;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//
//import java.util.Date;
//
//public class OAuth2Service {
//    // ...
//
//    public String generateJwtToken(User user, String secretKey, long expirationTimeMillis) {
//        Algorithm algorithm = Algorithm.HMAC256(secretKey);
//
//        // Set the expiration time
//        Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeMillis);
//
//        // Build the JWT token
//        String token = JWT.create()
//                .withClaim("userId", user.getUserId())
//                .withClaim("userName", user.getUserName())
//                .withClaim("email", user.getEmail())
//                .withClaim("login", user.getLogin())
//                .withExpiresAt(expirationDate)
//                .sign(algorithm);
//
//        return token;
//    }
//
//    // ...
//}