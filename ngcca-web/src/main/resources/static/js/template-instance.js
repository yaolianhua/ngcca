//初始化常量
const TEMPLATE_INSTANCE_API = "/v1/templates/instance";

$(function () {
    toastr.options = {
        "timeOut": "5000"
    };
});

//deploy template instance
function deploytemplate(e) {
    $(e).disable = true;
    let name = $(e).data("definition-name");
    animate_swal.fire({
        icon: 'info',
        html: name + "创建成功"
    })
    axios.post(TEMPLATE_INSTANCE_API + "?template=" + name)
        .then(response => {
            window.location.href = "/user/template-instance";
        })
        .catch(error => {
            fail(error);
        });
}