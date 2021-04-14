import {
    findNodeHandle,
    requireNativeComponent,
    UIManager,
    NativeModules,
    Text,
    TouchableOpacity,
    View
} from "react-native";
import React, {Component} from "react";
import PropTypes from "prop-types";

const MapView = requireNativeComponent("AGSMapManager");
const AGSMapModule = NativeModules.AGSMapModule;

export default class AGSMapView extends Component {

    constructor(props) {
        super(props);
        this.ManagerName = "AGSMapManager";
        this._onGraphicClick = this._onGraphicClick.bind(this);
    }

    static propTypes = {
        initialMapCenter: PropTypes.arrayOf(PropTypes.array),
        minZoom: PropTypes.number,
        maxZoom: PropTypes.number,
        onGraphicClick: PropTypes.func,
    };

    static defaultProps = {
        initialMapCenter: []
    };

    _onGraphicClick(event: Event) {
        if (!this.props.onGraphicClick) {
            return;
        }
        this.props.onGraphicClick(event.nativeEvent);
    }

    UIDispatch = (command, params = []) => {
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this.AGSMapView),
            UIManager[this.ManagerName].Commands[command],
            [params]
        );
    };

    zoomIn = () => {
        this.UIDispatch('zoomIn');
    };

    zoomOut = () => {
        this.UIDispatch('zoomOut');
    };

    location = () => {
        this.UIDispatch('location');
    };

    changeBaseMap = (params) => {
        this.UIDispatch('changeBaseMap', params);
    };

    addGeometry = (params) => {
        this.UIDispatch('addGeometry', params);
    };

    addMarker = (params) => {
        this.UIDispatch('addMarker', params);
    };

    addPolyline = (params) => {
        this.UIDispatch('addPolyline', params);
    };

    addPolygon = (params) => {
        this.UIDispatch('addPolygon', params);
    };

    addCircle = (params) => {
        this.UIDispatch('addCircle', params);
    };

    drawPoint = () => {
        AGSMapModule.drawPoint(findNodeHandle(this.AGSMapView))
    };

    drawMultiPoint = () => {
        AGSMapModule.drawMultiPoint(findNodeHandle(this.AGSMapView))
    };

    drawFreeHandPolygon = () => {
        AGSMapModule.drawFreeHandPolygon(findNodeHandle(this.AGSMapView))
    };

    drawPolygon = () => {
        AGSMapModule.drawPolygon(findNodeHandle(this.AGSMapView))
    };

    drawPolyline = () => {
        AGSMapModule.drawPolyline(findNodeHandle(this.AGSMapView))
    };

    drawFreeHandLine = () => {
        AGSMapModule.drawFreeHandLine(findNodeHandle(this.AGSMapView))
    };

    drawRectangle = () => {
        AGSMapModule.drawRectangle(findNodeHandle(this.AGSMapView))
    };

    drawStop = (callBack) => {
        AGSMapModule.drawStop(findNodeHandle(this.AGSMapView), (res) => {
            if (callBack) {
                callBack(res)
            }
        })
    };

    componentWillUnmount() {
        this.UIDispatch('dispose');
    }

    render() {
        return <MapView ref={(ref) => this.AGSMapView = ref}
                        {...this.props}
                        // onGraphicClick={this._onGraphicClick}
        />
    }
}
