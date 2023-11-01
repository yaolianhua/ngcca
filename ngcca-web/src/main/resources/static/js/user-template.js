//初始化常量
const TEMPLATE_INSTANCE_API = "/v1/templates/instance";
const USER_TEMPLATE_LIST_VIEWS = "/user/template-instance?action=list";

let intervalId;

let instanceYamlCodeMirror;
let instanceIngressYamlCodeMirror;
$(function () {
    toastr.options = {
        "timeOut": "3000"
    };
    intervalId = setInterval('instances()', 5000);

    //instance yaml
    instanceYamlCodeMirror = CodeMirror.fromTextArea(document.getElementById("codemirror-template-instance-yaml"), {
        mode: "yaml",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });
    //instance ingress yaml
    instanceIngressYamlCodeMirror = CodeMirror.fromTextArea(document.getElementById("codemirror-template-instanceIngress-yaml"), {
        mode: "yaml",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });

    //tooltip
    $('#instance-msg').tooltip();
});


const swal = Swal.mixin({
    customClass: {
        confirmButton: 'btn btn-success',
        cancelButton: 'btn btn-danger'
    },
    buttonsStyling: false
})

function instanceYaml(e) {
    let id = $(e).data("instance-id");
    $('#modal-template-instance-yaml').modal('show');
    axios.get(TEMPLATE_INSTANCE_API + "/" + id)
        .then(response => {
            // Populate data into table
            instanceYamlCodeMirror.setValue(response.data.yaml)
            instanceYamlCodeMirror.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function instanceIngressYaml(e) {
    let id = $(e).data("instance-id");
    $('#modal-template-instanceIngress-yaml').modal('show');
    axios.get(TEMPLATE_INSTANCE_API + "/" + id)
        .then(response => {
            // Populate data into table
            instanceIngressYamlCodeMirror.setValue(response.data.ingress)
            instanceIngressYamlCodeMirror.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

//user template instance list
function instances() {
    $('#user-instance-fragment').load(USER_TEMPLATE_LIST_VIEWS, function () {

    });
}

//user template instance delete
function instanceDelete(e) {
    let id = $(e).data("instance-id");
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