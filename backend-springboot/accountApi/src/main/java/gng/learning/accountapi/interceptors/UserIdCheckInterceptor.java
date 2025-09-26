package gng.learning.accountapi.interceptors;

import gng.learning.accountapi.controllers.AccountController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;
import java.util.logging.Logger;


@Component
public class UserIdCheckInterceptor  implements HandlerInterceptor {

    Logger logger = Logger.getLogger(UserIdCheckInterceptor.class.getName());

    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {

        String userIdHeader = req.getHeader("X-User-Id");
        String path = req.getRequestURI();
        //String queryString = req.getQueryString();

        logger.info("Intercepting request to path: " + path);
        //logger.info("Query String = : " + queryString);


        // Define paths to exclude
        if (path.startsWith("/incoming-transaction")) {
            logger.info("Excluding path from interceptor: " + path);
            return true;
        }

        if (userIdHeader == null || userIdHeader.isEmpty()) {

            logger.info("Missing X-User-Id header for path: " + path);

            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Missing X-User-Id header");
            return false;
        }

        try {

            UUID.fromString(userIdHeader);

        } catch (IllegalArgumentException e) {

            logger.info("Invalid X-User-Id header for path: " + path);

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid X-User-Id format");
            return false;
        }

        logger.info("X-User-Id header is valid: " + userIdHeader + " for path: " + path);

        return true;
    }


}
