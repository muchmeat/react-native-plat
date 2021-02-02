import React, {Component} from 'react';
import {
    Image,
    View,
    Dimensions,
    Alert,
    BackHandler,
    Keyboard, NativeModules,
    ToastAndroid, AsyncStorage
} from 'react-native';
import {connect} from "react-redux";
import Svg from "react-native-svg";
import {Input, Button} from 'react-native-elements';
import Global from "../utils/Global";
import {storeAccount} from "../utils/account";
import * as accountAction from "../redux/actions/accountAction";
import {postJson, postForm} from "../utils/http";
import IconLib from "../resources/svg/IconLib";

const SCREEN_WIDTH = Dimensions.get('window').width;
const SCREEN_HEIGHT = Dimensions.get('window').height;


export class Login extends Component {

    constructor(props) {
        super(props);
        this.state = {
            username: '',
            usernameValid: true,
            password: '',
            showLoading: false,
            enClick: false,
        };
        BackHandler.addEventListener('hardwareBackPress', this.onBackHandler);
        //获取统一认证用户信息
        this._checkUserInfo();
    }

    componentWillUnmount() {
        BackHandler.removeEventListener('hardwareBackPress', this.onBackHandler);
    }

    //获取服务总线的token
    _checkUserInfo = () => {
        let authorization = "VTJ3Q0Z2OFY0cTRLVG9VTUR2TEx6VzpRbmNGZ0VLckR0U3JaQWRTclNxY1FI";
        postForm(Global.TOKEN_URL, {authorization: 'Basic ' + authorization}).then((responseData) => {
            let data = responseData;
            console.warn(data);
            if (data && data.access_token) {
                let access_token = data.access_token;
                Global.ACCESS_TOKEN = access_token;
                //原生后台请求获取用户信息
                NativeModules.sendMessage.getUserInfo((e) => {
                    // let userInfo = e.userInfo;
                    if (e && e.userInfo) {
                        ToastAndroid.show("你好，警号" + e.userInfo, ToastAndroid.LONG);
                        this.setState({
                            username: e.userInfo,
                            password: '111',
                        });
                        this.submitLoginCredentials(e.userInfo);
                    } else {
                        ToastAndroid.show("获取用户失败", ToastAndroid.SHORT);
                    }
                });
            } else {
                ToastAndroid.show("验证失败：" + data.error_description, ToastAndroid.SHORT);
                // _this._message("failed", "验证失败：" + data.error_description);
            }
        }).catch((error) => {//请求网络失败
            ToastAndroid.show("请求服务总线令牌网络失败" + error, ToastAndroid.SHORT);
            // _this._message("failed", "请求服务总线令牌网络失败" + error);
        });

    };

    /**
     * 按钮输入登录
     */
    submitLoginCredentialsByButton = () => {
        let _this = this;
        console.warn(111)
        const {password, username, loading, enClick} = _this.state;
        let {setAccount} = _this.props;
        if (loading || !enClick) return;
        _this.setState({loading: true}, () => {
            if (password && username) {
                let json = {
                    username: username, password: password, loginType: "Account"
                };
                console.warn(222)
                postJson(Global.REQUEST_BASE_URL + "/loginValiApp", json).then((res) => {
                    _this.setState({loading: false});
                    if (res && res.success) {
                        let user = res.data.user;
                        let dict = res.data.dict;
                        let dwssxjb = res.data.dwssxjb;
                        AsyncStorage.setItem("user", JSON.stringify(user));
                        AsyncStorage.setItem("userRole", JSON.stringify(user.roleList));
                        AsyncStorage.setItem("dict", JSON.stringify(dict));
                        AsyncStorage.setItem("dwssxjb", JSON.stringify(dwssxjb));
                        setAccount(res.data);
                        storeAccount(res.data).then(() => {
                            this.props.navigation.navigate('App');
                        });
                    } else {
                        if (res.msg) {
                            ToastAndroid.show(res.msg, ToastAndroid.SHORT);
                        } else {
                            ToastAndroid.show("登录失败，请检查账号信息", ToastAndroid.SHORT);
                        }
                    }
                }).catch((error) => {
                    console.warn(error)
                    _this.setState({loading: false});
                });
            } else {
                _this.setState({loading: false});
                ToastAndroid.show("用户名密码为空", ToastAndroid.SHORT);
            }
        });
    };

    submitLoginCredentials(username) {
        let _this = this;
        let {setAccount} = _this.props;
        // const {showLoading, username, enClick} = this.state;
        // if (showLoading || !enClick) return;
        if (username) {
            this.setState({
                showLoading: true
            }, () => {
                Keyboard.dismiss();
                let json = {
                    username: username,
                    loginType: "Account"
                };
                postJson(Global.REQUEST_BASE_URL + "/loginValiApp", json).then((res) => {
                    _this.setState({showLoading: false});
                    if (res && res.success) {
                        //启动通讯服务
                        // NativeModules.StarRtcModule.startService(res.data.user.id + "," + res.data.user.name);
                        let user = res.data.user;
                        let dict = res.data.dict;
                        let dwssxjb = res.data.dwssxjb;
                        AsyncStorage.setItem("user", JSON.stringify(user));
                        AsyncStorage.setItem("userRole", JSON.stringify(user.roleList));
                        AsyncStorage.setItem("dict", JSON.stringify(dict));
                        AsyncStorage.setItem("dwssxjb", JSON.stringify(dwssxjb));
                        setAccount(res.data);
                        storeAccount(res.data).then(() => {
                            this.props.navigation.navigate('App');
                        });
                    } else {
                        if (res.msg) {
                            ToastAndroid.show(res.msg, ToastAndroid.SHORT);
                        } else {
                            ToastAndroid.show("登录失败，请检查账号信息", ToastAndroid.SHORT);
                        }
                    }
                }).catch((error) => {
                    _this.setState({showLoading: false});
                });
            });
        } else {
            Alert.alert("温馨提示", "用户名密码为空");
        }
    }

