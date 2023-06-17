package GradleAuthenticationWithJWT;

import org.eclipse.jetty.server.Authentication;

public class CustomAuthentication implements Authentication {
  
    private Token customData;

    public CustomAuthentication() {
     
    }

    public Token getCustomData() {
        return customData;
    }

    public void setCustomData(Token customData) {
        this.customData = customData;
    }
}