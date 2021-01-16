package com.example.demo.config;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


/**
 * ログインしていないと閲覧できないページを設定するクラス
 * @author root1
 *
 */
@Component
public class LoginCheckFilter implements Filter{

	final Logger logger = LoggerFactory.getLogger(LoginCheckFilter.class);

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,ServletException{
        System.out.println("ログインチェック");

        // セッションが存在しない場合NULLを返す
        HttpSession session = ((HttpServletRequest)req).getSession(false);

        if(session != null){
            // セッションがNULLでなければ、通常どおりの遷移
            chain.doFilter(req, res);
        }else{
            // セッションがNullならば、ログイン画面へ飛ばす
        	HttpServletResponse httpResponse = (HttpServletResponse) res;
        	httpResponse.sendRedirect("/");
        }

    }

    // フィルター設定をBeanに登録
    @Bean
    public FilterRegistrationBean<LoginCheckFilter> headerValidatorFilter() {
        FilterRegistrationBean<LoginCheckFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoginCheckFilter());

        // ログインを必要とするURLを定義
        registrationBean.addUrlPatterns(
        		"/api/*",
        		"/signout",
        		"/championship-member/*",
        		"/championship/*",
        		"/account/*",
        		"/user/*",
        		"/brawler/*"
        	);
        return registrationBean;
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        // 初回アクセス時に、URLにSessionIDが付与されるのを防ぐ
        // https://blog.ik.am/entries/353
        ServletContextInitializer initializer = servletContext -> {
            servletContext.setSessionTrackingModes(
                Collections.singleton(SessionTrackingMode.COOKIE)
            );
        };
        return initializer;
    }
}