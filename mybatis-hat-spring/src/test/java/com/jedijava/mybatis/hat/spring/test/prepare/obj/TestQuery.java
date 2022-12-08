package com.jedijava.mybatis.hat.spring.test.prepare.obj;

import com.jedijava.mybatis.hat.face.entity.PageQuery;
import lombok.Data;

/**
 * @author liukaiyang
 * @since 2019/9/4 19:15
 */
@Data
public class TestQuery extends PageQuery {
    private String username;
}
