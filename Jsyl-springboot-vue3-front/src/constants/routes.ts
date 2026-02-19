/**
 * 路由路径常量模块
 * 
 * @module constants/routes
 */

// ==================== 基础路由 ====================

/** 根路径 */
export const ROUTE_ROOT = '/';

/** 登录页面路径 */
export const ROUTE_LOGIN = '/login';

/** 注册页面路径 */
export const ROUTE_REGISTER = '/register';

/** 首页路径 */
export const ROUTE_HOME = '/home';

// ==================== 首页子路由 ====================

/** 控制台路径 */
export const ROUTE_HOME_MAIN = `${ROUTE_HOME}/main`;

/** 订单中心路径 */
export const ROUTE_HOME_ORDER_CENTER = `${ROUTE_HOME}/ordercenter`;

/** 个人中心路径 */
export const ROUTE_HOME_PROFILE = `${ROUTE_HOME}/profile`;

// ==================== 路由名称 ====================

/** 登录路由名称 */
export const ROUTE_NAME_LOGIN = 'Login';

/** 注册路由名称 */
export const ROUTE_NAME_REGISTER = 'Register';

/** 首页路由名称 */
export const ROUTE_NAME_HOME = 'Home';

/** 控制台路由名称 */
export const ROUTE_NAME_HOME_MAIN = 'HomeMain';

/** 订单中心路由名称 */
export const ROUTE_NAME_ORDER_CENTER = 'OrderCenter';

/** 个人中心路由名称 */
export const ROUTE_NAME_PROFILE = 'Profile';
