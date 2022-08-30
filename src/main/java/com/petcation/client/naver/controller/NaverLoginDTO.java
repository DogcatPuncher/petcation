package com.petcation.client.naver.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import com.petcation.client.naver.service.NaverLoginApi;



public class NaverLoginDTO {

   private final static String CLIENT_ID = "AwL0bpRCRduT7YQPZged";
   private final static String CLIENT_SECRET = "yCToMmjWqp";
   private final static String REDIRECT_URI = "http://localhost:8080/member/naverLogin";
   private final static String SESSION_STATE = "oauth_state";
   
   /* �׾Ʒ� ���� URL ���� Method */
   public String getAuthorizationUrl(HttpSession session) {

      /* ���� ��ȿ�� ������ ���Ͽ� ������ ���� */
      String state = generateRandomString();
      /* ������ ���� ���� session�� ���� */
      setSession(session, state);

      /* Scribe���� �����ϴ� ���� URL ���� ����� �̿��Ͽ� �׾Ʒ� ���� URL ���� */
      OAuth20Service oauthService = new ServiceBuilder().apiKey(CLIENT_ID).apiSecret(CLIENT_SECRET)
            .callback(REDIRECT_URI).state(state) // �ռ� ������ �������� ���� URL������ �����
            .build(NaverLoginApi.instance());

      return oauthService.getAuthorizationUrl();
   }

   /* �׾Ʒ� Callback ó�� �� AccessToken ȹ�� Method */
   public OAuth2AccessToken getAccessToken(HttpSession session, String code, String state) throws IOException {

      /* Callback���� ���޹��� ���������� �������� ���ǿ� ����Ǿ��ִ� ���� ��ġ�ϴ��� Ȯ�� */
      String sessionState = getSession(session);
      if (StringUtils.equals(sessionState, state)) {

         OAuth20Service oauthService = new ServiceBuilder().apiKey(CLIENT_ID).apiSecret(CLIENT_SECRET)
               .callback(REDIRECT_URI).state(state).build(NaverLoginApi.instance());

         /* Scribe���� �����ϴ� AccessToken ȹ�� ������� �׾Ʒ� Access Token�� ȹ�� */
         OAuth2AccessToken accessToken = oauthService.getAccessToken(code);
         return accessToken;
      }
      return null;
   }

   /* ���� ��ȿ�� ������ ���� ���� ������ */
   private String generateRandomString() {
      return UUID.randomUUID().toString();
   }

   /* http session�� ������ ���� */
   private void setSession(HttpSession session, String state) {
      session.setAttribute(SESSION_STATE, state);
   }

   /* http session���� ������ �������� */
   private String getSession(HttpSession session) {
      return (String) session.getAttribute(SESSION_STATE);
   }

   /* ������ ��ȸ API URL */
   private final static String PROFILE_API_URL = "https://openapi.naver.com/v1/nid/me";

   /* Access Token�� �̿��Ͽ� ���̹� ����� ������ API�� ȣ�� */
   public String getUserProfile(OAuth2AccessToken oauthToken) throws IOException {

      OAuth20Service oauthService = new ServiceBuilder().apiKey(CLIENT_ID).apiSecret(CLIENT_SECRET)
            .callback(REDIRECT_URI).build(NaverLoginApi.instance());

      OAuthRequest request = new OAuthRequest(Verb.GET, PROFILE_API_URL, oauthService);
      oauthService.signRequest(oauthToken, request);
      Response response = request.send();
      return response.getBody();
   }
   
   //����
   public void naverProfile() {
        String token = "YOUR_ACCESS_TOKEN";// ���̹� �α��� ���� ��ū;
           String header = "Bearer " + token; // Bearer ������ ���� �߰�
           try {
               String apiURL = "https://openapi.naver.com/v1/nid/me";
               URL url = new URL(apiURL);
               HttpURLConnection con = (HttpURLConnection)url.openConnection();
               con.setRequestMethod("GET");
               con.setRequestProperty("Authorization", header);
               int responseCode = con.getResponseCode();
               BufferedReader br;
               if(responseCode==200) { // ���� ȣ��
                   br = new BufferedReader(new InputStreamReader(con.getInputStream()));
               } else {  // ���� �߻�
                   br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
               }
               String inputLine;
               StringBuffer response = new StringBuffer();
               while ((inputLine = br.readLine()) != null) {
                   response.append(inputLine);
               }
               br.close();
               System.out.println(response.toString());
           } catch (Exception e) {
               System.out.println(e);
           }
       }
   

}