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


public class KgapAnnoDataVersion{

	@TableId(type = IdType.AUTO)
	private Integer id;
	private Long taskId;
	private Long versionId;
	private String relAnnos;
	private String entAnnos;
	private String docContent;
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

	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}
	public Long getVersionId() {
		return versionId;
	}
	public void setRelAnnos(String relAnnos) {
		this.relAnnos = relAnnos;
	}
	public String getRelAnnos() {
		return relAnnos;
	}
	public void setEntAnnos(String entAnnos) {
		this.entAnnos = entAnnos;
	}
	public String getEntAnnos() {
		return entAnnos;
	}
	public void setDocContent(String docContent) {
		this.docContent = docContent;
	}
	public String getDocContent() {
		return docContent;
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
