//初始化常量

let intervalId;

$(function () {
    toastr.options = {
        "timeOut": "3000"
    };
    intervalId = setInterval('applications()', 5000);

    //tooltip
    $('#application-tooltip-msg').tooltip();

    bsCustomFileInput.init();
});

function togglediv() {
    let value = $('#select-source-origin').val();
    if (value === "JAR" || value === "WAR") {
        $('#search-repository-div').css("display", "none");
        $('#upload-package-div').css("display", "block");
    }
    if (value === "IMAGE") {
        $('#upload-package-div').css("display", "none");
        $('#search-repository-div').css("display", "block");
    }

    if (value === "SOURCE_CODE") {
        $('#upload-package-div').css("display", "none");
        $('#search-repository-div').css("display", "none");
    }
}

function searchrepositories(e) {
    let url = $(e).data("registry-endpoint");
    let username = $(e).data("registry-username");
    let password = $(e).data("registry-password");
    let key = $('#search-repository').val();
    let requestUri = REGISTRY_SEARCH_API + "/harbor/repositories?registry=" + url + "&username=" + username + "&password=" + password + "&q=" + key + "&page=1&pageSize=30";

    $('#modal-select-repository').modal('show');
    axios.get(requestUri)
        .then(response => {
            $('#selected-repository').empty();
            $('#selected-repository').append($('<option>', {
                disabled: true,
                selected: true,
                text: "请选择"
            }));
            $.each(response.data.data, function (index, item) {
                $('#selected-repository').append($('<option>', {
                    value: item.name,
                    text: item.name
                }));
            })
        })
        .catch(error => {
            fail(error);
        });
}

function triggersearchrepositorytag(e) {
    let url = $('#search-repository-button').data("registry-endpoint");
    let username = $('#search-repository-button').data("registry-username");
    let password = $('#search-repository-button').data("registry-password");
    let repositoryname = $(e).val();
    let requestUri = REGISTRY_SEARCH_API + "/harbor/tags?registry=" + url + "&username=" + username + "&password=" + password + "&repository=" + repositoryname + "&page=1&pageSize=30";
    axios.get(requestUri)
        .then(response => {
            $('#selected-repository-tag').empty();
            $('#selected-repository-tag').append($('<option>', {
                disabled: true,
                selected: true,
                text: "请选择"
            }));
            $.each(response.data.data, function (index, item) {
                $('#selected-repository-tag').append($('<option>', {
                    value: item.imageName,
                    text: item.tag
                }));
            })
        })
        .catch(error => {
            fail(error);
        });
}

function choosedrepository() {
    $('#modal-select-repository').modal('hide');
    let imagename = $('#selected-repository-tag').val();
    $('#http-url').val(imagename);
}
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
    $('#application-list-fragment').load(USER_APPLICATION_LIST_VIEWS + "?action=list", function () {

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
                    applications();
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

function stepperprevious() {
    stepper.previous();
}

function steppernext(part) {
    if ("basic" === part) {
        if (isEmpty($('#application-name').val())) {
            alertWarn("请输入应用名称");
            return;
        }
        if (isEmpty($('#selected-cluster').val())) {
            alertWarn("请选择集群");
            return;
        }

        stepper.next();
    }

    if ("source" === part) {
        if (isEmpty($('#select-source-origin').val())) {
            alertWarn("请选择构建源");
            return;
        }
        if (isEmpty($('#http-url').val())) {
            alertWarn("构建源url为空");
            return;
        }

        stepper.next();
    }
}

function uploadjavapackage() {
    let fileinput = $('#upload-java-package')[0];
    let file = fileinput.files[0];
    if (isEmpty(file)) {
        alertWarn("请选择上传文件");
        return;
    }
    let data = new FormData();
    data.append('file', file);

    $('#modal-loading').modal('show');
    //Send a POST request
    axios({
        method: 'post',
        url: FILE_UPLOAD_API + "?bucket=" + CURRENT_USERNAME + "&public=true",
        data: data
    }).then(function (response) {
        $('#modal-loading').modal('hide');
        $('#http-url').val(response.data.data);
        ok(response);
    }).catch(function (error) {
        fail(error);
    });
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
    //basic
    let applicationname = $('#application-name').val();
    let clusterid = $('#selected-cluster').val();
    let enableIngressAccess = $('#enable-ingress-access').prop('checked');
    let serverport = $('#application-serverport').val();
    let replicas = $('#application-replicas').val();
    //
    data.name = applicationname;
    data.clusterId = clusterid;
    data.enableIngressAccess = enableIngressAccess;
    data.serverPort = serverport;
    data.replicas = replicas;

    //source origin
    let sourceorigin = $('#select-source-origin').val();
    let sourceurl = $('#http-url').val();
    let sourcegitbranch = $('#application-git-branch').val();
    let sourcegitsubmodule = $('#application-git-submodule').val();
    let sourcestartargs = $('#application-startargs').val();
    let sourcestartoptions = $('#application-startoptions').val();

    //
    data.source.origin = sourceorigin;
    data.source.url = sourceurl;
    data.source.runtime = "JAVA17";
    data.source.gitSubmodule = sourcegitsubmodule;
    data.source.gitBranch = sourcegitbranch;
    data.source.startArgs = sourcestartargs;
    data.source.startOptions = sourcestartoptions;

    //advanced
    let envstring = $('#application-envs').val();
    data.envStrings = envstring;

    console.log(data);
    //Send a POST request
    axios({
        method: 'post',
        url: APPLICATION_API,
        data: data
    }).then(function (response) {
        ok(response);
        applications();
    }).catch(function (error) {
        fail(error);
    });
}