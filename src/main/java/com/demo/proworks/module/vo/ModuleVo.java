package com.demo.proworks.module.vo;

import com.inswave.elfw.annotation.ElDto;
import com.inswave.elfw.annotation.ElDtoField;
import com.inswave.elfw.annotation.ElVoField;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("elExcludeFilter")
@ElDto(FldYn = "", logicalName = "모듈정보")
public class ModuleVo extends com.demo.proworks.cmmn.ProworksCommVO {
    private static final long serialVersionUID = 1L;

    @ElDtoField(logicalName = "module_id", physicalName = "moduleId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String moduleId;

    @ElDtoField(logicalName = "code", physicalName = "code", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String code;

    @ElDtoField(logicalName = "name", physicalName = "name", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String name;

    @ElDtoField(logicalName = "description", physicalName = "description", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String description;

    @ElDtoField(logicalName = "price", physicalName = "price", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String price;

    @ElDtoField(logicalName = "icon", physicalName = "icon", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String icon;

    @ElDtoField(logicalName = "search_module_id", physicalName = "scModuleId", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scModuleId;

    @ElDtoField(logicalName = "search_code", physicalName = "scCode", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scCode;

    @ElDtoField(logicalName = "search_name", physicalName = "scName", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scName;

    @ElDtoField(logicalName = "search_description", physicalName = "scDescription", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scDescription;

    @ElDtoField(logicalName = "search_price", physicalName = "scPrice", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scPrice;

    @ElDtoField(logicalName = "search_icon", physicalName = "scIcon", type = "String", typeKind = "", fldYn = "", length = 0, dotLen = 0, baseValue = "", desc = "")
    private String scIcon;

    @ElVoField(physicalName = "moduleId")
    public String getModuleId(){
        return moduleId;
    }

    @ElVoField(physicalName = "moduleId")
    public void setModuleId(String moduleId){
        this.moduleId = moduleId;
    }

    @ElVoField(physicalName = "code")
    public String getCode(){
        return code;
    }

    @ElVoField(physicalName = "code")
    public void setCode(String code){
        this.code = code;
    }

    @ElVoField(physicalName = "name")
    public String getName(){
        return name;
    }

    @ElVoField(physicalName = "name")
    public void setName(String name){
        this.name = name;
    }

    @ElVoField(physicalName = "description")
    public String getDescription(){
        return description;
    }

    @ElVoField(physicalName = "description")
    public void setDescription(String description){
        this.description = description;
    }

    @ElVoField(physicalName = "price")
    public String getPrice(){
        return price;
    }

    @ElVoField(physicalName = "price")
    public void setPrice(String price){
        this.price = price;
    }

    @ElVoField(physicalName = "icon")
    public String getIcon(){
        return icon;
    }

    @ElVoField(physicalName = "icon")
    public void setIcon(String icon){
        this.icon = icon;
    }

    @ElVoField(physicalName = "scModuleId")
    public String getScModuleId(){
        return scModuleId;
    }

    @ElVoField(physicalName = "scModuleId")
    public void setScModuleId(String scModuleId) {
        this.scModuleId = scModuleId;
    }

    @ElVoField(physicalName = "scCode")
    public String getScCode(){
        return scCode;
    }

    @ElVoField(physicalName = "scCode")
    public void setScCode(String scCode) {
        this.scCode = scCode;
    }

    @ElVoField(physicalName = "scName")
    public String getScName(){
        return scName;
    }

    @ElVoField(physicalName = "scName")
    public void setScName(String scName) {
        this.scName = scName;
    }

    @ElVoField(physicalName = "scDescription")
    public String getScDescription(){
        return scDescription;
    }

    @ElVoField(physicalName = "scDescription")
    public void setScDescription(String scDescription) {
        this.scDescription = scDescription;
    }

    @ElVoField(physicalName = "scPrice")
    public String getScPrice(){
        return scPrice;
    }

    @ElVoField(physicalName = "scPrice")
    public void setScPrice(String scPrice) {
        this.scPrice = scPrice;
    }

    @ElVoField(physicalName = "scIcon")
    public String getScIcon(){
        return scIcon;
    }

    @ElVoField(physicalName = "scIcon")
    public void setScIcon(String scIcon) {
        this.scIcon = scIcon;
    }

    @Override
    public String toString() {
        return "ModuleVo [moduleId=" + moduleId + ",code=" + code + ",name=" + name + ",description=" + description + ",price=" + price + ",icon=" + icon + ",scModuleId=" + scModuleId + ",scCode=" + scCode + ",scName=" + scName + ",scDescription=" + scDescription + ",scPrice=" + scPrice + ",scIcon=" + scIcon + "]";
    }

    public boolean isFixedLengthVo() {
        return false;
    }

}
