package tech.aiflowy.common.web.controller;

import com.mybatisflex.core.query.QueryWrapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OperatorBuilder {

    public static final String operatorSuffix = "__op";
    public static final Set<String> supportedOperators = new HashSet<>(Arrays.asList(
            "eq", "ne", "gt", "ge", "lt", "le",
            "like", "likeLeft", "likeRight", "notLike", "notLikeLeft", "notLikeRight",
            "between", "notBetween", "in", "notIn"
            , "isNull", "isNotNull"));


    public static void buildOperator(QueryWrapper queryWrapper, String columnName, String op, String[] values) {
        if (!supportedOperators.contains(op)) {
            return;
        }
        switch (op) {
            case "eq":
                queryWrapper.eq(columnName, values[0]);
                break;
            case "ne":
                queryWrapper.ne(columnName, values[0]);
                break;
            case "gt":
                queryWrapper.gt(columnName, values[0]);
                break;
            case "ge":
                queryWrapper.ge(columnName, values[0]);
                break;
            case "lt":
                queryWrapper.lt(columnName, values[0]);
                break;
            case "le":
                queryWrapper.le(columnName, values[0]);
                break;
            case "like":
                queryWrapper.like(columnName, values[0]);
                break;
            case "likeLeft":
                queryWrapper.likeLeft(columnName, values[0]);
                break;
            case "likeRight":
                queryWrapper.likeRight(columnName, values[0]);
            case "notLike":
                queryWrapper.notLike(columnName, values[0]);
                break;
            case "notLikeLeft":
                queryWrapper.notLikeLeft(columnName, values[0]);
                break;
            case "notLikeRight":
                queryWrapper.notLikeRight(columnName, values[0]);
                break;
            case "between":
                queryWrapper.between(columnName, values[0], values[1]);
                break;
            case "notBetween":
                queryWrapper.notBetween(columnName, values[0], values[1]);
                break;
            case "in":
                queryWrapper.in(columnName, values);
                break;
            case "notIn":
                queryWrapper.notIn(columnName, values);
                break;
            case "isNull":
                queryWrapper.isNull(columnName);
                break;
            case "isNotNull":
                queryWrapper.isNotNull(columnName);
                break;
        }
    }


}
