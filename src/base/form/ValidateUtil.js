/**
 * 表单数据验证工具类
 * @author:czq
 * @date:2021-1-18
 */

export class ValidateUtil {

    /**
     * 非空验证
     * @param value
     * @returns {string|null}
     */
    static isNotNull(value) {
        if (!value || value.length === 0) {
            return '不可为空';
        }
        return null;
    }

    /**
     * 验证密码强度
     * @param value
     * @returns {string}
     */
    static password(value) {
        let reg = new RegExp("^[0-9A-Za-z]{8,}$", "i");
        if (!reg.test(value)) {
            return "格式错误或强度不足";
        }
    }

    /**
     * 验证整数
     * @param value
     * @returns {string|null}
     */
    static isInteger(value) {
        if (value && value.length > 0) {
            let reg = new RegExp("^[0-9]*$", "g");
            if (!reg.test(value)) {
                return "应输入非负整数";
            }
        }
        return null;
    }

    /**
     * 验证正整数
     * @param value
     * @returns {string|null}
     */
    static isPositiveInteger(value) {
        if (value && value.length > 0) {
            let reg = new RegExp(/^([1-9]+[0-9]*)?$/);
            if (!reg.test(value)) {
                return "应输入正整数";
            }
        }
        return null;
    }

    /**
     * 验证数字
     * @param value
     * @returns {string|null}
     */
    static isNumber(value) {
        if (value && value.length > 0) {
            let reg = new RegExp(/^-?[0-9]*(\.[0-9]+)?$/);
            if (!reg.test(value)) {
                return "应为有效数字";
            }
        }
        return null;
    }

    /**
     * 验证正数
     * @param value
     * @returns {string|null}
     */
    static isPositiveNumber(value) {
        if (value && value.length > 0) {
            let reg1 = new RegExp(/^[1-9][0-9]*(\.[0-9]+)?$/);
            //0.x小数
            let reg2 = new RegExp(/^0\.[1-9]+[0-9]*$/);
            //0.000..x小数
            let reg3 = new RegExp(/^0\.0+[1-9]+[0-9]*$/);
            if (!reg1.test(value) && !reg2.test(value) && !reg3.test(value)) {
                return "应输入正数";
            }
        }
        return null;
    }

    /**
     * 联系电话(手机/电话皆可)验证
     * @param phoneNum
     * @returns {null|string|boolean}
     */
    static isTel(phoneNum) {
        let isEmpty = function (value) {
            if (/^\s*$/.test(value) === true) {
                return true;
            }
            return value === null || value === '' || value === undefined;
        };
        if (isEmpty(phoneNum)) {
            return "不可为空";
        }
        let result1 = /^((\(\d{2,3}\))|(\d{3}\-))?((1\d{10}))$/.test(phoneNum);
        let result2 = /^((\(\d{2,3}\))|(\d{3}\-))?(\(0\d{2,3}\)|0\d{2,3}-)?[1-9]\d{6,7}(\-\d{1,4})?$/.test(phoneNum);
        if (result1 || result2) {
            return null;
        } else {
            return "格式不正确";
        }
    }

    /**
     * 身份证校验
     * @param value
     * @returns {string|null}
     */
    static isIdCardNo(value) {
        let city = {
            11: "北京", 12: "天津", 13: "河北", 14: "山西", 15: "内蒙古",
            21: "辽宁", 22: "吉林", 23: "黑龙江", 31: "上海", 32: "江苏",
            33: "浙江", 34: "安徽", 35: "福建", 36: "江西", 37: "山东", 41: "河南",
            42: "湖北", 43: "湖南", 44: "广东", 45: "广西", 46: "海南", 50: "重庆",
            51: "四川", 52: "贵州", 53: "云南", 54: "西藏", 61: "陕西", 62: "甘肃",
            63: "青海", 64: "宁夏", 65: "新疆", 71: "台湾", 81: "香港", 82: "澳门", 91: "国外"
        };

        // 判断是否为空
        let isEmpty = function (card) {
            return /^\s*$/.test(card)
        };
        //检查号码是否符合规范，包括长度，类型
        let isCardNo = function (card) {
            if (isEmpty(card)) {
                return false;
            }
            //身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
            let reg = /(^\d{15}$)|(^\d{17}(\d|X)$)/;
            return reg.test(card) !== false;

        };

        //取身份证前两位,校验省份
        let checkProvince = function (card) {
            if (isEmpty(card)) {
                return false;
            }
            let province = card.substr(0, 2);
            return city[province] != undefined;
        };

        //检查生日是否正确
        let checkBirthday = function (card) {
            if (isEmpty(card)) {
                return false;
            }
            let len = card.length;
            //身份证15位时，次序为省（3位）市（3位）年（2位）月（2位）日（2位）校验位（3位），皆为数字
            if (len == 15) {
                let re_fifteen = /^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/;
                let arr_data = card.match(re_fifteen);
                let year = arr_data[2];
                let month = arr_data[3];
                let day = arr_data[4];
                let birthday = new Date('19' + year + '/' + month + '/' + day);
                return verifyBirthday('19' + year, month, day, birthday);
            }
            //身份证18位时，次序为省（3位）市（3位）年（4位）月（2位）日（2位）校验位（4位），校验位末尾可能为X
            if (len == 18) {
                let re_eighteen = /^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/;
                let arr_data = card.match(re_eighteen);
                let year = arr_data[2];
                let month = arr_data[3];
                let day = arr_data[4];
                let birthday = new Date(year + '/' + month + '/' + day);
                return verifyBirthday(year, month, day, birthday);
            }
            return false;
        };

        //校验日期
        let verifyBirthday = function (year, month, day, birthday) {
            let now = new Date();
            let now_year = now.getFullYear();
            //年月日是否合理
            if (birthday.getFullYear() == year && (birthday.getMonth() + 1) == month && birthday.getDate() == day) {
                //判断年份的范围（3岁到150岁之间)
                let time = now_year - year;
                return time >= 3 && time <= 150;
            }
            return false;
        };

        //校验位的检测
        let checkParity = function (card) {
            if (isEmpty(card)) {
                return false;
            }
            //15位转18位
            card = changeFifteenToEighteen(card);
            let len = card.length;
            if (len == 18) {
                let arrInt = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
                let arrCh = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'];
                let cardTemp = 0, i, valNum;
                for (i = 0; i < 17; i++) {
                    cardTemp += card.substr(i, 1) * arrInt[i];
                }
                valNum = arrCh[cardTemp % 11];
                return valNum == card.substr(17, 1);

            }
            return false;
        };

        //15位转18位身份证号
        let changeFifteenToEighteen = function (card) {
            if (isEmpty(card)) {
                return 0;
            }
            if (card.length == 15) {
                let arrInt = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
                let arrCh = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'];
                let cardTemp = 0, i;
                card = card.substr(0, 6) + '19' + card.substr(6, card.length - 6);
                for (i = 0; i < 17; i++) {
                    cardTemp += card.substr(i, 1) * arrInt[i];
                }
                card += arrCh[cardTemp % 11];
                return card;
            }
            return card;
        };

        let card = value.toUpperCase();
        //校验长度，类型
        if (!isCardNo(card)) {
            return "身份证长度不正确";
        }
        //检查省份
        if (!checkProvince(card)) {
            return "身份证内省份信息不正确";
        }
        //校验生日
        if (!checkBirthday(card)) {
            return "身份证内生日信息不正确";
        }
        //检验位的检测
        if (!checkParity(card)) {
            return "身份证校验位不正确";
        }
        return null;
    }
}
