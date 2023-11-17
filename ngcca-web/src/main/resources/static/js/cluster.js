
function clusteradd() {
    let data = {};
    let value = $('#cluster-add-form').serializeArray();
    $.each(value, function (index, item) {
        data[item.name] = item.value;
    });
    // Send a POST request
    axios({
        method: 'post',
        url: CLUSTER_API,
        data: data
    }).then(function (response) {
        $('#modal-cluster-add').modal('hide');
        $('#cluster-list-fragment').load(CLUSTER_LIST_VIEWS + "?action=list&refresh=true", function () {

        });

        ok(response);
    }).catch(function (error) {
        fail(error);
    });
}

function showclustereditmodal(e) {
    $("#modal-cluster-edit").modal("show");
    let clusterid = $(e).data("cluster-id");
    let clusterName = $(e).data("cluster-name");
    let clusterUrl = $(e).data("cluster-url");
    $("#cluster-id").val(clusterid);
    $("#cluster-name").val(clusterName);
    $("#cluster-url").val(clusterUrl);
}

function clusteredit() {
    let data = {};
    let value = $('#cluster-edit-form').serializeArray();
    $.each(value, function (index, item) {
        data[item.name] = item.value;
    });
    // Send a POST request
    axios({
        method: 'post',
        url: CLUSTER_API,
        data: data
    }).then(function (response) {
        $('#modal-cluster-edit').modal('hide');
        $('#cluster-list-fragment').load(CLUSTER_LIST_VIEWS + "?action=list&refresh=true", function () {

        });

        ok(response);
    }).catch(function (error) {
        fail(error);
    });
}

function clusterdelete(e) {
    let id = $(e).data("cluster-id");
    swal.fire({
        title: '确认删除?',
        text: '删除集群会删除所有与此相关的数据和资源，谨慎操作!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            axios.delete(CLUSTER_API + "/" + id)
                .then(response => {
                    $('#cluster-list-fragment').load(CLUSTER_LIST_VIEWS + "?action=list&refresh=true", function () {

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