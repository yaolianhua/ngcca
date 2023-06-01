//初始化常量
const TEMPLATE_INSTANCE_API = "/v1/templates/instance";

$(function () {
    toastr.options = {
        "timeOut": "5000"
    };
});

function ok(response) {
    console.log(response);
    toastr.success('操作成功!')
}

function fail(error) {
    console.log(error);
    toastr.error('操作失败[' + error.response.data.message + ']');
}

//deploy template instance
function instanceDeploy(name) {
    axios.post(TEMPLATE_INSTANCE_API + "?template=" + name)
        .then(response => {
            window.location.href = "/template/instances";
        })
        .catch(error => {
            fail(error);
        });
}