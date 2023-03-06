package com.training.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.training.model.Account;


public class LoginCheckFilter implements Filter {
		
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
		throws IOException, ServletException {
		// 檢查 Sesssion Scope 中是否存在登入的使用者 ("account").
		HttpServletRequest httpRequest = (HttpServletRequest)request;		
		HttpSession session = httpRequest.getSession();
		Account account = (Account) session.getAttribute("account");
        // 如果存在就放行.
        if(account != null){
            chain.doFilter(request,response);
        } else {
            // 如果不存在就必須先判斷目前是否為登入的請求,是的話則進行後續帳密驗證比對 LoginAction
        	String requestURI = httpRequest.getRequestURI();
        	String action = request.getParameter("action");
            if(requestURI.endsWith("LoginAction.do") && "login".equals(action)) {
            	
            	chain.doFilter(request,response);
            	
            } else {
                // 不是登入驗證的請求或是SessionTimeOut,轉向到 "/Login.jsp" 要求重新登入.            	
                RequestDispatcher dispatcher = request.getRequestDispatcher("/Login.jsp");
                dispatcher.forward(request,response);
            }            
        }
	}
	
	@Override
	public void destroy() { }
	
	
	@Override
	public void init(FilterConfig config) throws ServletException { }

}


