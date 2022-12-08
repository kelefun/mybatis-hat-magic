package com.jedijava.mybatis.hat.test.parsing;

import com.jedijava.mybatis.hat.parsing.NameTokenHandler;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.junit.jupiter.api.Test;

/**
 * @author liukaiyang
 * @since 2019/9/18 17:14
 */
public class TokenHandlerTest {
    @Test
    public void testVar() {
        String sql = "name=#{name_hat} and mobile=#{what}";
        NameTokenHandler tokenHandler = new NameTokenHandler();
        GenericTokenParser parser = new GenericTokenParser("#{", "}", tokenHandler);
        parser.parse(sql);
        System.out.println(tokenHandler.getNameList());
    }

    @Test
    public void testColumn() {
        String sql = "@[name]=#{name_hat} and @[mobile_hat]=#{what}";
        NameTokenHandler tokenHandler = new NameTokenHandler();
        GenericTokenParser parser = new GenericTokenParser("@[", "]", tokenHandler);
        String result = parser.parse(sql);
        System.out.println(result);
        System.out.println(tokenHandler.getNameList());
    }
}
