//初始化常量
const TEMPLATE_INSTANCE_API = "/v1/templates/instance";
const USER_TEMPLATE_LIST_VIEWS = "/template/instances?action=list";
const USER_TEMPLATE_DETAIL_VIEWS = "/template/instances?action=detail&id=";

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