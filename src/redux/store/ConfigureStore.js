'use strict';
import {applyMiddleware, createStore} from 'redux';
import thunkMiddleware from 'redux-thunk';
import rootReducer from '../reducers/index';

const createStoreWithMiddleware = applyMiddleware(thunkMiddleware)(createStore);

/**
 * 创建了一个Store。createStore 有两个参数，Reducer 和 initialState。  将reducer的数据更新拿过来，然后如果没有更新的话就传一个默认值。
 * @param initialState
 * @returns {Store<StoreEnhancer<S>>}
 */
export default function configureStore(initialState) {
  return createStoreWithMiddleware(rootReducer, initialState);
}
