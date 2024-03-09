
package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Entity
@Table(name = "reports")
@SQLRestriction("delete_flg = false")
public class Report {


    // ID
    @Id
    // @Column(length = 10)
    @NotEmpty
    @Length(max = 10)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // 日付
    @Column(nullable = false)
    @NotEmpty
    private LocalDate report_date;

    // タイトル
    @Column(length = 100, nullable = false)
    @NotEmpty
    @Length(max = 100) // エラーチェック 100文字超過の場合
    private String title;
    
    // 内容
    @Column(nullable = false, columnDefinition="LONGTEXT")
    @NotEmpty
    @Length(max = 600) // エラーチェック 600文字超過の場合
    private String content;
    
    // 社員番号
    @Column(length = 10)
    @NotEmpty
    @Length(max = 10)
    private String employee_code;
    
    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;
    
    // 削除フラグ(論理削除を行うため)
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    // 登録日時
    @Column(nullable = false)
    private LocalDateTime created_at;

    // 更新日時
    @Column(nullable = false)
    private LocalDateTime updated_at;
    
    

    

    
}