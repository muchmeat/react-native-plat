package com.component.map;


import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.UIManagerModule;

public class AGSMapModule extends ReactContextBaseJavaModule {

    public AGSMapModule(ReactApplicationContext context) {
        super(context);
    }

    @NonNull
    @Override
    public String getName() {
        return "AGSMapModule";
    }

    public AGSMapView getView(final int viewId) {
//        AGSMapView agsMapView = null;
        UIManagerModule uiManagerModule = getReactApplicationContext().getNativeModule(UIManagerModule.class);
        return (AGSMapView) uiManagerModule.resolveView(viewId);
//        uiManagerModule.addUIBlock(nativeViewHierarchyManager -> {
//            View view = nativeViewHierarchyManager.resolveView(viewId);
//            if (view instanceof AGSMapView) {
//                agsMapView[0] =(AGSMapView) view;
//            }
//        });
//        return agsMapView;
    }

    @ReactMethod
    public void drawStop(int viewId, Callback callback) {
        AGSMapView agsMapView = getView(viewId);
        agsMapView.drawStop(callback);
    }

    @ReactMethod
    public void drawPoint(int viewId) {
        AGSMapView agsMapView = getView(viewId);
        agsMapView.drawPoint();
    }

    @ReactMethod
    public void drawMultiPoint(int viewId) {
        AGSMapView agsMapView = getView(viewId);
        agsMapView.drawMultiPoint();
    }

    @ReactMethod
    public void drawFreeHandPolygon(int viewId) {
        AGSMapView agsMapView = getView(viewId);
        agsMapView.drawFreeHandPolygon();
    }

    @ReactMethod
    public void drawPolygon(int viewId) {
        AGSMapView agsMapView = getView(viewId);
        agsMapView.drawPolygon();
    }

    @ReactMethod
    public void drawPolyline(int viewId) {
        AGSMapView agsMapView = getView(viewId);
        agsMapView.drawPolyline();
    }

    @ReactMethod
    public void drawFreeHandLine(int viewId) {
        AGSMapView agsMapView = getView(viewId);
        agsMapView.drawFreeHandLine();
    }

    @ReactMethod
    public void drawRectangle(int viewId) {
        AGSMapView agsMapView = getView(viewId);
        agsMapView.drawRectangle();
    }
}
