package com.example.demo.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 本番環境での固定IP利用のためにプロキシ設定を行うクラス
 * @author root1
 *
 */
@Component
public class RestTemplateConfig implements RestTemplateCustomizer {

    @Override
    public void customize(RestTemplate restTemplate) {

		try {
	    // APIがIPの指定を必要とするため固定IPから通信
        URL proxyUrl;

			proxyUrl = new URL(System.getenv("QUOTAGUARDSTATIC_URL"));

	        String userInfo = proxyUrl.getUserInfo();
	        String user = userInfo.substring(0, userInfo.indexOf(':'));
	        String password = userInfo.substring(userInfo.indexOf(':') + 1);

	        String proxyHost = proxyUrl.getHost();
	        int proxyPort = proxyUrl.getPort();

	        URLConnection conn = null;

	        System.setProperty("http.proxyHost", proxyHost);
	        System.setProperty("http.proxyPort", Integer.toString(proxyPort));

	        Authenticator.setDefault(new Authenticator() {
	                protected PasswordAuthentication getPasswordAuthentication() {
	                    return new PasswordAuthentication(user, password.toCharArray());
	                }
	            });

	        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
	        factory.setProxy(new Proxy(Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));

	        URL url = new URL("http://ip.quotaguard.com");
	        conn = url.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

	        String inputLine;
	        while ((inputLine = in.readLine()) != null)
	            System.out.println(inputLine);

	        in.close();

	        HttpClientBuilder builder;
	        if (proxyHost != null && !proxyHost.isEmpty() && proxyPort != 0) {
	            builder = HttpClientBuilder.create().setProxy(new HttpHost(proxyHost, proxyPort));
	            if (user != null && !user.isEmpty() && password != null && !password.isEmpty()) {
	                AuthScope authScope = new AuthScope(proxyHost, proxyPort);
	                UsernamePasswordCredentials usernamePassword = new UsernamePasswordCredentials(user, password);
	                BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
	                credentialsProvider.setCredentials(authScope, usernamePassword);
	                builder.setDefaultCredentialsProvider(credentialsProvider);
	            }
	        }
	        else {
	            builder = HttpClientBuilder.create();
	        }

	        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(builder.build());


	        restTemplate.setRequestFactory(requestFactory);
		} catch (IOException e) {
			e.printStackTrace();

		}

	};
}
