package com.techacademy.controller;

import java.util.List;

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
import com.techacademy.entity.Report;
import com.techacademy.entity.Employee.Role;
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
    

    // 日報一覧画面
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        Employee user = userDetail.getEmployee();
        if (user.getRole() == Role.ADMIN) {
         // ヒット件数をlistSizeという名前で登録
            model.addAttribute("listSize", reportService.findAll().size());
                // list.htmlの全○件の表示に使用
            // 全件検索結果をModelに登録
            model.addAttribute("reportList", reportService.findAll());
        } else {
            // model.addAttribute("listSize", user.getReportList().size());
            // model.addAttribute("reportList", user.getReportList());
            model.addAttribute("listSize", reportService.findByEmployee(user).size());
            model.addAttribute("reportList", reportService.findByEmployee(user));
        
        }
        
        
        

        return "reports/list";
    }
    
    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable String id, Model model) {

        model.addAttribute("report", reportService.findByCode(id));
        return "reports/detail";
    }
    
    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        Employee employee = userDetail.getEmployee();
        report.setEmployee(employee);
        model.addAttribute("report", report);
        return "reports/new";
    }
    
    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@AuthenticationPrincipal UserDetail userDetail, @Validated Report report, BindingResult res, Model model) {
        
        /*
        // パスワード空白チェック
        /*
         * エンティティ側の入力チェックでも実装は行えるが、更新の方でパスワードが空白でもチェックエラーを出さずに
         * 更新出来る仕様となっているため上記を考慮した場合に別でエラーメッセージを出す方法が簡単だと判断
         
        if ("".equals(employee.getPassword())) {
            // パスワードが空白だった場合
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.BLANK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.BLANK_ERROR));

            return create(employee);
        }
        */
        
        // 入力チェック
        if (res.hasErrors()) {
            return create(report, userDetail, model);
        }
        
        
        // 業務チェック
        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        /*
        try {
            // 例外が発生する処理
            ErrorKinds result = reportService.save(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(report, userDetail, model);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(report, null, model);
        }
        
        Employee employee = userDetail.getEmployee();
        report.setEmployee(employee);
        ErrorKinds result = reportService.save(report);
        */
        Employee employee = userDetail.getEmployee();
        report.setEmployee(employee);
        // 業務チェック：すでに同じ日付の日報が存在したらエラーにする
        List<Report> reportList = reportService.findByEmployee(employee);
        for (int i = 0; i < reportList.size(); i++) {
            Report reportCheck = reportList.get(i);
            if (report.getReportDate().equals(reportCheck.getReportDate() ) ) { // 一致する日付けがあればTrue
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                        ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));
                return create(report, userDetail, model);
            }
        }
        
        ErrorKinds result = reportService.save(report);
        
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report, userDetail, model);
        }
        

        return "redirect:/reports";
    }
    
    
    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    public String edit(Report report, @AuthenticationPrincipal UserDetail userDetail, @PathVariable String id, Model model) {
        if(id != null) {
            // idがnullでない＝通常の遷移なのでDB値をModelに登録
            Employee employee = userDetail.getEmployee();
            report.setEmployee(employee);
            model.addAttribute("report", reportService.findByCode(id));
        } else {
            // idがnull＝更新処理でエラーになって戻ってきた場合なので、
            // 元の値を表示
            model.addAttribute("report", report);
        }
        
        return "reports/update";
    }

    
    
    // 日報更新処理
    @PostMapping(value = "/{id}/update")
    public String update(@AuthenticationPrincipal UserDetail userDetail, @Validated Report report, BindingResult res, Model model) {
        // ValidatedアノテーションでEmployeeエンティティに基づく入力チェック
        
        Employee employee = userDetail.getEmployee();
        report.setEmployee(employee);
        
        if(res.hasErrors()) {
            // エラーありの場合
            return edit(report, userDetail, null, model);
            // idがnullになるがeditのelseの処理で元の値を表示
        }

        
        // 業務チェック：すでに同じ日付の日報が存在したらエラーにする
        List<Report> reportList = reportService.findByEmployee(employee);
        for (int i = 0; i < reportList.size(); i++) {
            Report reportCheck = reportList.get(i);
            if (report.getId().equals(reportCheck.getId())){
                continue;
            } else {
                if(report.getReportDate().equals(reportCheck.getReportDate() ) ) { // 一致する日付けがあればTrue
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                        ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));
                return edit(report, userDetail, null, model);
             // idがnullになるがeditのelseの処理で元の値を表示
                }
            }   
        }
        // ErrorKinds result = reportService.update(report);
        
        
        /*
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return edit(employee, null, model);
        }
        */
            
        // 入力結果の登録
        reportService.update(report);
        // 一覧画面にリダイレクト
        return "redirect:/reports";
        }  
    
    
    
    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    // public String delete(@PathVariable String id, @AuthenticationPrincipal UserDetail userDetail, Model model) {
    public String delete(@PathVariable String id, Model model) {
        
        // ErrorKinds result = reportService.delete(id, userDetail);
        ErrorKinds result = reportService.delete(id);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findByCode(id));
            return detail(id, model);
        }

        return "redirect:/reports";
    }

 
 
}
