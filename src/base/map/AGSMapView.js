import {findNodeHandle, requireNativeComponent, UIManager} from "react-native";
import React, {Component} from "react";
import PropTypes from "prop-types";

const MapView = requireNativeComponent("AGSMapManager");

export default class AGSMapView extends Component {

    constructor(props) {
        super(props);
        this.ManagerName = "AGSMapManager";
    }

    static propTypes = {
        marker: PropTypes.arrayOf(PropTypes.object),
        polyline: PropTypes.arrayOf(PropTypes.object),
        polygon: PropTypes.arrayOf(PropTypes.object),
        circle: PropTypes.object,
    };

    // UIDispatch = (command, params = []) => {
    //     UIManager.dispatchViewManagerCommand(
    //         findNodeHandle(this.mineMapView),
    //         UIManager[this.ManagerName].Commands[command],
    //         [params]
    //     );
    // };
    //
    // _addMarker = (params) => {
    //     this.UIDispatch('addMarker', params);
    // };
    //
    // _addPolyline = (params) => {
    //     this.UIDispatch('addPolyline', params);
    // };
    //
    // _addPolygon = (params) => {
    //     this.UIDispatch('addPolygon', params);
    // };
    //
    // _addCircle = (params) => {
    //     this.UIDispatch('addCircle', params);
    // };
    //
    // _mapReadyCallback = () => {
    //     this._addMarker(this.props.marker);
    //     this._addPolyline(this.props.polyline);
    //     this._addPolygon(this.props.polygon);
    //     this._addCircle(this.props.circle);
    // };

    // componentWillUnmount() {
    //     console.warn("componentWillUnmount")
    //     this.UIDispatch('dispose');
    // }

    render() {
        return <MapView ref={(ref) => this.AGSMapView = ref}
                        style={{flex: 1}}
        />
    }
}
