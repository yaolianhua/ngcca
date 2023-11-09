//初始化常量
const APPLICATION_API = "/v1/applications/instance";
const USER_APPLICATION_LIST_VIEWS = "/user/applications?action=list";

let intervalId;

$(function () {
    toastr.options = {
        "timeOut": "3000"
    };
    intervalId = setInterval('applications()', 5000);

    //tooltip
    $('#application-tooltip-msg').tooltip();

    //Initialize Select2 Elements
    $('.select2').select2();
    //Initialize Select2 Elements
    $('.select2bs4').select2({
        theme: 'bootstrap4'
    });

    bsCustomFileInput.init();
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

function createapplication() {
    let data = {
        "clusterId": null,
        "name": null,
        "enableIngressAccess": false,
        "serverPort": 0,
        "source": {
            "origin": null,
            "runtime": null,
            "url": null,
            "gitBranch": null,
            "gitSubmodule": null,
            "startArgs": null,
            "startOptions": null
        },
        "replicas": 1,
        "envs": {},
        "envStrings": null
    };
    let value = $('#application-create-form').serializeArray();
    $.each(value, function (index, item) {
        data[item.name] = item.value;
    });
    let source_origin = $('#source-origin').val();
    let source_url = $('#http-url').val();
    let source_gitBranch = $('#application-git-branch').val();
    let source_startArgs = $('#application-startargs').val();
    let source_startOptions = $('#application-startoptions').val();
    let enableIngressAccess = $('#enable-ingress-access').val();
    //
    data.source.origin = source_origin;
    data.source.url = source_url;
    data.source.gitBranch = source_gitBranch;
    data.source.startArgs = source_startArgs;
    data.source.startOptions = source_startOptions;
    data.enableIngressAccess = enableIngressAccess;

    console.log(data);
    // Send a POST request
    // axios({
    //     method: 'post',
    //     url: USER_API,
    //     data: data
    // }).then(function (response) {
    //     $('#modal-new-user').modal('hide');
    //     $('#users-fragment').load(USER_LIST_VIEWS, function () {
    //         userPaging();
    //     });
    //
    //     ok(response);
    // }).catch(function (error) {
    //     fail(error);
    // });
}