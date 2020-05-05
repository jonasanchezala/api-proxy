package com.ujaveriana.modyval.apiproxy.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

@Component
public class PostPaymentFilter extends ZuulFilter {

    private static final String POST_METHOD = "POST";
    private static final String CONTENT_TYPE_HEADER = "Content-type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CONTENT_TYPE_XML = "application/xml";
    private static final String CHARSET_NAME = "UTF-8";

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 999;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();

        try {
            HttpServletRequest request = context.getRequest();
            InputStream stream = context.getResponseDataStream();
            String body = StreamUtils.copyToString(stream, Charset.forName(CHARSET_NAME));

            if(request.getMethod().equals(POST_METHOD) &&
                    !request.getHeader(CONTENT_TYPE_HEADER).equals(CONTENT_TYPE_JSON)){
                JSONObject rootObject= new JSONObject();
                rootObject.put("root", new JSONObject(body));
                String xml = XML.toString(rootObject);
                body = xml;
                context.addZuulResponseHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_XML);
            }

            context.setResponseBody(body);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
