import React from 'react';
import {
    View,
    Text,
    TouchableOpacity
} from 'react-native';
import {createAppContainer, createSwitchNavigator} from 'react-navigation';
import {createStackNavigator} from 'react-navigation-stack';
import {createBottomTabNavigator} from 'react-navigation-tabs';
import Svg from 'react-native-svg';
import IconLib from './resources/svg/IconLib';


import Splash from './pages/Splash';
import Login from './pages/Login';
import Example from './pages/project/jwyy/example';
import Example2 from './pages/project/jwyy/example2';
import Example3 from './pages/project/jwyy/example3';


const Tabs = createBottomTabNavigator({
    'Home': {
        screen: Example,
        navigationOptions: {
            tabBarLabel: '首页', // tabBar显示的文字
            tabBarIcon: ({focused}) => { // tabBar显示的图标
                if (focused) {
                    return <Svg height={18} width={18} viewBox="0 0 1024 1024">{IconLib.IC_MAINPAGE2}</Svg>
                } else {
                    return <Svg height={18} width={18} viewBox="0 0 1024 1024">{IconLib.IC_MAINPAGE}</Svg>
                }
            }
        }
    },
    'Page': {
        screen: Example2,
        navigationOptions: {
            tabBarLabel: '通讯录', // tabBar显示的文字
            tabBarIcon: ({focused}) => { // tabBar显示的图标
                if (focused) {
                    return <Svg height={18} width={18} viewBox="0 0 1024 1024">{IconLib.IC_DWGL1}</Svg>
                } else {
                    return <Svg height={18} width={18} viewBox="0 0 1024 1024">{IconLib.IC_DWGL}</Svg>
                }
            }
        }
    },
    'Component': {
        screen: Example3,
        navigationOptions: {
            tabBarLabel: '我的', // tabBar显示的文字
            tabBarIcon: ({focused}) => { // tabBar显示的图标
                if (focused) {
                    return <Svg height={22} width={22} viewBox="0 0 1024 1024">{IconLib.MY2}</Svg>
                } else {
                    return <Svg height={22} width={22} viewBox="0 0 1024 1024">{IconLib.MY1}</Svg>
                }
            }
        }
    },
    'AnalysisAndJudgment': {
        screen: Example,
        navigationOptions: {
            tabBarLabel: '分析研判', // tabBar显示的文字
            tabBarIcon: ({focused}) => { // tabBar显示的图标
                if (focused) {
                    return <Svg height={22} width={22} viewBox="0 0 1024 1024">{IconLib.FXYP_CHECKED}</Svg>
                } else {
                    return <Svg height={22} width={22} viewBox="0 0 1024 1024">{IconLib.FXYP_UNCHECK}</Svg>
                }
            }
        }
    }
}, {
    headerMode: 'screen',
    initialRouteName: 'Home',
    swipeEnabled: false,
    // tabBarPosition: 'bottom',
    tabBarOptions: {
        activeTintColor: "#4171ff",
        inactiveTintColor: '#999',
        // pressColor: "#26D4FE",   //不使用涟漪效果
        style: {
            height: 51,
            backgroundColor: '#fff',
            elevation: 0,//去除阴影
        },
        indicatorStyle: {
            height: 0, // 不显示indicator
        },
        labelStyle: {
            fontSize: 12,
            marginBottom: 25
        },
        tabStyle: {height: 70},
        iconStyle: {height: 30},
        showIcon: true, // 是否显示图标, 默认为false
        showLabel: true, // 是否显示label
    },
});


//设置嵌入stack的tab Title
Tabs.navigationOptions = ({navigation}) => {
    return setNavigation(navigation, Tabs, 1);
};

const setNavigation = (navigation, tab) => {
    const component = tab.router.getComponentForState(navigation.state);
    if (navigation.state.index === 1) {
        return {
            headerShown: false
        }
    }
    if (typeof component.navigationOptions === 'function') {
        return component.navigationOptions({navigation})
    }
    return component.navigationOptions
};

const routeConfigMap = {
    App: {screen: Tabs}
};
const stackConfig = {
    initialRouteName: 'Tabs', // 默认显示界面
    headerMode: 'screen',       //header的显示模式，值为none时不显示
    // headerTransitionPreset: "fade-in-place",
    navigationOptions: {       //此处设置的navigationOptions属性为所有页面使用，页面可以设置static navigationOptions，将此属性覆盖
                               //右边的button，可在页面上写
                               // headerRight:<View style={{paddingRight:20,height:50,width:60,alignItems:"center"}}><TextCompent style={{flex:1}} onPress={()=>{alert("曹尼玛啊")}}>重中之重</TextCompent></View>,
        headerStyle: {
            backgroundColor: "#4171ff",
            height: 51,
            elevation: 0,
        },
        headerTitleStyle: {
            fontSize: 16,
            color: "#FFF",
        },
        //返回图标颜色
        headerTintColor: '#fff',
        //返回图标按住的样色（MD）
        // headerPressColorAndroid: "transparent",
        //header是否透明，默认false不透明
        // headerTransparent:true
        //返回上一页手势功能，在ios默认true，Android默认false
        // gesturesEnabled:true
        //手势水平垂直滑动多少触发
        // gestureResponseDistance:{
        //     horizontal:25,
        //     vertical:100,
        // }
        //手势滑动的方向
        // gestureDirection:"right-to-left"
        // headerMode:"none"
        //path属性适用于其他app或浏览器使用url打开本app并进入指定页面。path属性用于声明一个界面路径，例如：【/pages/Home】。此时我们可以在手机浏览器中输入：app名称://pages/Home来启动该App，并进入Home界面。
        // path:'../pages/MainPage'
    },
    // transitionConfig: () => ({ //自定义动画效果
    //     screenInterpolator: StackViewStyleInterpolator.forHorizontal,
    // })
};

const AppNavigator = createStackNavigator(routeConfigMap, stackConfig);

const SwitchNavigator = createSwitchNavigator({
    Splash: {screen: Splash},
    Auth: {screen: Login},
    ...routeConfigMap
}, {
    initialRouteName: 'App'
});
export default createAppContainer(SwitchNavigator);
