import axios from 'axios';
import qs from 'qs'; //axios内置库，将json转为QueryString
import {ToastAndroid,AsyncStorage} from "react-native";
import Global from "../Global";

const CONNECT_TIMEOUT = 5000; //建立连接时间
const READ_TIMEOUT = 200000; //从服务器读取到可用资源所用的时间

//超时取消请求
function generateRequestCancelToken() {
    let cancelRequest;
    return async (config: Axios.AxiosXHRConfigBase<any>) => {
        config.cancelToken = new axios.CancelToken((cancelExecutor) => {
            cancelRequest = cancelExecutor
        });
        setTimeout(() => {
            if (cancelRequest instanceof Function) {
                cancelRequest('ECONNABORTED')
            }
        }, READ_TIMEOUT);
        return config;
    }
}

//新建一个 axios 实例
let instance = axios.create({
    headers: {'content-type': 'application/x-www-form-urlencoded'} //请求格式，数据需要利用qs库处理
});

instance.defaults.timeout = CONNECT_TIMEOUT;
const requestCancelToken = generateRequestCancelToken();
//请求拦截器 When timeout, it will throw an error: { message: 'ECONNABORTED' }, then you can handle this error.
instance.interceptors.request.use(requestCancelToken, null);
//响应拦截器
instance.interceptors.response.use(
    response => {
        // 如果返回的状态码为200，说明接口请求成功,这里为方便获取将data作为Promise回调参数
        // 否则抛出错误
        if (response.status === 200) {
            return Promise.resolve(response.data);
        } else {
            return Promise.reject(response);
        }
    },
    // 服务器状态码不以2开头，登录过期提示，错误提示等等
    // 下面列举几个常见的操作，其他需求可自行扩展
    error => {
        if ((error.code === 'ECONNABORTED' && error.message.indexOf('timeout') !== -1) || error.message === 'ECONNABORTED') {
            console.warn(error);
            ToastAndroid.show("请求超时", ToastAndroid.SHORT);
        }
        if (error.response.status) {
            // switch (error.response.status) {
            //     // 401: 未登录
            //     // 未登录则跳转登录页面，并携带当前页面的路径
            //     // 在登录成功后返回当前页面，这一步需要在登录页操作。
            //     case 401:
            //         router.replace({
            //             path: '/login',
            //             query: {
            //                 redirect: router.currentRoute.fullPath
            //             }
            //         });
            //         break;
            //
            //     // 403 token过期
            //     // 登录过期对用户进行提示
            //     // 清除本地token和清空vuex中token对象
            //     // 跳转登录页面
            //     case 403:
            //         Toast({
            //             message: '登录过期，请重新登录',
            //             duration: 1000,
            //             forbidClick: true
            //         });
            //         // 清除token
            //         localStorage.removeItem('token');
            //         store.commit('loginSuccess', null);
            //         // 跳转登录页面，并将要浏览的页面fullPath传过去，登录成功后跳转需要访问的页面
            //         setTimeout(() => {
            //             router.replace({
            //                 path: '/login',
            //                 query: {
            //                     redirect: router.currentRoute.fullPath
            //                 }
            //             });
            //         }, 1000);
            //         break;
            //
            //     // 404请求不存在
            //     case 404:
            //         Toast({
            //             message: '网络请求不存在',
            //             duration: 1500,
            //             forbidClick: true
            //         });
            //         break;
            //     // 其他错误，直接抛出错误提示
            //     default:
            //         Toast({
            //             message: error.response.data.message,
            //             duration: 1500,
            //             forbidClick: true
            //         });
            // }
        }
        return Promise.reject(error.response);
    }
);

/**
 * 请求头带上token
 */
async function mergeToken() {
    console.warn(444)
    const token = await AsyncStorage.getItem('token');
    console.warn(555)
    console.warn(token)
    let options = {
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            "Authorization": 'Bearer ' + Global.ACCESS_TOKEN,
            "AppAuthorization": token
        }
    };
    return options;
}

async function mergeFormToken() {
    const token = null;
    let options = {
        headers: {
            "Content-type": "multipart/form-data"
        }
    };
    if (token != null) {
        options = {
            headers: {
                "Authorization": token,
                "Content-type": "multipart/form-data"
            }
        }
    }
    return options;
}

export async function get(url, params) {
    const options = await mergeToken();
    return instance.request({
        method: 'GET',
        url: url,
        params: params,
        ...options
    });
}

export async function post(url, params) {
    const options = await mergeToken();
    return instance.request({
        method: 'POST',
        data: params ? qs.stringify(params) : null,
        url: url,
        ...options
    });
}

export async function postJson2(url, json) {
    const options = await mergeToken();
    return instance.request({
        method: 'POST',
        data: json ? json : null,
        url: url,
        ...options
    });
}

/**
 *
 * @param url 请求地址
 * @param json 数据
 * @returns {Promise<AxiosPromise<any>>}
 */
export async function postForm(url, json) {
    const options = await mergeFormToken();
    return instance.request({
        method: 'POST',
        data: json ? json : null,
        url: url,
        ...options
    });
}


export async function postJson(url, params) {
    const options = await mergeToken();
    console.warn(333)
    let json = {
        furl: url,
        ...params
    };
    return instance.request({
        method: 'POST',
        data: json,
        url: Global.REQUEST_BASE_QUERY_URL,
        ...options
    });
}

/**
 * 由于postJson方法无法有效处理请求转发
 * 添加此方法处理FormData对象使后台接收
 * @param url
 * @param formData
 * @returns {Promise<AxiosPromise<any>>}
 */
export async function postFormData(url, formData) {
    const options = await mergeToken();
    return instance.request({
        method: 'POST',
        data: formData ? formData : null,
        url: url,
        ...options
    });
}
