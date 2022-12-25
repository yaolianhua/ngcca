$(function () {
    templatePaging();
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
function templatePaging() {
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

function ok(response) {
    console.log(response);
    toastr.success('操作成功!')
}

function fail(error) {
    console.log(error);
    toastr.error('操作失败[' + error.response.data.message + ']');
}

//template save
function templateS() {
    let data = {};
    let value = $('#template-form').serializeArray();
    $.each(value, function (index, item) {
        data[item.name] = item.value;
    });
    // Send a POST request
    axios({
        method: 'post',
        url: '/administrator/templates',
        data: data
    }).then(function (response) {
        $('#modal-new-template').modal('hide');
        $('#templates-fragment').load('/administrator/template-manage?action=list', function () {
            templatePaging();
        });

        ok(response);
    }).catch(function (error) {
        fail(error);
    });
}

//template edit
function templateES() {
    let data = {};
    let value = $('#template-edit-form').serializeArray();
    $.each(value, function (index, item) {
        data[item.name] = item.value;
    });
    // Send a POST request
    axios({
        method: 'put',
        url: '/administrator/templates',
        data: data
    }).then(function (response) {
        $('#templates-fragment').load('/administrator/template-manage?action=list', function () {
            templatePaging();
        });

        ok(response);
    }).catch(function (error) {
        fail(error);
    });
}

//template edit view
function templateEP(id) {
    $('#templates-fragment').load('/administrator/template-manage?action=edit&id=' + id, function () {

    });
}

//template detail view
function templateDP(id, endpoint) {
    $('#templates-fragment').load('/administrator/template-manage?action=detail&id=' + id, function () {
        dropzone(id, endpoint);
    });
}

//template list
function templates() {
    $('#templates-fragment').load('/administrator/template-manage?action=list', function () {
        templatePaging();
    });
}

//template delete
function templateD(id) {
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
            axios.delete('/administrator/templates/' + id)
                .then(response => {
                    $('#templates-fragment').load('/administrator/template-manage?action=list', function () {
                        templatePaging();
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