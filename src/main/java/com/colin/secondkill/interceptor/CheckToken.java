package com.colin.secondkill.interceptor;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson2.JSONObject;
import com.colin.secondkill.annotation.LoginStatus;
import com.colin.secondkill.bean.User;
import com.colin.secondkill.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 2024年07月04日上午10:27
 */
@Component
public class CheckToken implements HandlerInterceptor {
    @Autowired
    private JedisPool jedisPool;

    private String longTokenId;
    /**
     * Intercept the execution of a handler. Called after HandlerMapping determined
     * an appropriate handler object, but before HandlerAdapter invokes the handler.
     * <p>DispatcherServlet processes a handler in an execution chain, consisting
     * of any number of interceptors, with the handler itself at the end.
     * With this method, each interceptor can decide to abort the execution chain,
     * typically sending an HTTP error or writing a custom response.
     * <p><strong>Note:</strong> special considerations apply for asynchronous
     * request processing. For more details see
     * {@link AsyncHandlerInterceptor}.
     * <p>The default implementation returns {@code true}.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself. Else, DispatcherServlet assumes
     * that this interceptor has already dealt with the response itself.
     * @throws Exception in case of errors
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setCharacterEncoding("utf8");
        response.setContentType("text/html;charset=utf8");

        //当前拦截器拦截的是所有请求，包括静态资源和接口方法
        HandlerMethod handlerMethod = null;
        //这次请求的是静态资源，不需要拦截，直接返回true
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //这次请求的是接口方法，有LoginStatus注解的需要登录，没有直接放行
        handlerMethod = (HandlerMethod) handler;
        LoginStatus loginStatus = handlerMethod.getMethodAnnotation(LoginStatus.class);
        if (loginStatus == null){
            return true;
        }
        String shortToken = null;
        String longToken = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if ("longToken".equals(cookie.getName())){
                longToken = cookie.getValue();
            }
            if ("shortToken".equals(cookie.getName())){
                shortToken = cookie.getValue();
            }
        }
        if (longToken == null){
            request.getRequestDispatcher("/toLogin").forward(request, response);
            return false;
        }
        //校验长token是否合法
        if ((this.longTokenId = TokenUtil.checkLongToken(longToken, jedisPool)) == null){
            request.getRequestDispatcher("/toLogin").forward(request, response);
            return false;
        }
        MD5 md5 = MD5.create();
        Jedis resource = jedisPool.getResource();
        String jsonUser = resource.get(this.longTokenId);
        User user = JSONObject.parseObject(jsonUser, User.class);
        //如果短token不存在，依赖于长token决定 重新登录 还是 无感刷新
        if (shortToken == null){
            //如果长token合法，实现短token的 无感刷新
            shortToken = TokenUtil.getShortToken(user.getId());
            Cookie cookie = new Cookie("shortToken", shortToken);
            cookie.setMaxAge(20 * 60);
            response.addCookie(cookie);
            resource.close();
            return true;
        }
        Integer status = TokenUtil.checkShortToken(shortToken);
        if (TokenUtil.OVER_TIME.equals(status)) {
            shortToken = TokenUtil.getShortToken(user.getId());
            Cookie cookie = new Cookie("shortToken", shortToken);
            cookie.setMaxAge(365 * 24 * 60 * 60);
            cookie.setDomain("localhost");
            cookie.setPath("/");
            response.addCookie(cookie);
            return true;
        }

        if (TokenUtil.ILLEGALITY.equals(status)) {
            request.getRequestDispatcher("/toLogin.html").forward(request, response);
            return false;
        }
        return true;
    }



}
