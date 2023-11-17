//初始化常量

const TEMPLATE_DEFINITION_LIST_VIEWS = "/administrator/template-manage";

$(function () {
    templatedefinitionpage();
    toastr.options = {
        "timeOut": "3000"
    };
});

const swal = Swal.mixin({
    customClass: {
        confirmButton: 'btn btn-success',
        cancelButton: 'btn btn-danger'
    },
    buttonsStyling: false
})

//dataTable init
function templatedefinitionpage() {
    $('#template-list').DataTable({
        "paging": true,
        "lengthChange": false,
        "searching": false,
        "ordering": true,
        "info": true,
        "autoWidth": true,
        "responsive": true,
    });
}

//template save
function savetemplatedefinition() {
    let data = {};
    let value = $('#template-form').serializeArray();
    $.each(value, function (index, item) {
        data[item.name] = item.value;
    });
    // Send a POST request
    axios({
        method: 'post',
        url: TEMPLATE_DEFINITION_API,
        data: data
    }).then(function (response) {
        $('#modal-new-template').modal('hide');
        $('#templates-fragment').load(TEMPLATE_DEFINITION_LIST_VIEWS + "?action=list", function () {
            templatedefinitionpage();
        });

        ok(response);
    }).catch(function (error) {
        fail(error);
    });
}

//template delete
function deletetemplatedefinition(e) {
    let id = $(e).data("template-definition-id");
    swal.fire({
        title: '确认删除?',
        text: '删除模板会导致实例模板数据不可用，若要使用需要再次添加，请谨慎！',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            axios.delete(TEMPLATE_DEFINITION_API + '/' + id)
                .then(response => {
                    $('#templates-fragment').load(TEMPLATE_DEFINITION_LIST_VIEWS + "?action=list", function () {
                        templatedefinitionpage();
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