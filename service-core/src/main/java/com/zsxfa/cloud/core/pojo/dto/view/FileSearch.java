package com.zsxfa.cloud.core.pojo.dto.view;

import lombok.Data;

/**
 * @author zsxfa
 */
@Data
public class FileSearch {
    private int Mon;
    private int Tue;
    private int Wed;
    private int Thu;
    private int Fri;
    private int Sat;
    private int Sun;


    public String toSting() {
        return Mon + "," + Tue + "," + Wed + "," + Thu + "," + Fri + "," + Sat + "," + Sun;
    }
}
