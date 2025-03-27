package io.renren.modules.app.controller;
import io.renren.common.utils.CoordinateUtil;
import io.renren.modules.app.entity.TileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/map")
public class MapController {

    @Value("${map.tile.path:google_tile_s}")  // 从配置文件读取瓦片路径，默认值与你的Python代码一致
    private String tileBasePath;

    // 获取瓦片图片
    @GetMapping("/tile/{z}/{x}/{y}")
    public ResponseEntity<Resource> getTile(
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y) {
        try {
            Path tilePath = Paths.get(tileBasePath, String.valueOf(z), String.valueOf(x), y + ".png");
            Resource resource = new UrlResource(tilePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 获取瓦片信息（包括经纬度）
    @GetMapping("/tile-info/{z}/{x}/{y}")
    public ResponseEntity<TileResponse> getTileInfo(
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y) {
        try {
            double[] coordinates = CoordinateUtil.tileToWGS84(x, y, z);
            TileResponse response = new TileResponse();
            response.setTilePath(String.format("/api/map/tile/%d/%d/%d", z, x, y));
            response.setLongitude(coordinates[0]);
            response.setLatitude(coordinates[1]);
            response.setZoom(z);
            response.setX(x);
            response.setY(y);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 获取鼠标点击位置的经纬度
    @GetMapping("/coordinates")
    public ResponseEntity<double[]> getCoordinates(
            @RequestParam int z,
            @RequestParam int tileX,
            @RequestParam int tileY,
            @RequestParam int pixelX,
            @RequestParam int pixelY) {
        try {
            double[] coordinates = CoordinateUtil.pixelToWGS84(pixelX, pixelY, tileX, tileY, z);
            return ResponseEntity.ok(coordinates);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
