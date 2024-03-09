
package com.techacademy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TopController {

    // ログイン画面表示
    @GetMapping(value = "/login")
    public String login() {
        return "login/login";
    }

    // ログイン後のトップページ表示
    @GetMapping(value = "/")
    public String top(Model model) {
        // return "redirect:/employees";
            // 修正前は従業員一覧画面にリダイレクト
        return "redirect:/reports";
            // 修正後は日報一覧画面にリダイレクト
    }

}
