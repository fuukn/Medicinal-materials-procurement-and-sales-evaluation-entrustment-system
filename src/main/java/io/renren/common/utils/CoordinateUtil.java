package io.renren.common.utils;

public class CoordinateUtil {
    // 将瓦片坐标转换为WGS84经纬度
    public static double[] tileToWGS84(int x, int y, int z) {
        double n = Math.pow(2.0, z);
        double lon = x / n * 360.0 - 180.0;
        double latRad = Math.atan(Math.sinh(Math.PI * (1 - 2 * y / n)));
        double lat = Math.toDegrees(latRad);
        return new double[]{lon, lat};
    }

    // 将像素坐标转换为WGS84经纬度
    public static double[] pixelToWGS84(int pixelX, int pixelY, int tileX, int tileY, int zoom) {
        double tileSize = 256.0; // Google瓦片标准大小
        double[] tileLonLat = tileToWGS84(tileX, tileY, zoom);

        double n = Math.pow(2.0, zoom);
        double lonPerPixel = 360.0 / (n * tileSize);
        double latRad = Math.atan(Math.sinh(Math.PI * (1 - 2 * tileY / n)));
        double latDeg = Math.toDegrees(latRad);

        double lon = tileLonLat[0] + (pixelX * lonPerPixel);
        double lat = latDeg - (pixelY * (180.0 / (tileSize * n)));

        return new double[]{lon, lat};
    }
}
