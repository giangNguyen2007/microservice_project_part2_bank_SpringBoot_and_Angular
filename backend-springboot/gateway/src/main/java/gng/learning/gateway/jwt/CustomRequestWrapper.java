package gng.learning.gateway.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

public class CustomRequestWrapper extends HttpServletRequestWrapper {

    public static final String USER_ID_HEADER = "X-User-Id";

    private final String userId;
    public CustomRequestWrapper(HttpServletRequest request, String userId1) {
        super(request);
        this.userId = userId1;
    }


    @Override
    public String getHeader(String name) {
        if (USER_ID_HEADER.equalsIgnoreCase(name)) return userId;
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (USER_ID_HEADER.equalsIgnoreCase(name)) {
            return Collections.enumeration(List.of(userId));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new HashSet<>();
        Enumeration<String> e = super.getHeaderNames();
        while (e.hasMoreElements()) names.add(e.nextElement());
        names.add(USER_ID_HEADER);
        return Collections.enumeration(names);
    }




}
