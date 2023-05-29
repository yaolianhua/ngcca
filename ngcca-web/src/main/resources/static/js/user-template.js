//初始化常量
const TEMPLATE_INSTANCE_API = "/v1/templates/instance";
const USER_TEMPLATE_LIST_VIEWS = "/template/instances?action=list";
const USER_TEMPLATE_DETAIL_VIEWS = "/template/instances?action=detail&id=";
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

let intervalId;

$(function () {
    toastr.options = {
        "timeOut": "3000"
    };
    intervalId = setInterval('instances()', 5000);
});


const swal = Swal.mixin({
    customClass: {
        confirmButton: 'btn btn-success',
        cancelButton: 'btn btn-danger'
    },
    buttonsStyling: false
})

function ok(response) {
    console.log(response);
    toastr.success('操作成功!')
}

function fail(error) {
    console.log(error);
    toastr.error('操作失败[' + error.response.data.message + ']');
}

//user template instance detail view
function instanceDetail(id) {
    clearInterval(intervalId);
    $('#user-instance-fragment').load(USER_TEMPLATE_DETAIL_VIEWS + id, function () {
        // CodeMirror
        CodeMirror.fromTextArea(document.getElementById("codemirror-yaml"), {
            mode: "yaml",
            theme: "monokai"
        });
        CodeMirror.fromTextArea(document.getElementById("codemirror-ingress"), {
            mode: "yaml",
            theme: "monokai"
        });
    });
}

//user template instance list
function instances() {
    $('#user-instance-fragment').load(USER_TEMPLATE_LIST_VIEWS, function () {

    });
}

//user template instance delete
function instanceDelete(id) {
    swal.fire({
        title: '确认删除?',
        text: '删除实例会删除所有实例相关的服务组件，谨慎操作!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            axios.delete(TEMPLATE_INSTANCE_API + "/" + id)
                .then(response => {
                    $('#user-instance-fragment').load(USER_TEMPLATE_LIST_VIEWS, function () {

                    });
                    ok(response);
                })
                .catch(error => {
                    fail(error);
                });
        } else {
            //
        }
    })
}