import {AsyncStorage} from "react-native";

/**
 * 缓存用户信息
 * @param data
 */
export async function storeAccount(data) {
    try {
        let arr = [
            ["token", data.token],
            ["user", JSON.stringify(data.user)]
        ];
        await AsyncStorage.multiSet(arr);
    } catch (e) {
        console.warn('Error: ', e);
    }
}
