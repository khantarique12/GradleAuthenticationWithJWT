package GradleAuthenticationWithJWT;

import java.util.Date;

public class Token {

	private String token;
	private Date expiryDate;

	public Token(String token, Date expiryDate) {
		this.token = token;
		this.expiryDate = expiryDate;
	}

	public String getToken() {
		return token;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}
}
