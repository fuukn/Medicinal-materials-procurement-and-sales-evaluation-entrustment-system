
package io.renren.modules.app.entity;

import lombok.Data;

@Data
public class TileResponse {
    private String tilePath;    // 瓦片文件路径
    private double longitude;   // 经度
    private double latitude;    // 纬度
    private int zoom;          // 缩放级别
    private int x;             // x坐标
    private int y;             // y坐标
}
