package com.jedijava.mybatis.hat.test.utils;

import com.jedijava.mybatis.hat.utils.HatStringUtil;
import org.junit.jupiter.api.Test;

/**
 * @author liukaiyang
 * @since 2019/9/18 20:48
 */
public class StringTest {

    @Test
    public void testUnderline(){
        System.out.println(HatStringUtil.toUnderline("UserMapperObject"));
        System.out.println(HatStringUtil.toUnderline("userMapperObject"));
    }
    @Test
    public void testCamel(){
        System.out.println(HatStringUtil.toCamel("UserMapperObject"));
        System.out.println(HatStringUtil.toCamel("_user_test_"));
        System.out.println(HatStringUtil.toCamel("user_test_name"));
    }

}
