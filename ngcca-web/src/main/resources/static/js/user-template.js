//初始化常量
const TEMPLATE_INSTANCE_API = "/v1/templates/instance";
const TEMPLATE_INSTANCE_LIST_VIEWS = "/user/template-instance?action=list";

let intervalId;

let instanceYamlCodeMirror;
let instanceIngressYamlCodeMirror;
$(function () {
    toastr.options = {
        "timeOut": "3000"
    };
    intervalId = setInterval('templateinstancesrefresh()', 5000);

    //instance yaml
    instanceYamlCodeMirror = CodeMirror.fromTextArea(document.getElementById("codemirror-instance-yaml"), {
        mode: "yaml",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });
    //instance ingress yaml
    instanceIngressYamlCodeMirror = CodeMirror.fromTextArea(document.getElementById("codemirror-instance-ingress-yaml"), {
        mode: "yaml",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });

    //tooltip
    $('#instance-msg-tooltip').tooltip();
});


const swal = Swal.mixin({
    customClass: {
        confirmButton: 'btn btn-success',
        cancelButton: 'btn btn-danger'
    },
    buttonsStyling: false
})

function showtemplateinstanceyaml(e) {
    let id = $(e).data("instance-id");
    $('#modal-instance-yaml').modal('show');
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

function showtemplateinstanceingressyaml(e) {
    let id = $(e).data("instance-id");
    $('#modal-instance-ingress-yaml').modal('show');
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
function templateinstancesrefresh() {
    $('#template-instance-list-fragment').load(TEMPLATE_INSTANCE_LIST_VIEWS, function () {

    });
}

//user template instance delete
function deletetemplateinstance(e) {
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
                    $('#template-instance-list-fragment').load(TEMPLATE_INSTANCE_LIST_VIEWS, function () {

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