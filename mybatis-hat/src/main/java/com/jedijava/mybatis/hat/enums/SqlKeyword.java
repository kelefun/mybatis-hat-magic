package com.jedijava.mybatis.hat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author liukaiyang
 * @since 2019/9/10 17:16
 */
@Getter
@AllArgsConstructor
public enum SqlKeyword {
    SELECT("SELECT"),
    UPDATE("UPDATE"),
    SET("SET"),
    DELETE("DELETE"),
    INSERT("INSERT INTO"),
    VALUES("VALUES"),
    FROM("FROM"),
    WHERE("WHERE"),
    AND("AND"),
    OR("OR"),
    IN("IN"),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    EQUAL("="),
    NOT_EQUAL("<>"),
    GREATER_THAN(">"),
    GREATER_EQUAL(">="),
    LESS_THAN("<"),
    LESS_EQUAL("<="),
    IS_NULL("IS NULL"),
    NOT_NULL("IS NOT NULL"),
    GROUP("GROUP BY"),
    HAVING("HAVING"),
    ORDER("ORDER BY"),
    BETWEEN("BETWEEN"),
    ASC("ASC"),
    DESC("DESC");

    private String val;
}
