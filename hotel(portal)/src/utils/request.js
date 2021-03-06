import axios from "axios";

//创建axios实例
const instance = axios.create({
    baseURL:"http://106.52.219.171:8102",
    //baseURL:"http://106.52.219.171/tomato/hotel/api",
    // timeout:15000, 请求超时设置
})

//请求拦截
instance.interceptors.request.use(
    function (config) {
        if (localStorage.getItem('Token')) {
            config.headers.Authorization = localStorage.getItem('Token');
        } else if(/auth/.test(config.url)) { // 通过正则匹配请求url是否有auth，为true则不需要携带token
            return config
        } else{ // 请求为携带token，且不是非权限接口，则提示无法获取token
            console.log('token无法获取');
        }
        return config
    },
    function (err) {
        return Promise.reject(err)
    }
)

//响应拦截
instance.interceptors.response.use(
    function (res) {
        return res
    },
    function (err) {
        return Promise.reject(err)
    }
)

//封装get请求
export function get(url,params) {
    return instance.get(url,{
        params
    })
}

//封装post请求
export function post(url,data,config) {
    return instance.post(url,data,config)
}