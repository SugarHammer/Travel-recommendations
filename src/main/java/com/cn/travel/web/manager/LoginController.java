package com.cn.travel.web.manager;

import com.cn.travel.role.admin.entity.Admin;
import com.cn.travel.role.admin.service.imp.AdminService;
import com.cn.travel.utils.Tools;
import com.cn.travel.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController extends BaseController {

    @Autowired
    AdminService adminService;

    // 浏览器中输入“localhost:80/login”网址，会跳转到login.html登录页面
    @RequestMapping("/login")
    public String login(HttpServletRequest request){
        Object user = request.getSession().getAttribute("admin");
        if (user != null) {
            return REDIRECT+"/manager/index";
        }
        // 返回 login字符串，由于是layui，对应于login.html，也就是返回一个登录页面
        return "login";
    }


    /**
     * Session会话：关闭浏览器，该Session对象会清除掉。
     * 用户在登录页面中点击登录按钮，会调用该“loging”接口。
     *  用户第一次登录时，调用该loging接口，从数据库中查找用户名、密码，当存在时，将用户信息存入Session中；
     *  用户第二次调用loging接口，由于没有输入用户名、密码，故会重定向到“login”接口，然后从Session池中查询，
     *  显然可以找到该用户，故直接返回重定向页面【return REDIRECT+"/manager/index";】
     *   @RequestMapping("/loging") :此处为GET请求
     *   POST请求，需要传递请求体
     *
     *  用户跳转到其他页面时，如何保存用户的状态信息的，该项目是将用户信息存入Session。
     * @param userName
     * @param password
     * @param redirectAttributes
     * @param request
     * @return
     */
    @RequestMapping("/loging")
    public String loging(String userName,String password,RedirectAttributes redirectAttributes,HttpServletRequest request){
        if (Tools.isEmpty(userName)||Tools.isEmpty(password)){
            redirectAttributes.addFlashAttribute("message","用户名密码不得为空!");
            return REDIRECT+"/login";
        }
        try {
            Admin admin = adminService.login(userName, password);
            if (Tools.isEmpty(admin)){
                redirectAttributes.addFlashAttribute("message","用户名不存在或密码错误!");
                return REDIRECT+"/login";
            }else{
                if (admin.getState() == 1) {
                    // 将admin用户信息存入Session中
                    request.getSession().setAttribute("admin", admin);
                    System.out.println(request.getSession());
                   /* System.out.println("打印：request， request.getSession()， request.getSession().getAttribute"
                            + request +"\n" + request.getSession() + request.getSession().getAttribute("admin"));*/
                    return REDIRECT+"/manager/index";
                } else {
                    redirectAttributes.addFlashAttribute("message","账户已被停用!");
                    return REDIRECT+"/login";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return REDIRECT+"/login";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){
        Object user = request.getSession().getAttribute("admin");
        if (user != null) {
            request.getSession().removeAttribute("admin");
        }
        return "/login";
    }
}
