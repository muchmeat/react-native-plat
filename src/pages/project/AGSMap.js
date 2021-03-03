/**
 * 地图
 * @author：czq
 */
import React, {Component} from 'react';
import {
    StyleSheet
} from 'react-native';
import {connect} from 'react-redux';// 引入connect函数
import AGSMapView from "../../base/map/AGSMapView";

class AGSMap extends Component {
    constructor(props) {
        super(props);
    }

    static navigationOptions = ({navigation}) => {
        return {
            title: 'ArcGIS地图',
            headerStyle: {
                borderBottomWidth: 0,
                backgroundColor: "#2E84E6",
                elevation: 0,
            }
        }
    };

    componentDidMount() {
    }

    render() {
        return <AGSMapView style={{flex: 1}}/>
    }
}


export default connect(
    (state) => ({
        user: state.accountReducer.user,
        dictionary: state.accountReducer.dictionary
    }),
    (dispatch) => ({})
)(AGSMap)
