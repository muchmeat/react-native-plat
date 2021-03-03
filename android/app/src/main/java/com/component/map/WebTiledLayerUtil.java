package com.component.map;

import com.esri.arcgisruntime.arcgisservices.LevelOfDetail;
import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.WebTiledLayer;

import java.util.ArrayList;
import java.util.List;

public class WebTiledLayerUtil {
    private static final SpatialReference SRID = SpatialReference.create(4326);
    private static final List<String> SUB_DOMAINS = new ArrayList<String>(){{
        add("tdtjwd");
    }};

    private static final Point ORIGIN = new Point(-180, 90, SRID);
    private static final Envelope ENVELOPE = new Envelope(-180.0, -90.0, 180.0, 90, SRID);

    private static final int MIN_ZOOM_LEVEL = 2;
    private static final int MAX_ZOOM_LEVEL = 18;

    private static final double[] SCALES = {147748796.52937502,
            73874398.264687508, 36937199.132343754, 18468599.566171877,
            9234299.7830859385, 4617149.8915429693,
            2308574.9457714846, 1154287.4728857423, 577143.73644287116,
            288571.86822143558, 144285.93411071779, 72142.967055358895,
            36071.483527679447, 18035.741763839724,
            9017.8708819198619, 4508.9354409599309, 2254.4677204799655

    };
    private static final double[] RESOLUTIONS = {0.3515625,
            0.17578125, 0.087890625, 0.0439453125, 0.02197265625, 0.010986328125,
            0.0054931640625, 0.00274658203125, 0.001373291015625,
            0.0006866455078125, 0.00034332275390625, 0.000171661376953125,
            8.58306884765625e-005, 4.291534423828125e-005, 2.1457672119140625e-005,
            1.0728836059570313e-005, 5.3644180297851563e-006
    };

    public static WebTiledLayer createLayer(String src, List<String> subDomains) {
        List<LevelOfDetail> mainLevelOfDetail = new ArrayList<LevelOfDetail>();
        for (int i = 0; i <= MAX_ZOOM_LEVEL - MIN_ZOOM_LEVEL; i++) {
            LevelOfDetail item = new LevelOfDetail(i + MIN_ZOOM_LEVEL, RESOLUTIONS[i], SCALES[i]);
            mainLevelOfDetail.add(item);
        }
        TileInfo tileInfo = new TileInfo(
                96,
                TileInfo.ImageFormat.PNG24,
                mainLevelOfDetail,
                ORIGIN,
                SRID,
                256,
                256
        );
        String url = src + "{subDomain}/{level}/{col}/{row}.png";
        if(null == subDomains){
            subDomains = SUB_DOMAINS;
        }
        WebTiledLayer webTiledLayer = new WebTiledLayer(
                url,
                subDomains,
                tileInfo,
                ENVELOPE);
//        webTiledLayer.setName("PGIS");
        webTiledLayer.loadAsync();
        return webTiledLayer;
    }

}
