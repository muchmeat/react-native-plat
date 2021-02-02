/**
 * 全局配置项组件
 * Created by ruixin on 16/7/14.
 */
import React, {Component} from 'react';

export default class Global extends Component {

    static REQUEST_BASE_URL = 'http://10.125.6.114:8080/jwdcpt/app'; //本地
    static REQUEST_BASE_QUERY_URL = 'http://10.125.6.114:8080/jwdcpt/app/query'; //本地

    // static REQUEST_BASE_STURL = 'http://20.105.129.45/jwdcpt/app/query'; //真实环境
    // static REQUEST_BASE_URL = 'http://44.1.12.101:7001/jwdcpt/app';//真实环境

    static ACCESS_TOKEN = ""; //访问后台服务必须携带

    //省厅服务总线请求地址
    static TOKEN_URL = "http://20.105.130.44/sso/oauth2/token?grant_type=client_credentials";//正式

    static serviceResponse = {
        SUCCESS: {code: "0", msg: "保存成功"},
        ERROR: {code: "5", msg: "保存失败"},
        NETWORK_ERR: {msg: "网络请求异常"},
        REQUEST_ERR: {msg: "请求数据失败"}
    };


}
