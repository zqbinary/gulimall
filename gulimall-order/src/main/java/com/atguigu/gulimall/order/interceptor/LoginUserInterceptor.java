package com.atguigu.gulimall.order.interceptor;

import com.atguigu.common.vo.MemberResponseVo;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

import static com.atguigu.common.constant.AuthServerConstant.LOGIN_USER;

public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberResponseVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("xxxxxxxx");
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/order/order/status/**", uri);
        boolean match1 = antPathMatcher.match("/payed/notify", uri);
        //todo 这两个是啥，可以放行
        if (match1 || match) {
            return true;
        }

        //获取登录的用户信息
        HttpSession session = request.getSession();
        //把登录后用户的信息放在ThreadLocal里面进行保存
        MemberResponseVo attribute = (MemberResponseVo) session.getAttribute(LOGIN_USER);
        //未登录，返回登录页面
        if (attribute != null) {
            loginUser.set(attribute);
            return true;
        } else {
            //未登录，返回登录页面
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('请先进行登录，再进行后续操作！');location.href='http://auth.gulimall.com/login.html'</script>");
            // session.setAttribute("msg", "请先进行登录");
            // response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }

}
