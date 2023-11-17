//初始化常量

let intervalId;

$(function () {
    toastr.options = {
        "timeOut": "3000"
    };
    intervalId = setInterval('templateinstancesrefresh()', 5000);

    //tooltip
    $('#instance-msg-tooltip').tooltip();
});

function showtemplateinstanceyaml(e) {
    $('#modal-codemirror-yaml').modal('show');
    let id = $(e).data("instance-id");
    axios.get(TEMPLATE_INSTANCE_API + "/" + id)
        .then(response => {
            // Populate data into table
            codemirror_yaml.setValue(response.data.yaml)
            codemirror_yaml.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function showtemplateinstanceingressyaml(e) {
    $('#modal-codemirror-yaml').modal('show');
    let id = $(e).data("instance-id");
    axios.get(TEMPLATE_INSTANCE_API + "/" + id)
        .then(response => {
            // Populate data into table
            codemirror_yaml.setValue(response.data.ingress)
            codemirror_yaml.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

//user template instance list
function templateinstancesrefresh() {
    $('#template-instance-list-fragment').load(TEMPLATE_INSTANCE_LIST_VIEWS + "?action=list", function () {

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
                    $('#template-instance-list-fragment').load(TEMPLATE_INSTANCE_LIST_VIEWS + "?action=list", function () {

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