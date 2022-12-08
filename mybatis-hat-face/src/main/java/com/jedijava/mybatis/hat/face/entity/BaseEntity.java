package com.jedijava.mybatis.hat.face.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

/**
 * @author liukaiyang
 * @since 2019/8/29 14:13
 */
public class BaseEntity extends HatEntity {

    private static final long serialVersionUID = 4043624175070137740L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 逻辑删除标记 0正常,1已删 ,默认值正常
     */
    @JsonIgnore
    private Boolean deleted=false;

    /**
     * 创建日期
     */
    private LocalDateTime createDateTime;

    /**
     * 更新日期
     */
    private LocalDateTime updateDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                ", deleted=" + deleted +
                ", createDateTime=" + createDateTime +
                ", updateDateTime=" + updateDateTime +
                '}';
    }
}
