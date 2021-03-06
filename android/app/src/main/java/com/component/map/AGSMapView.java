package com.component.map;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.plat.R;

public class AGSMapView extends LinearLayout {

    View rootView;
    public MapView mapView;

    public AGSMapView(Context context) {
        super(context);
        //移除水印 Licensed For Developer Use Only
        //http://zhihu.geoscene.cn/article/3936
        //https://developers.arcgis.com/pricing/licensing/
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4449636536,none,NKMFA0PL4S0DRJE15166");
        rootView = inflate(context.getApplicationContext(), R.layout.activity_main_arcgis, this);
        mapView = rootView.findViewById(R.id.mapView);
//        Basemap basemap = new Basemap(WebTiledLayerUtil.createLayer("http://192.168.0.117:8080/", null));
//        Basemap basemap = new Basemap(WebTiledLayerUtil.createLayer("http://t0.tianditu.com/", null));
//        WebTiledLayer webTiledLayer = TianDiTuTiledMapServiceLayerUtil.CreateTianDiTuTiledLayer(TianDiTuTiledMapServiceLayerUtil.LayerType.TIANDITU_IMAGE_2000);
        WebTiledLayer webTiledLayer = TDTLayerUtil.CreateTianDiTuTiledLayer(TDTLayerUtil.LayerType.TIANDITU_VECTOR_MERCATOR);
        webTiledLayer.loadAsync();
        Basemap basemap = new Basemap(webTiledLayer);
//        mapView.setMap(new ArcGISMap(Basemap.Type.STREETS_VECTOR, 31.3583, 118.4271, 17));
        mapView.setMap(new ArcGISMap(basemap));
        //移除下方logo Powered By Esri
        mapView.setAttributionTextVisible(false);

//        mapView.getMap().addDoneLoadingListener(() -> {
//            ArcGISRuntimeException e = mapView.getMap().getLoadError();
//            Boolean success = e != null;
//            String errorMessage = !success ? "" : e.getMessage();
//            WritableMap map = Arguments.createMap();
//            map.putBoolean("success",success);
//            map.putString("errorMessage",errorMessage);
//
//            emitEvent("onMapDidLoad",map);
//        });
    }
}
