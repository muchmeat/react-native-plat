package com.component.map;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

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
        rootView = inflate(context.getApplicationContext(), R.layout.activity_main_arcgis, this);
        mapView = rootView.findViewById(R.id.mapView);
        Basemap basemap = new Basemap(WebTiledLayerUtil.createLayer("http://192.168.0.117:8080/", null));
//        mapView.setMap(new ArcGISMap(Basemap.Type.STREETS_VECTOR, 31.3583, 118.4271, 17));
        mapView.setMap(new ArcGISMap(basemap));
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
