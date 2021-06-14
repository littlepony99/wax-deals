package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CatalogueServlet extends HttpServlet {

    private final DiscogsService discogsService;
    private final UniqueVinylService uniqueVinylService;

    public CatalogueServlet(UniqueVinylService uniqueVinylService, DiscogsService discogsService) {
        this.uniqueVinylService = uniqueVinylService;
        this.discogsService = discogsService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String discogsUserName;
//        User user = null;
//        List<UniqueVinyl> randomUniqueVinyls = uniqueVinylService.findManyRandom(50);
//        List<UniqueVinyl> forShowing = new ArrayList<>();
//        List<UniqueVinyl> allUniqueVinyl = uniqueVinylService.findAll();
//        response.setContentType("text/html;charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//        Map<String, String> attributes = new HashMap<>();
//        HttpSession session = request.getSession(false);
//        String isWantListEmpty = request.getParameter("wantlist");
//        if (session != null && isWantListEmpty == null) {
//            user = (User) session.getAttribute("user");
//            if (user != null) {
//                attributes.put("userRole", user.getRole().toString());
//                discogsUserName = user.getDiscogsUserName();
//                forShowing = discogsService.getDiscogsMatchList(discogsUserName, allUniqueVinyl);
//            }
//        }
//        if (user != null) {
//            PageGenerator.getInstance().process("catalog", forShowing, attributes, response.getWriter());
//        } else {
//            PageGenerator.getInstance().process("catalog", randomUniqueVinyls, attributes, response.getWriter());
//        }
    }

}
