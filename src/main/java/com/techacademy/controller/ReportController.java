package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Employee;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {
   
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    

    // 従業員一覧画面
    @GetMapping
    public String list(Model model) {
        // ヒット件数をlistSizeという名前で登録
        model.addAttribute("listSize", reportService.findAll().size());
            // list.htmlの全○件の表示に使用
        // 全件検索結果をModelに登録
        model.addAttribute("reportList", reportService.findAll());

        return "reports/list";
    }
    
    // 従業員詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable String id, Model model) {

        model.addAttribute("report", reportService.findByCode(id));
        return "reports/detail";
    }
 
 
}
