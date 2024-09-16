package com.ibm.oauth;

public class OAuth2Details {

	private String scope;
	private String grantType;
	private String clientId;
	private String clientSecret;
	private String accessToken;
	private String refreshToken;
	private String username;
	private String password;
	private String authenticationServerUrl;
	private String resourceServerUrl;
	private boolean isAccessTokenRequest;
	
	public String getScope() {
		return this.scope;
	}
	
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public String getGrantType() {
		return this.grantType;
	}
	
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}
	
	public String getClientId() {
		return this.clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getClientSecret() {
		return this.clientSecret;
	}
	
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	
	public String getAccessToken() {
		return this.accessToken;
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public String getRefreshToken() {
		return this.refreshToken;
	}
	
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public String getAuthenticationServerUrl() {
		return this.authenticationServerUrl;
	}
	
	public void setAuthenticationServerUrl(String authenticationServerUrl) {
		this.authenticationServerUrl = authenticationServerUrl;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isAccessTokenRequest() {
		return this.isAccessTokenRequest;
	}
	
	public void setAccessTokenRequest(boolean isAccessTokenRequest) {
		this.isAccessTokenRequest = isAccessTokenRequest;
	}
	
	public String getResourceServerUrl() {
		return this.resourceServerUrl;
	}
	
	public void setResourceServerUrl(String resourceServerUrl) {
		this.resourceServerUrl = resourceServerUrl;
	}
}
