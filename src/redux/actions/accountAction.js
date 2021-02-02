'use strict';

import * as types from '../constants/accountTypes';

// 登录用户信息接口 根据返回结果来划分action属于哪个type,然后返回对象,给reducer处理
/**
 * 每当store.dispatch发送过来一个新的 Action，就会自动调用 Reducer，得到新的 State。
 * @returns {function(*)}
 */

export function setAccount(data) {
    return dispatch => dispatch({
        type: types.SET_ACCOUNT,
        token: data.token,
        user: data.user,
        dictionary: data.dict,
        dwssxjb: data.dwssxjb
    })
}

/**
 * 清除账户信息
 * @returns {function(*): *}
 */
export function clearAccount() {
    return dispatch => dispatch({
        type: types.CLEAR_ACCOUNT
    })
}
