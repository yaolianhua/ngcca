//初始化常量
const TEMPLATE_INSTANCE_API = "/v1/templates/instance";

$(function () {
    toastr.options = {
        "timeOut": "5000"
    };
    //Initialize Select2 Elements
    $('.select2').select2()
    //Initialize Select2 Elements
    $('.select2bs4').select2({
        theme: 'bootstrap4'
    })
});

//deploy template instance
function deploytemplate(e) {
    $(e).disable = true;
    let name = $(e).data("definition-name");
    $('#modal-select-cluster').modal("show");
    $('#selected-template').val(name);
}

function submittemplatedeploy() {
    $('#modal-select-cluster').modal("hide");
    let clusterid = $('#selected-template-cluster').val();
    let template = $('#selected-template').val();

    alertInfo(template + "已创建");

    axios.post(TEMPLATE_INSTANCE_API + "?template=" + template + "&clusterId=" + clusterid)
        .then(response => {
            window.location.href = "/user/template-instance";
        })
        .catch(error => {
            fail(error);
        });
}