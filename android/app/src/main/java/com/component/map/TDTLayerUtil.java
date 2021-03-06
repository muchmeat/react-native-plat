package com.component.map;

import com.esri.arcgisruntime.arcgisservices.LevelOfDetail;
import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.WebTiledLayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TDTLayerUtil {
    //需要自己去申请,绑定包名和SHA1，我这个你们用不了
    private static final String key = "4eeaf737c0d61a4ee834a26b22658292";
    private static final List<String> SubDomain =
            Arrays.asList(new String[]{"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"});

    private static final int DPI = 96;
    private static final int minZoomLevel = 1;
    private static final int maxZoomLevel = 18;
    private static final int tileWidth = 256;
    private static final int tileHeight = 256;
    private static final SpatialReference SRID_MERCATOR = SpatialReference.create(102100);

    private static final double X_MIN_MERCATOR = -20037508.3427892;
    private static final double Y_MIN_MERCATOR = -20037508.3427892;
    private static final double X_MAX_MERCATOR = 20037508.3427892;
    private static final double Y_MAX_MERCATOR = 20037508.3427892;
    private static final Point ORIGIN_MERCATOR =
            new Point(-20037508.3427892, 20037508.3427892, SRID_MERCATOR);
    private static final Envelope ENVELOPE_MERCATOR =
            new Envelope(X_MIN_MERCATOR,
                    Y_MIN_MERCATOR,
                    X_MAX_MERCATOR,
                    Y_MAX_MERCATOR,
                    SRID_MERCATOR);

    private static final SpatialReference SRID_2000 = SpatialReference.create(4490);
    private static final double X_MIN_2000 = -180;
    private static final double Y_MIN_2000 = -90;
    private static final double X_MAX_2000 = 180;
    private static final double Y_MAX_2000 = 90;
    private static final Point ORIGIN_2000 = new Point(-180, 90, SRID_2000);
    private static final Envelope ENVELOPE_2000 =
            new Envelope(X_MIN_2000,
                    Y_MIN_2000,
                    X_MAX_2000,
                    Y_MAX_2000,
                    SRID_2000);

    private static final double[] SCALES = {
            2.958293554545656E8, 1.479146777272828E8,
            7.39573388636414E7, 3.69786694318207E7,
            1.848933471591035E7, 9244667.357955175,
            4622333.678977588, 2311166.839488794,
            1155583.419744397, 577791.7098721985,
            288895.85493609926, 144447.92746804963,
            72223.96373402482, 36111.98186701241,
            18055.990933506204, 9027.995466753102,
            4513.997733376551, 2256.998866688275,
            1128.4994333441375
    };
    private static final double[] RESOLUTIONS_MERCATOR = {
            78271.51696402048, 39135.75848201024,
            19567.87924100512, 9783.93962050256,
            4891.96981025128, 2445.98490512564,
            1222.99245256282, 611.49622628141,
            305.748113140705, 152.8740565703525,
            76.43702828517625, 38.21851414258813,
            19.109257071294063, 9.554628535647032,
            4.777314267823516, 2.388657133911758,
            1.194328566955879, 0.5971642834779395,
            0.298582141738970};
    private static final double[] RESOLUTIONS_2000 = {
            0.7031249999891485, 0.35156249999999994,
            0.17578124999999997, 0.08789062500000014,
            0.04394531250000007, 0.021972656250000007,
            0.01098632812500002, 0.00549316406250001,
            0.0027465820312500017, 0.0013732910156250009,
            0.000686645507812499, 0.0003433227539062495,
            0.00017166137695312503, 0.00008583068847656251,
            0.000042915344238281406, 0.000021457672119140645,
            0.000010728836059570307, 0.000005364418029785169};

    public static WebTiledLayer CreateTianDiTuTiledLayer(LayerType layerType) {
        String type = "";
        String tilematrixset = "";
        WebTiledLayer webTiledLayer = null;
        String mainUrl;
        TileInfo mainTileInfo;
        Envelope mainEnvelope;
        try {
            switch (layerType) {
                case TIANDITU_VECTOR_MERCATOR:
                    type = "vec";
                    tilematrixset = "w";
                    break;
                case TIANDITU_VECTOR_MERCATOR_LABLE:
                    type = "cva";
                    tilematrixset = "w";
                    break;
                case TIANDITU_VECTOR_2000:
                    type = "vec";
                    tilematrixset = "c";
                    break;
                case TIANDITU_VECTOR_2000_LABLE:
                    type = "cva";
                    tilematrixset = "c";
                    break;
                case TIANDITU_IMAGE_MERCATOR:
                    type = "img";
                    tilematrixset = "w";
                    break;
                case TIANDITU_IMAGE_MERCATOR_LABLE:
                    type = "cia";
                    tilematrixset = "w";
                    break;
                case TIANDITU_IMAGE_2000:
                    type = "img";
                    tilematrixset = "c";
                    break;
                case TIANDITU_IMAGE_2000_LABLE:
                    type = "cia";
                    tilematrixset = "c";
                    break;
                case TIANDITU_TERRAIN_MERCATOR:
                    type = "ter";
                    tilematrixset = "w";
                    break;
                case TIANDITU_TERRAIN_MERCATOR_LABLE:
                    type = "cta";
                    tilematrixset = "w";
                    break;
                case TIANDITU_TERRAIN_2000:
                    type = "ter";
                    tilematrixset = "c";
                    break;
                case TIANDITU_TERRAIN_2000_LABLE:
                    type = "cta";
                    tilematrixset = "c";
                    break;
            }
            mainUrl = "http://t0.tianditu.gov.cn/"
                    + type + "_" + tilematrixset + "/wmts?" +
                    "service=wmts" +
                    "&request=gettile" +
                    "&version=1.0.0" +
                    "&layer=" + type +
                    "&format=tiles" +
                    "&STYLE=default" +
                    "&tilematrixset=" + tilematrixset +
                    "&tilecol={col}" +
                    "&tilerow={row}" +
                    "&tilematrix={level}" +
                    "&tk=" + key;
            List<LevelOfDetail> mainLevelOfDetail = new ArrayList<LevelOfDetail>();
            Point mainOrigin;
            if (tilematrixset.equals("c")) {
                for (int i = minZoomLevel; i <= maxZoomLevel; i++) {
                    LevelOfDetail item =
                            new LevelOfDetail(i, RESOLUTIONS_2000[i - 1], SCALES[i - 1]);
                    mainLevelOfDetail.add(item);
                }
                mainEnvelope = ENVELOPE_2000;
                mainOrigin = ORIGIN_2000;
            } else {
                for (int i = minZoomLevel; i <= maxZoomLevel; i++) {
                    LevelOfDetail item =
                            new LevelOfDetail(i, RESOLUTIONS_MERCATOR[i - 1], SCALES[i - 1]);
                    mainLevelOfDetail.add(item);
                }
                mainEnvelope = ENVELOPE_MERCATOR;
                mainOrigin = ORIGIN_MERCATOR;
            }
            mainTileInfo = new TileInfo(
                    DPI,
                    TileInfo.ImageFormat.PNG24,
                    mainLevelOfDetail,
                    mainOrigin,
                    mainOrigin.getSpatialReference(),
                    tileHeight,
                    tileWidth
            );
            webTiledLayer = new WebTiledLayer(
                    mainUrl,
                    SubDomain,
                    mainTileInfo,
                    mainEnvelope);
            webTiledLayer.setName(type);
            webTiledLayer.loadAsync();
        } catch (Exception e) {
            e.getCause();
        }
        return webTiledLayer;
    }

    public enum LayerType {
        /**
         * 天地图矢量墨卡托投影地图服务
         */
        TIANDITU_VECTOR_MERCATOR,
        TIANDITU_VECTOR_MERCATOR_LABLE,
        /**
         * 天地图矢量2000地图服务
         */
        TIANDITU_VECTOR_2000,
        TIANDITU_VECTOR_2000_LABLE,

        /**
         * 天地图影像墨卡托地图服务
         */
        TIANDITU_IMAGE_MERCATOR,
        TIANDITU_IMAGE_MERCATOR_LABLE,

        /**
         * 天地图影像2000地图服务
         */
        TIANDITU_IMAGE_2000,
        TIANDITU_IMAGE_2000_LABLE,
        /**
         * 天地图地形墨卡托地图服务
         */
        TIANDITU_TERRAIN_MERCATOR,
        TIANDITU_TERRAIN_MERCATOR_LABLE,
        /**
         * 天地图地形2000地图服务
         */
        TIANDITU_TERRAIN_2000,
        TIANDITU_TERRAIN_2000_LABLE,
    }
}
