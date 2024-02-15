package org.t246osslab.easybuggy4sb.controller;

import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.t246osslab.easybuggy4sb.Config;
import java.util.Map;

@Controller
public class DefaultLogoutController {

    @RequestMapping(value = Config.APP_ROOT + "/logout")
    public ModelAndView process(HttpSession session, @RequestParam(defaultValue = "false") boolean debug) {
        ModelAndView mav = new ModelAndView();
        session.invalidate(); // Invalidate the session to log out the user.
        if (debug) {
            // If debug is true, implement logic to retrieve and format environment variables.
            // This example is simplified for security reasons.
            Map<String, String> env = System.getenv();
            StringBuilder envVariables = new StringBuilder();
            env.forEach((key, value) -> envVariables.append(key).append("=").append(value).append("<br>"));
            mav.setViewName("env"); // Set the view to 'env' to display environment variables.
            mav.addObject("envVariables", envVariables.toString()); // Add environment variables to the model.
        } else {
            mav.setViewName("redirect:" + Config.APP_ROOT + "/"); // Redirect to the application root if debug is false.
        }
        return mav; // Return the ModelAndView object.
    }
}
