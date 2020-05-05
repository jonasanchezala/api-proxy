package com.ujaveriana.modyval.apiproxy.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Component
public class PrePaymentFilter extends ZuulFilter {

    private static final String CONTENT_TYPE_HEADER = "Content-type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CHARSET_NAME = "UTF-8";
    private static final String POST_METHOD = "POST";

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER;
    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        try {
            InputStream in = request.getInputStream();


            if(request.getMethod().equals(POST_METHOD) &&
                    !request.getHeader(CONTENT_TYPE_HEADER).equals(CONTENT_TYPE_JSON)) {

                String body = StreamUtils.copyToString(in, Charset.forName(CHARSET_NAME));
                JSONObject xmlJSONObj = XML.toJSONObject(body);
                String jsonPrettyPrintString = xmlJSONObj.getJSONObject("root").toString(4);
                System.out.println(jsonPrettyPrintString);
                body = jsonPrettyPrintString;


                context.addZuulRequestHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON);
                byte[] bytes = body.getBytes(CHARSET_NAME);
                context.setRequest(getRequest(request, bytes));
            }

            System.out.println("Request Method : " + request.getMethod() + " Request URL : "
                    + request.getRequestURL().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpServletRequestWrapper getRequest(HttpServletRequest request, byte[] bytes) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public ServletInputStream getInputStream() throws IOException {
                return new ServletInputStreamWrapper(bytes);
            }

            @Override
            public int getContentLength() {
                return bytes.length;
            }

            @Override
            public long getContentLengthLong() {
                return bytes.length;
            }
        };
    }
}
