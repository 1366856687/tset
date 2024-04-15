package com.test.webapi.dto.output;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GetPbocDataListResDto {
    private String id;
    private String name;
    private String value;
    private Date createdDate;
}
