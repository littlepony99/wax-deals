package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ShopServlet extends HttpServlet {

    private ShopService shopService;

    public ShopServlet(ShopService shopService) {
        this.shopService = shopService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        var shopList = shopService.findAll();
        log.info("Shops list is prepared to be included in response, size { 'Shops List size':}", shopList.size());
        Map<String, String> attributes = new HashMap<>();
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
                attributes.put("email", user.getEmail());
                attributes.put("discogsUserName", user.getDiscogsUserName());
            }
        }
        PageGenerator.getInstance().processStores("stores", shopList, attributes, response.getWriter());
    }

}
