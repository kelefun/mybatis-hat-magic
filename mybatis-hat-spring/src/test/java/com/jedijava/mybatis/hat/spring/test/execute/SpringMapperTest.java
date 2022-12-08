package com.jedijava.mybatis.hat.spring.test.execute;

import com.google.common.collect.Maps;
import com.jedijava.mybatis.hat.spring.test.prepare.SpringHsqlBaseTest;
import com.jedijava.mybatis.hat.spring.test.prepare.dao.TestMapper;
import com.jedijava.mybatis.hat.spring.test.prepare.obj.TestEntity;
import com.jedijava.mybatis.hat.spring.test.prepare.obj.TestQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author liukaiyang
 * @since 2019/9/4 19:40
 */
public class SpringMapperTest extends SpringHsqlBaseTest {

    @Autowired
    TestMapper testMapper;

    private String getRandom() {
        return "name" + (Math.random() + "").substring(1, 6);
    }

    @Test
    public void testInsert() {
        TestEntity userInfo = new TestEntity();
        userInfo.setUsername("测试新增用户");

        int effect = testMapper.insert(userInfo);
        System.out.println("新增条数" + effect);
        System.out.println(userInfo);
        assertEquals(effect, 1);
        assertNotNull(userInfo.getId(), "the primary key should not be null");
    }

    @Test
    public void testDeleteLogic() {
        int effect = testMapper.deleteLogic(2);
        Optional<TestEntity> result = testMapper.selectByKey(2);
        assertEquals(effect, 1);
        assertTrue(result.isPresent());
    }

    @Test
    public void testUpdate() {
        String newName = getRandom();
        TestEntity userInfo = new TestEntity();
        userInfo.setId(2L);
        userInfo.setUsername(newName);
        int effect = testMapper.updateByKey(userInfo, true);
        Optional<TestEntity> result = testMapper.selectByKey(2);
        assertEquals(effect, 1);
        assertEquals(result.get().getUsername(), newName);
    }

    @Test
    public void testUpdateMap() {
        String newName = getRandom();
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", 2);
        map.put("username", newName);
        int effect = testMapper.updateByKey(map);
        Optional<TestEntity> result = testMapper.selectByKey(2);
        assertTrue(result.isPresent());
        System.out.println(result.get());
        assertEquals(effect, 1);
        assertEquals(result.get().getUsername(), newName);
    }


    @Test
    public void testSelectByKey() {
        Optional<TestEntity> result = testMapper.selectByKey(2);
        assertTrue(result.isPresent());
        System.out.println(result.get());
    }

    @Test
    public void testSelectList() {
        List<TestEntity> userInfoList = testMapper.selectList(builder ->
                builder.select("id", "username").eq("accountType", "101"));
        System.out.println(userInfoList);
        assertAll(
                () -> assertNotNull(userInfoList, "the result should not be null"),
                () -> assertTrue(userInfoList.size() > 0, "the result should not be empty")
        );
    }

    @Test
    public void testSelectLambda() {
        TestQuery query = new TestQuery();
        query.setUsername("王五");
        List<TestEntity> userInfoList = testMapper.selectList(sqlBuilder -> sqlBuilder.lambda(query).eq(TestQuery::getUsername).eq(TestQuery::getUsername)
        );
    }


    @Test
    public void testSelectListHasNotColumn() {

        List<TestEntity> userInfoList = testMapper.selectList(builder -> builder.page(1, 2));
        System.out.println(userInfoList);
        assertAll(
                () -> assertNotNull(userInfoList, "the result should not be null"),
                () -> assertTrue(userInfoList.size() > 0, "the result size should not be 5")
        );
    }
}
