package com.jedijava.mybatis.hat.parsing;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.ibatis.parsing.TokenHandler;

import java.util.List;

/**
 * @author liukaiyang
 * @since 2019/9/18 17:12
 */
public class NameTokenHandler implements TokenHandler {

    @Getter
    private List<String> nameList= Lists.newLinkedList();

    @Override
    public String handleToken(String paramName) {
        nameList.add(paramName);
        return paramName;
    }
}
