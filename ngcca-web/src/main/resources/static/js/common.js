axios.defaults.baseURL = $('#server-endpoint').data('server-endpoint');
// Request interceptors for API calls
axios.interceptors.request.use(
    config => {
        config.headers['Authorization'] = `Bearer ${getAuthorization()}`;
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

//Get authorization from cookies
function getAuthorization() {
    let strcookie = document.cookie;//获取cookie字符串
    let arrcookie = strcookie.split("; ");//分割
    //遍历匹配
    for (let i = 0; i < arrcookie.length; i++) {
        let arr = arrcookie[i].split("=");
        if (arr[0] === "authorization") {
            return arr[1];
        }
    }
    return "";
}