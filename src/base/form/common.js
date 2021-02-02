/**
 * 获取特定格式的年月日
 * @param time(Long)
 * @returns {string}
 */
export function toYMDBYLONG(time) {
    let date;
    try {
        date = new Date(time);
    } catch (e) {
    }
    if (!date)
        return null;
    else
        return date.getFullYear() + "年" + (date.getMonth() + 1) + "月" + date.getDate() + "日";
}

/**
 * 获取特定格式的年月日
 * @param date(Long)
 * @returns {string}
 */
export function toDDMMMYYYY(date) {
    let mths = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
    let d = new Date(date);
    let dd = d.getDate() < 10 ? "0" + d.getDate() : d.getDate().toString();
    let mm = mths[d.getMonth()];
    let yyyy = d.getFullYear().toString();
    return yyyy + "/" + mm + "/" + dd;
}

/**
 * 使用递归的方式实现数组、对象的深拷贝
 * @param obj
 * @returns {*}
 */
export function deepClone(obj) {
    //判断拷贝的要进行深拷贝的是数组还是对象，是数组的话进行数组拷贝，对象的话进行对象拷贝
    var objClone = Array.isArray(obj) ? [] : {};
    //进行深拷贝的不能为空
    if (obj && typeof obj === "object") {
        for (let key in obj) {
            if (obj.hasOwnProperty(key)) {
                if (obj[key] && typeof obj[key] === "object") {
                    objClone[key] = deepClone(obj[key]);
                } else {
                    objClone[key] = obj[key];
                }
            }
        }
    }
    return objClone;
}

/**
 * 标量数组比较，简单结构
 * @param array1
 * @param array2
 * @returns {boolean}
 */
export function scalarArrayEquals(array1, array2) {
    array1 = array1.sort();
    array2 = array2.sort();
    return array1.length == array2.length && array1.every(function (v, i) {
        return v === array2[i]
    });
}

/**
 * 判断对象是否为数组
 * @param value
 * @returns {*}
 */
export function isArrayFn(value) {
    if (typeof Array.isArray === "function") {
        return Array.isArray(value);
    } else {
        return Object.prototype.toString.call(value) === "[object Array]";
    }
}

/**
 *
 * @param inputTime
 * @param mode
 * @returns {*}
 */
export function formatDateTime(inputTime, mode) {
    if (!inputTime) {
        return null;
    }
    let date = new Date(inputTime);
    let y = date.getFullYear();
    let m = date.getMonth() + 1;
    m = m < 10 ? ('0' + m) : m;
    let d = date.getDate();
    d = d < 10 ? ('0' + d) : d;
    let h = date.getHours();
    h = h < 10 ? ('0' + h) : h;
    let minute = date.getMinutes();
    minute = minute < 10 ? ('0' + minute) : minute;
    if (mode == "date") {
        return y + '-' + m + '-' + d;
    } else if (mode == "time") {
        return h + ':' + minute + ":00";
    }
};

/**
 * 解析并返回json
 * @param date
 * @returns {string}
 */
export function parseJson(params) {
    let entity = {};
    for (let i in params) {
        if (Array.isArray(params[i]))
            entity[i] = parseArray(params[i]);
        else
            entity[i] = params[i];
    }
    return entity;
}

function parseArray(arr) {
    let result = [];
    for (let i in arr) {
        result[i] = arr[i];
    }
    return result;
}


/**
 * 根据数据库对附件的分组配置，实现分组上传的配置
 * @param fileOptions
 * @param navigation
 * @param t
 */
export function parseFileOption(fileOptions, navigation, t) {
    let result = {};
    result.rows = {};
    result.options = {};
    result.values = {};
    for (let i in fileOptions) {
        let limit = fileOptions[i].maxnum;
        let isMust = fileOptions[i].minnum;
        let code = fileOptions[i].code;
        let name = fileOptions[i].name;
        result.rows[code] = isMust ? t.list(t.String) : t.maybe(t.list(t.String));
        result.values[code] = [];
        result.options[code] = {};
        result.options[code].label = name;
        result.options[code].navigation = navigation;
        result.options[code].limit = limit ? limit : 4;
    }
    return result;
}


export function parseOptionForUpload(fileOptions, navigation, t) {
    let result = {};
    result.rows = {};
    result.options = {};
    result.values = {};
    for (let i in fileOptions) {
        let limit = fileOptions[i].maxnum;
        let code = fileOptions[i].code;
        let name = fileOptions[i].name;
        result.rows[code] = t.maybe(t.list(t.String));
        result.values[code] = [];
        result.options[code] = {};
        result.options[code].label = name;
        result.options[code].navigation = navigation;
        result.options[code].limit = limit ? limit : 4;
    }
    return result;
}

export function parseFileValue(pics) {
    let values = {};
    for (let pic in pics) {
        let item = pics[pic];
        if (!values[item.code]) values[item.code] = [];
        values[item.code].push(item);
    }
    return values;
}

export function isTel(val) {
    if (!val) return false;
    let result1 = /^((\(\d{2,3}\))|(\d{3}\-))?((1\d{10}))$/.test(val);
    let result2 = /^((\(\d{2,3}\))|(\d{3}\-))?(\(0\d{2,3}\)|0\d{2,3}-)?[1-9]\d{6,7}(\-\d{1,4})?$/.test(val);
    let result3 = /\b\d{11,12}\b/.test(val);   //11位或12位
    return result1 || result2 || result3;
}

export function getBirthdayFromIdCard(idCard) {
    let birthday = "";
    if (idCard) {
        if (idCard.length === 15) {
            birthday = "19" + idCard.substr(6, 6);
        } else if (idCard.length === 18) {
            birthday = idCard.substr(6, 8);
        }
        birthday = birthday.replace(/(.{4})(.{2})/, "$1-$2-");
    }
    return birthday;
}


export function isSg(v) {
    if (!v)
        return true;
    if (v < 10 || v >= 1000) {
        return false;
    } else {
        return true;
    }
    // let a = v.split(".")[0];
    // let b = v.split(".")[1] ? v.split(".")[1] : "0";
    // return (2 <= a.length && a.length <= 3) && (0 <= b.length && b.length <=2) && (/^[0-9]*[1-9][0-9]*$/.test(a)) && v[v.length - 1] != ".";
}

export function formatSize(b) {
    //大于1M
    if (b >= 1000000) {
        return (b / 1000000).toFixed(2) + "M";
        //大于1K
    } else if (b >= 1000) {
        return (b / 1000).toFixed(2) + "K";
        //大于1B
    } else {
        return b + "B";
    }
}


export function randomString(len) {
    len = len || 32;
    let $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';
    /****默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1****/
    let maxPos = $chars.length;
    let pwd = '';
    for (let i = 0; i < len; i++) {
        pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
    }
    return pwd;
}

/**
 * 获取UUID 32位
 * @returns {*}
 */
export function guid() {
    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }

    return (S4() + S4() + S4() + S4() + S4() + S4() + S4() + S4());
}

/**
 * 判断字符串是否为空
 * @param str
 * @returns {boolean}
 */
export function isEmpty(str) {
    return str === null || str === '' || str === undefined;
}