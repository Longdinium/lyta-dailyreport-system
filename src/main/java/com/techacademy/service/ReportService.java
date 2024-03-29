package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    // private final PasswordEncoder passwordEncoder;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
        // this.passwordEncoder = passwordEncoder;
    }
    
        
    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {
        // 過去に同じ日付で登録された日報がないかのチェック
        //reportRepository.findById( report.getEmployee() );
        //findByCode( report.getEmployee() );
        
        /*
        // パスワードチェック
        ErrorKinds result = employeePasswordCheck(employee);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }
        

        // 日付重複チェック
        if (report.getReportDate() != null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }
        */
        

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }
    
    // 日報更新処理
    @Transactional
    public ErrorKinds update(Report report) {
        Report original = findByCode( String.valueOf( report.getId() ) );
        
        /*
        if (!"".equals(employee.getPassword())) {
            // パスワードが空白でない場合
            // PWチェックの結果をresultに渡す
            ErrorKinds result = employeePasswordCheck(employee);
            if (ErrorKinds.CHECK_OK != result) {
                return result;
            }
        } else {
            // パスワードが空白の場合、チェックはせず元のパスワードを渡す
            // 1. まずオリジナルのパスワードをとってくる
            // 2. オリジナルのパスワードの値をresultに渡す
            employee.setPassword(original.getPassword());
        }
        */
        report.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        /*
        employee.setCreatedAt(original.getCreatedAt());
        employee.setUpdatedAt(now);
         */
        report.setCreatedAt(original.getCreatedAt() );
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 従業員削除
    @Transactional
    // public ErrorKinds delete(String id, UserDetail userDetail) {
    public ErrorKinds delete(String id) {
        
        /*
        // 自分を削除しようとした場合はエラーメッセージを表示
        if (code.equals(userDetail.getEmployee().getCode())) {
            return ErrorKinds.LOGINCHECK_ERROR;
        }
        */
        Report report = findByCode(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }
    

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }
    
    
    // 1件を検索
    public Report findByCode(String id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }
    
    // 従業員に紐づく日報情報を検索しリストで取得
    public List<Report> findByEmployee(Employee employee){
        List<Report> list = reportRepository.findByEmployee(employee);
        return list;
    }
    
    /*
    // 従業員パスワードチェック
    private ErrorKinds employeePasswordCheck(Employee employee) {

        // 従業員パスワードの半角英数字チェック処理
        if (isHalfSizeCheckError(employee)) {

            return ErrorKinds.HALFSIZE_ERROR;
        }

        // 従業員パスワードの8文字～16文字チェック処理
        if (isOutOfRangePassword(employee)) {

            return ErrorKinds.RANGECHECK_ERROR;
        }

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        return ErrorKinds.CHECK_OK;
    }

    // 従業員パスワードの半角英数字チェック処理
    private boolean isHalfSizeCheckError(Employee employee) {

        // 半角英数字チェック
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(employee.getPassword());
        return !matcher.matches();
    }

    // 従業員パスワードの8文字～16文字チェック処理
    public boolean isOutOfRangePassword(Employee employee) {

        // 桁数チェック
        int passwordLength = employee.getPassword().length();
        return passwordLength < 8 || 16 < passwordLength;
    }
    */

}
