package com.vinylteam.vinyl.web.templater;

import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.web.dto.OneVinylOffersServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageGenerator {

    private static PageGenerator pageGenerator;
    private TemplateEngine templateEngine;

    public static PageGenerator getInstance() {
        if (pageGenerator == null) {
            pageGenerator = new PageGenerator();
        }
        return pageGenerator;
    }

    private PageGenerator() {
        templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateEngine.setTemplateResolver(templateResolver);
    }

    public void process(String fileName, Writer writer) {
        process(fileName, new ArrayList<>(), new ArrayList<>(), new HashMap<>(), writer);
    }

    public void process(String fileName, Map<String, String> attributes, Writer writer) {
        process(fileName, new ArrayList<>(), new ArrayList<>(), attributes, writer);
    }

    public void process(String fileName, List<UniqueVinyl> list, Writer writer) {
        process(fileName, list, new ArrayList<>(), new HashMap<>(), writer);
    }

    public void process(String fileName, List<UniqueVinyl> list, List<OneVinylOffersServletResponse> vinylOffersList, Writer writer) {
        process(fileName, list, vinylOffersList, new HashMap<>(), writer);
    }

    public void process(String fileName, List<UniqueVinyl> list, Map<String, String> attributes, Writer writer) {
        process(fileName, list, new ArrayList<>(), attributes, writer);
    }

    public void processStores(String fileName, List<Shop> list, Map<String, String> attributes, Writer writer) {
        Context storesContext = new Context();
        prepareUserContext(attributes, storesContext);
        storesContext.setVariable("shopList", list);
        templateEngine.process(fileName, storesContext, writer);
    }

    public void process(String fileName, List<UniqueVinyl> vinylList, List<OneVinylOffersServletResponse> vinylOffersList, Map<String, String> attributes, Writer writer) {
        Context context = getContext(vinylList, vinylOffersList, attributes);
        templateEngine.process(fileName, context, writer);
    }

    private Context getContext(List<UniqueVinyl> vinylList, List<OneVinylOffersServletResponse> vinylOffersList, Map<String, String> attributes) {
        Context context = new Context();
        List<UniqueVinyl> firstUniqueVinylRow = new ArrayList<>();
        List<UniqueVinyl> otherUniqueVinylRow = new ArrayList<>();
        List<UniqueVinyl> uniqueVinylsByArtist = new ArrayList<>();

        String searchWord = attributes.get("searchWord");
        if (searchWord != null) {
            context.setVariable("matcher", searchWord);
        }

        prepareUserContext(attributes, context);

        String message = attributes.get("message");
        if (message != null) {
            context.setVariable("message", message);
        }

        String token = attributes.get("token");
        if (token != null) {
            context.setVariable("token", token);
        }

// for catalog page

        context.setVariable("vinylList", vinylList);

// for search & one vinyl with offers pages

        if (!vinylList.isEmpty()) {
            context.setVariable("firstVinyl", vinylList.get(0));
        }

// for search page

        if (vinylList.size() > 1) {
            if (vinylList.size() >= 7) {
                for (int i = 1; i < 7; i++) {
                    firstUniqueVinylRow.add(vinylList.get(i));
                }
            } else {
                for (int i = 1; i < vinylList.size(); i++) {
                    firstUniqueVinylRow.add(vinylList.get(i));
                }
            }
            context.setVariable("firstVinylRow", firstUniqueVinylRow);
        }
        if (vinylList.size() > 7) {
            for (int i = 7; i < vinylList.size(); i++) {
                otherUniqueVinylRow.add(vinylList.get(i));
            }
            context.setVariable("otherVinylRow", otherUniqueVinylRow);
        }

// for one vinyl with offers page

        if (vinylList.size() > 1) {
            for (int i = 1; i < vinylList.size(); i++) {
                uniqueVinylsByArtist.add(vinylList.get(i));
            }
            context.setVariable("vinylsByArtist", uniqueVinylsByArtist);
        }

        context.setVariable("vinylOffersList", vinylOffersList);

        return context;
    }

    void prepareUserContext(Map<String, String> attributes, Context context) {
        String userRole = attributes.get("userRole");
        if (userRole != null) {
            context.setVariable("userRole", userRole);
        }

        String email = attributes.get("email");
        if (email != null) {
            context.setVariable("email", email);
        }

        String discogsUserName = attributes.get("discogsUserName");
        if (discogsUserName != null) {
            context.setVariable("discogsUserName", discogsUserName);
        }

        String discogsLink = attributes.get("discogsLink");
        if (discogsLink != null) {
            if (!discogsLink.equals("")) {
                context.setVariable("discogsLink", discogsLink);
            }
        }
    }

}
