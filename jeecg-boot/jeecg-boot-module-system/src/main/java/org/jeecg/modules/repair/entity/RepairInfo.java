package org.jeecg.modules.repair.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 报修单信息表
 * @Author: jeecg-boot
 * @Date:   2019-09-03
 * @Version: V1.0
 */
@Data
@TableName("repair_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="repair_info对象", description="报修单信息表")
public class RepairInfo {
    
	/**修理工接单时间*/
	@Excel(name = "修理工接单时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修理工接单时间")
	private Date accTime;
	/**报修地址*/
	@Excel(name = "报修地址", width = 15)
    @ApiModelProperty(value = "报修地址")
	private String adress;
	/**报修内容*/
	@Excel(name = "报修内容", width = 15)
    @ApiModelProperty(value = "报修内容")
	private Object cont;
	/**报修单创建人*/
	@Excel(name = "报修单创建人", width = 15)
    @ApiModelProperty(value = "报修单创建人")
	private String createBy;
	/**报修单创建时间*/
	@Excel(name = "报修单创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "报修单创建时间")
	private Date createTime;
	/**评论内容*/
	@Excel(name = "评论内容", width = 15)
    @ApiModelProperty(value = "评论内容")
	private Object evalCont;
	/**满意度（满分10）*/
	@Excel(name = "满意度（满分10）", width = 15)
    @ApiModelProperty(value = "满意度（满分10）")
	private Integer evalDegree;
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private String id;
	/**报修人姓名*/
	@Excel(name = "报修人姓名", width = 15)
    @ApiModelProperty(value = "报修人姓名")
	private String name;
	/**维修完成时间*/
	@Excel(name = "维修完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "维修完成时间")
	private Date overTime;
	/**图片*/
	@Excel(name = "图片", width = 15)
    @ApiModelProperty(value = "图片")
	private String pic;
	/**报修提交时间*/
	@Excel(name = "报修提交时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "报修提交时间")
	private Date sendTime;
	/**修理工id*/
	@Excel(name = "修理工id", width = 15)
    @ApiModelProperty(value = "修理工id")
	private String staffId;
	/**修理工姓名*/
	@Excel(name = "修理工姓名", width = 15)
    @ApiModelProperty(value = "修理工姓名")
	private String staffName;
	/**开始维修时间*/
	@Excel(name = "开始维修时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始维修时间")
	private Date startTime;
	/**0：报修（默认）
	1：接收
	2：派工
	3：维修中
	4：维修完毕*/
	@Excel(name = "0：报修（默认） 1：接收 2：派工 3：维修中 4：维修完毕", width = 15)
    @ApiModelProperty(value = "0：报修（默认） 1：接收 2：派工 3：维修中 4：维修完毕")
	private java.lang.Integer stateTag;
	/**报修人电话*/
	@Excel(name = "报修人电话", width = 15)
    @ApiModelProperty(value = "报修人电话")
	private java.lang.String tel;
	/**报修标题*/
	@Excel(name = "报修标题", width = 15)
    @ApiModelProperty(value = "报修标题")
	private java.lang.String title;
	/**最近一次更新人*/
	@Excel(name = "最近一次更新人", width = 15)
    @ApiModelProperty(value = "最近一次更新人")
	private java.lang.String updateBy;
	/**最近更新时间*/
	@Excel(name = "最近更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最近更新时间")
	private Date updateTime;
	/**报修人id*/
	@Excel(name = "报修人id", width = 15)
    @ApiModelProperty(value = "报修人id")
	private String userId;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private Object vistBz;
	/**备注时间*/
	@Excel(name = "备注时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "备注时间")
	private Date vistTime;
}
