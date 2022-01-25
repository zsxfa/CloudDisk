package com.zsxfa.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zsxfa
 */
@Data
public class R {

    private Boolean success;

    private Integer code;

    private String message;

    private Map<String, Object> data = new HashMap();
//    private T data;
    /**
     * 构造器私有
     */
    private R(){}

    /**
     * 返回成功
     */
    public static R ok(){
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResponseEnum.SUCCESS.getCode());
        r.setMessage(ResponseEnum.SUCCESS.getMessage());
        return r;
    }

    /**
     * 返回失败
     */
    public static R error(){
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResponseEnum.ERROR.getCode());
        r.setMessage(ResponseEnum.ERROR.getMessage());
        return r;
    }

    /**
     * 设置特定结果
     */
    public static R setResult(ResponseEnum responseEnum){
        R r = new R();
        r.setCode(responseEnum.getCode());
        r.setMessage(responseEnum.getMessage());
        return r;
    }

    public R message(String message){
        this.setMessage(message);
        return this;
    }

    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    public R data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    public R data(Map<String, Object> map){
        this.setData(map);
        return this;
    }
}