    _formUpdateState(text, targetName) {//表单输入框 更新状态
        this.setState({[targetName]: text}, () => {
            this.setState({
                enClick: !!(this.state.username && this.state.password)
            })
        });
    }

    render() {
        const {username, password, usernameValid, showLoading, enClick} = this.state;
        return (
            <View style={styles.container}>
                <Image style={{width: SCREEN_WIDTH, height: SCREEN_WIDTH * 600 / 1080 + 5}}
                       resizeMode={"cover"}
                       source={require('../resources/image/logo_login.png')}/>
                <View style={{
                    flex: 1,
                    backgroundColor: "#FFF",
                    top: -12,
                    bottom: -120,
                    borderTopLeftRadius: 12,
                    borderTopRightRadius: 12
                }}>
                    <View style={styles.loginInput}>
                        <Input
                            leftIcon={
                                <Svg height="15" width="15"
                                     viewBox="0 0 1024 1024">{IconLib.IC_USER}</Svg>
                            }
                            leftIconContainerStyle={styles.input.leftIconContainerStyle}
                            containerStyle={styles.input.inputContainer}
                            onChangeText={(text) => this._formUpdateState(text, "username")}
                            value={username}
                            inputContainerStyle={styles.input.inputContainerStyle}
                            inputStyle={styles.input.inputStyle}
                            placeholderTextColor={"#C0C0C0"}
                            keyboardAppearance="light"
                            placeholder="用户名"
                            autoFocus={false}
                            autoCapitalize="none"
                            autoCorrect={false}
                            returnKeyType="next"
                            onSubmitEditing={() => {
                                this.passwordInput.focus();
                            }}
                            blurOnSubmit={false}

                        />
                        <Input
                            leftIcon={
                                <Svg height="20" width="20"
                                     viewBox="0 0 1024 1024">{IconLib.IC_PASSWORD}</Svg>
                            }
                            leftIconContainerStyle={styles.input.leftIconContainerStyle}
                            containerStyle={styles.input.inputContainer}
                            onChangeText={(text) => this._formUpdateState(text, "password")}
                            value={password}
                            inputContainerStyle={styles.input.inputContainerStyle}
                            inputStyle={styles.input.inputStyle}
                            placeholderTextColor={"#C0C0C0"}
                            secureTextEntry={true}
                            keyboardAppearance="light"
                            placeholder="密 码"
                            autoCapitalize="none"
                            autoCorrect={false}
                            keyboardType="default"
                            returnKeyType="done"
                            ref={input => this.passwordInput = input}
                            blurOnSubmit={true}
                        />
                    </View>

                    <Button
                        title='登 录'
                        activeOpacity={0}
                        underlayColor="transparent"
                        onPress={this.submitLoginCredentialsByButton.bind(this)}
                        loading={showLoading}
                        loadingProps={{size: 'small', color: 'white'}}
                        disabled={!usernameValid}
                        buttonStyle={[styles.button.buttonStyle, {backgroundColor: enClick ? "#2E84E6" : "#C3C3C3"}]}
                        containerStyle={styles.button.containerStyle}
                        titleStyle={{fontWeight: 'bold', color: 'white'}}
                    />
                </View>

            </View>
        );
    }

    onBackHandler = () => {
        let {navigation} = this.props;
        if (navigation.isFocused()) {
            if (this.lastPressAndroidBack && this.lastPressAndroidBack + 2000 >= Date.now()) {
                return false
            }
            this.lastPressAndroidBack = Date.now();
            ToastAndroid.show('再按一次退出移动督察', ToastAndroid.SHORT);
            return true
        }
    };
}

const styles = Object.freeze({
    container: {
        flex: 1,
        backgroundColor: '#FFF',
    },
    loginInput: {
        flex: 3,
        justifyContent: 'center',
        alignItems: 'center'
    },
    input: {
        inputContainer: {
            marginVertical: 10,
            width: SCREEN_WIDTH / 1.2,
            height: 40,
            borderRadius: 20,
            backgroundColor: "#F5F5F5"
        },
        leftIconContainerStyle: {
            width: 25
        },
        inputContainerStyle: {borderBottomWidth: 0},
        inputStyle: {fontSize: 15, color: 'black'}
    },
    button: {
        buttonStyle: {
            height: 50,
            width: SCREEN_WIDTH / 1.2,
            borderRadius: 10,
            elevation: 0
        },
        containerStyle: {
            flex: 2,
            alignItems: "center",
            marginVertical: 10,
            borderColor: 'white',
            elevation: 0
        }
    }
});

export default connect(
    (state) => ({}),
    (dispatch) => ({
        setAccount: (data) => dispatch(accountAction.setAccount(data))
    })
)(Login)
