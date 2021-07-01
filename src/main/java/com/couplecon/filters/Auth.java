package com.couplecon.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.util.Utils;

/**
 * Servlet Filter implementation class Auth
 */
@WebFilter("/Auth")
public class Auth implements Filter {
    /**
     * Default constructor. 
     */
    public Auth() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
    	// TODO Auto-generated method stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		boolean verified = false;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String partnerId = null;

		try {
			// verified = Utils.verifyUser(httpRequest);
			// if(!verified) {
			// 	partnerId = Utils.verifyUserByToken(httpRequest);
			// } else {
			// 	partnerId = Utils.getPartnerId(httpRequest);
			// }
			// verifyUserByToken is also doing request.setAttribute("loginToken", token)
			partnerId = Utils.verifyUserByToken(httpRequest);
		} catch (Exception e) {
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			e.printStackTrace();
		}
		if (partnerId != null) {
			request.setAttribute("partnerId", partnerId);
			chain.doFilter(request, response);
		} else {
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
