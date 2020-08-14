/*
 * Copyright (c) 2005, 2019, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.example.mybatisplus.entity;

import java.util.Date;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;


public class KgapAnnoVersion{

	@TableId(type = IdType.AUTO)
	private Long id;
	private Long taskId;
	private String versionName;
	private Boolean status;
	private Integer annoNum;
	private Integer totalNum;
	private String remark;
	private String createUser;
	private Date createTime;
	private String schemaInfo;
	private String creatorName;
	private String modifierName;
	private String createOrgName;
	private String modifyOrgName;
	private Long creatorId;
	private Long modifierId;
	private Long createOrgId;
	private Long modifyOrgId;
	private Date timeCreated;
	private Date timeModified;

	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setAnnoNum(Integer annoNum) {
		this.annoNum = annoNum;
	}
	public Integer getAnnoNum() {
		return annoNum;
	}
	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}
	public Integer getTotalNum() {
		return totalNum;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getRemark() {
		return remark;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setSchemaInfo(String schemaInfo) {
		this.schemaInfo = schemaInfo;
	}
	public String getSchemaInfo() {
		return schemaInfo;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setModifierName(String modifierName) {
		this.modifierName = modifierName;
	}
	public String getModifierName() {
		return modifierName;
	}
	public void setCreateOrgName(String createOrgName) {
		this.createOrgName = createOrgName;
	}
	public String getCreateOrgName() {
		return createOrgName;
	}
	public void setModifyOrgName(String modifyOrgName) {
		this.modifyOrgName = modifyOrgName;
	}
	public String getModifyOrgName() {
		return modifyOrgName;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}
	public Long getModifierId() {
		return modifierId;
	}
	public void setCreateOrgId(Long createOrgId) {
		this.createOrgId = createOrgId;
	}
	public Long getCreateOrgId() {
		return createOrgId;
	}
	public void setModifyOrgId(Long modifyOrgId) {
		this.modifyOrgId = modifyOrgId;
	}
	public Long getModifyOrgId() {
		return modifyOrgId;
	}
	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
	public Date getTimeCreated() {
		return timeCreated;
	}
	public void setTimeModified(Date timeModified) {
		this.timeModified = timeModified;
	}
	public Date getTimeModified() {
		return timeModified;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
