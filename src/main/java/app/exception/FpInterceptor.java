package app.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
public class FpInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RequestContext requestContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("before Request");
        requestContext.start();
        /*HttpServletRequest requestCacheWrapperObject = new ContentCachingRequestWrapper(request);
        try {
            requestContext.setRequestString(httpServletRequestToString(requestCacheWrapperObject));
            System.out.println("Request String"+requestContext.getRequestString());
        } catch (Exception e){
            requestContext.setRequestString("Error");
        }*/
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        System.out.println("after Request");
        requestContext.end();
        requestContext.save();
    }
    /*
    private String httpServletRequestToString(HttpServletRequest request) throws Exception {
        ServletInputStream inputStream = request.getInputStream();
        Scanner sc = new Scanner(inputStream);
        StringBuffer sb = new StringBuffer();
        while(sc.hasNext()){
            sb.append(sc.nextLine());
        }
        return sb.toString();
    }*/
}
