package com.jedijava.mybatis.hat.spring.test.prepare.obj;

import com.jedijava.mybatis.hat.annotation.HatTable;
import com.jedijava.mybatis.hat.face.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author liukaiyang
 * @since 2019/9/4 19:15
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@HatTable("unit_test_table")
public class TestEntity extends BaseEntity {
    private String username;
    private String accountType;
}
