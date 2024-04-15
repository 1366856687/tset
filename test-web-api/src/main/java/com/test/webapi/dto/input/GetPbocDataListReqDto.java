package com.test.webapi.dto.input;

import com.test.webapi.dto.PageInfoReq;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class GetPbocDataListReqDto extends PageInfoReq {

    @NotBlank(message = "名字不能为空")
    private String name;

    @NotNull(message = "年龄不能为空")
    private Integer age;

    private Date day = new Date();

}
