package group.oneonetwo.hotelintelligencesystem.modules.hotel.model.vo;

import group.oneonetwo.hotelintelligencesystem.tools.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 文
 */
@ApiModel("酒店实体")
public class HotelVO implements Serializable {
    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("部门id")
    private  String deptId;

    @ApiModelProperty("酒店名")
    private String name;

    @ApiModelProperty("酒店地址")
    private String address;

    @ApiModelProperty("酒店停车场")
    private String parkingLot;

    @ApiModelProperty("其他政策")
    private String otherPolicy;

    @ApiModelProperty("封面")
    private String cover;

    @ApiModelProperty("是否删除")
    private Integer isDeleted;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private Date updateTime;


    private Page page;

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParkingLot() {
        return parkingLot;
    }

    public void setParkingLot(String parkingLot) {
        this.parkingLot = parkingLot;
    }

    public String getOtherPolicy() {
        return otherPolicy;
    }

    public void setOtherPolicy(String otherPolicy) {
        this.otherPolicy = otherPolicy;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "HotelVO{" +
                "id='" + id + '\'' +
                ", deptId='" + deptId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", parkingLot='" + parkingLot + '\'' +
                ", otherPolicy='" + otherPolicy + '\'' +
                ", cover='" + cover + '\'' +
                ", isDeleted=" + isDeleted +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}