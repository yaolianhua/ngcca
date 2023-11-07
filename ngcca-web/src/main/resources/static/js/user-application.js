//初始化常量
const APPLICATION_API = "/v1/applications/instance";
const USER_APPLICATION_LIST_VIEWS = "/user/applications?action=list";

let intervalId;
let stepper;

$(function () {
    toastr.options = {
        "timeOut": "3000"
    };
    intervalId = setInterval('applications()', 5000);

    //tooltip
    $('#application-tooltip-msg').tooltip();

    // BS-Stepper Init
    $(document).ready(function () {
        stepper = new Stepper($('.bs-stepper')[0])
    })

    //Initialize Select2 Elements
    $('.select2').select2();
    //Initialize Select2 Elements
    $('.select2bs4').select2({
        theme: 'bootstrap4'
    });
});


const swal = Swal.mixin({
    customClass: {
        confirmButton: 'btn btn-success',
        cancelButton: 'btn btn-danger'
    },
    buttonsStyling: false
})

function showapplicationyaml(e) {
    let id = $(e).data("application-id");
    $('#modal-codemirror-yaml').modal('show');
    axios.get(APPLICATION_API + "/" + id)
        .then(response => {
            // Populate data into table
            codemirror_yaml.setValue(response.data.yaml)
            codemirror_yaml.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function showapplicationingressyaml(e) {
    let id = $(e).data("application-id");
    $('#modal-codemirror-yaml').modal('show');
    axios.get(APPLICATION_API + "/" + id)
        .then(response => {
            // Populate data into table
            codemirror_yaml.setValue(response.data.ingress)
            codemirror_yaml.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

//user template application list
function applications() {
    $('#application-list-fragment').load(USER_APPLICATION_LIST_VIEWS, function () {

    });
}

//user template application delete
function deleteapplication(e) {
    let id = $(e).data("application-id");
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
            axios.delete(APPLICATION_API + "/" + id)
                .then(response => {
                    $('#user-application-fragment').load(USER_APPLICATION_LIST_VIEWS, function () {

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