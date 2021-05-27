package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SearchResultsServlet extends HttpServlet {

    private final UniqueVinylService vinylService;

    public SearchResultsServlet(UniqueVinylService vinylService) {
        this.vinylService = vinylService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> attributes = new HashMap<>();
        String matcher = request.getParameter("matcher");
        List<UniqueVinyl> filteredUniqueVinyls = vinylService.findManyFiltered(matcher);
        attributes.put("searchWord", matcher);
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
            }
        }
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PageGenerator.getInstance().process("search", filteredUniqueVinyls, attributes, response.getWriter());
    }

}
