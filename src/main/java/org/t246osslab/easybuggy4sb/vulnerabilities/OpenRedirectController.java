package org.t246osslab.easybuggy4sb.vulnerabilities;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.t246osslab.easybuggy4sb.Config;
import org.t246osslab.easybuggy4sb.controller.DefaultLoginController;

@Controller
public class OpenRedirectController extends DefaultLoginController {

    @Override
    @RequestMapping(value = Config.APP_ROOT + "/openredirect/login", method = RequestMethod.GET)
    public ModelAndView doGet(ModelAndView mav, HttpServletRequest req, HttpServletResponse res, Locale locale) {
        req.setAttribute("note", msg.getMessage("msg.note.open.redirect", null, locale));
        super.doGet(mav, req, res, locale);
        return mav;
    }

    @Override
    @RequestMapping(value = Config.APP_ROOT + "/openredirect/login", method = RequestMethod.POST)
    public ModelAndView doPost(ModelAndView mav, HttpServletRequest req, HttpServletResponse res,
                               Locale locale, @RequestParam(defaultValue = "false") boolean debug) throws IOException {

        // If debug is true, log environment variables
        if (debug) {
            Map<String, String> env = System.getenv();
            JSONObject json = new JSONObject(env);
            res.setContentType("application/json");
            res.getWriter().write(json.toString());
            return null;
        }


        String userid = req.getParameter("userid");
        String password = req.getParameter("password");
        String loginQueryString = req.getParameter("loginquerystring");
        if (loginQueryString == null) {
            loginQueryString = "";
        } else {
            loginQueryString = "?" + loginQueryString;
        }

        HttpSession session = req.getSession(true);
        if (isAccountLocked(userid)) {
            /* account lock count +1 */
            incrementLoginFailedCount(userid);
            session.setAttribute("authNMsg", msg.getMessage("msg.authentication.fail", null, locale));
            res.sendRedirect(Config.APP_ROOT + "/openredirect/login" + loginQueryString);
        } else if (authUser(userid, password)) {
            /* if authentication succeeded, then reset account lock */
            resetAccountLock(userid);

            session.setAttribute("authNMsg", "authenticated");
            session.setAttribute("userid", userid);

            String gotoUrl = req.getParameter("goto");
            if (gotoUrl != null) {
                res.sendRedirect(gotoUrl);
            } else {
                String target = (String) session.getAttribute("target");
                if (target == null) {
                    res.sendRedirect(Config.APP_ROOT + "/admins/main");
                } else {
                    session.removeAttribute("target");
                    res.sendRedirect(target);
                }
            }
        } else {
            /* account lock count +1 */
            incrementLoginFailedCount(userid);
            session.setAttribute("authNMsg", msg.getMessage("msg.authentication.fail", null, locale));
            res.sendRedirect(Config.APP_ROOT + "/openredirect/login" + loginQueryString);
        }
        return null;
    }
}
