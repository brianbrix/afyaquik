package com.afyaquik.utils.dto.delete;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class DeleteDto {
        private String entityName;
        private List<Long> ids=new ArrayList<>();

}
