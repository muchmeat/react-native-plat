'use strict';
import * as types from '../constants/accountTypes';

let init = {
    token: null,
    user: null,
    dictionary: null,
    dwssxjb: null
};
export default function accountReducer(state = init, action) {
    switch (action.type) {
        case types.SET_ACCOUNT:
            return {
                ...state,
                token: action.token,
                user: action.user,
                dictionary: action.dictionary,
                dwssxjb: action.dwssxjb
            };
        case types.CLEAR_ACCOUNT:
            return {
                ...state,
                token: null,
                user: null,
                dictionary: null,
                dwssxjb: null
            };
        case types.GET_ACCOUNT_TOKEN:
            return {
                ...state,
                token: action.token
            };
        case types.GET_ACCOUNT_USER:
            return {
                ...state,
                user: action.user
            };
        default:
            return state;
    }
}

