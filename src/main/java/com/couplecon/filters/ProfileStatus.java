package com.couplecon.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.util.DB;

/**
 * Servlet Filter implementation class ProfileStatus
 */
@WebFilter("/ProfileStatus")
public class ProfileStatus implements Filter {

    /**
     * Default constructor. 
     */
    public ProfileStatus() {
        // TODO Auto-generated constructor stub
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
		// Must run after authentication filter
		try {
			String partnerId = (String) request.getAttribute("partnerId");
			String coupleId = DB.getCoupleId(partnerId);
			String coupleVisibility = DB.getCoupleVisibility(coupleId);
			if (coupleVisibility.equals("y")) {
				chain.doFilter(request, response);
			} else {
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
				httpResponse.getWriter().write("{\"reason\":\"profile incomplete\"}");
			}
			// pass the request along the filter chain
		} catch (ServletException e) {
			throw e;
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
