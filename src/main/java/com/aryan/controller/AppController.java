package com.aryan.controller;

import com.aryan.entity.UrlData;
import com.aryan.service.AppService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AppController {

    @Autowired
    private AppService appService;

    @Value("${server.port}")
    private String port;

    @ModelAttribute
    public void addServerPortToModel(Model model) {
        model.addAttribute("serverPort", port);
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        if (session.getAttribute("url") != null) {
            model.addAttribute("url", session.getAttribute("url"));
        }
        if (session.getAttribute("error") != null) {
            model.addAttribute("error", session.getAttribute("error"));
        }
        return "index";
    }


    @PostMapping("/url-post-process")
    public String processUrl(@RequestParam("url") String url, HttpSession session) {
        try {
            String shortUrl = appService.saveUrl(url);
            session.setAttribute("url", shortUrl);
            session.setAttribute("originalUrl", url);
        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/tiny/{uniqueCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable String uniqueCode) {
        try {
            UrlData data = appService.getOriginalUrl(uniqueCode);
            return new RedirectView(data.getOriginalUrl());
        } catch (Exception e) {
            return new RedirectView("/error/" + e.getMessage());
        }
    }

    private void clearSessionMessages(HttpSession session) {
        session.removeAttribute("url");
        session.removeAttribute("error");
        session.removeAttribute("originalUrl");
    }
}