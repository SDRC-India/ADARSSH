package org.sdrc.rmnchaapi.filters;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * @author Sarita Panigrahi
 * Created on: 02-02-2019
 * This filter is used to pass access token to downstream services
 *
 */
public class AuthHeaderFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
//		final RequestContext ctx = RequestContext.getCurrentContext();
//		final HttpServletRequest request = ctx.getRequest();
//		// Here is the authorization header being read.
//		final String xAuth = request.getHeader("Authorization");
//		
//		// Use the below method to add anything to the request header to read
//		// downstream. if needed.
//		if (xAuth != null) {
//			ctx.addZuulRequestHeader("Authorization", xAuth);
//		}else {
//			@SuppressWarnings("unchecked") Set<String> ignoredHeaders = (Set<String>) ctx.get("ignoredHeaders");
//	        ignoredHeaders.remove("authorization");
//		}
		
		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

}
