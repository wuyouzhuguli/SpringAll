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


public class KgapAnnoEntLabel{

	@TableId(type = IdType.AUTO)
	private Long id;
	private Long labelId;
	private String text;
	private Long startindex;
	private Long endindex;
	private String labelName;
	private String docId;
	private Long taskId;
	private String annoUser;
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
	public void setLabelId(Long labelId) {
		this.labelId = labelId;
	}
	public Long getLabelId() {
		return labelId;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public void setStartindex(Long startindex) {
		this.startindex = startindex;
	}
	public Long getStartindex() {
		return startindex;
	}
	public void setEndindex(Long endindex) {
		this.endindex = endindex;
	}
	public Long getEndindex() {
		return endindex;
	}
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	public String getLabelName() {
		return labelName;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getDocId() {
		return docId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setAnnoUser(String annoUser) {
		this.annoUser = annoUser;
	}
	public String getAnnoUser() {
		return annoUser;
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
