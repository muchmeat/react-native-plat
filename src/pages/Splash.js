import {Component} from "react";
import {connect} from "react-redux";
import SplashScreen from "react-native-splash-screen";
import {post} from "../utils/http";
import Global from "../utils/Global";
import {ToastAndroid, NativeModules} from "react-native";
import * as accountAction from "../redux/actions/accountAction";

class Splash extends Component {
    constructor(props) {
        super(props);
        this._bootstrapAsync();
    }

    _bootstrapAsync = (deviceCode) => {
        let _this = this;
        let {setAccount} = _this.props;
        let url = Global.REQUEST_BASE_URL + "/tokenVali";
        if (deviceCode) {
            url += "?deviceCode=" + deviceCode;
        }
        //启动通讯服务
        // NativeModules.StarRtcModule.startService("002,李四");
        post(url).then((res) => {
            // This will switch to the App screen or Auth screen and this loading
            // screen will be unmounted and thrown away.
            if (res && res.success) {
                //启动通讯服务
                NativeModules.StarRtcModule.startService(res.data.user.id + "," + res.data.user.name);
                /* let roleList = res.data.user.roleList;
                 let roleType = 1; //个人用户
                 for (let index in roleList) {
                     if (roleList[index].code === 'DWGLY') {
                         roleType = 2; //单位管理员
                         break
                     } else if (roleList[index].code === 'MW') {
                         roleType = 3; //门卫
                         break
                     } else if (roleList[index].code === 'BMGLY') {
                         roleType = 4; //部门管理员
                         break
                     }
                 }*/
                let roleType = 2;
                setAccount(res.data);
                res.data.roleType = roleType;
                this.props.navigation.navigate('App');
            } else {
                this.props.navigation.navigate('Auth');
            }
            SplashScreen.hide();
        }).catch((error) => {
            this.props.navigation.navigate('Auth');
            ToastAndroid.show("请求服务异常", ToastAndroid.SHORT);
            SplashScreen.hide();
        });
    };

    render() {
        return null;
    }

}

export default connect(
    (state) => ({}),
    (dispatch) => ({
        setAccount: (data) => dispatch(accountAction.setAccount(data))
    })
)(Splash)
