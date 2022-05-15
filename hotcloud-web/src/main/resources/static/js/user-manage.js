$(function () {
    userPaging();
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
function userPaging() {
    $('#user-list').DataTable({
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
    toastr.success('操作成功!')
}

function fail(error) {
    console.log(error);
    const message = error.response.data.message;
    toastr.error('操作失败[' + message + ']');
}

//user save
function userS() {
    let data = {};
    let value = $('#user-form').serializeArray();
    $.each(value, function (index, item) {
        data[item.name] = item.value;
    });
    // Send a POST request
    axios({
        method: 'post',
        url: '/administrator/users',
        data: data
    }).then(function (response) {
        $('#modal-new-user').modal('hide');
        $('#users-fragment').load('/administrator/user-manage?action=list', function () {
            userPaging();
        });

        ok(response);
    }).catch(function (error) {
        fail(error);
    });
}

//user edit
function userES() {
    let data = {};
    let value = $('#user-edit-form').serializeArray();
    $.each(value, function (index, item) {
        data[item.name] = item.value;
    });
    // Send a POST request
    axios({
        method: 'put',
        url: '/administrator/users',
        data: data
    }).then(function (response) {
        $('#users-fragment').load('/administrator/user-manage?action=list', function () {
            userPaging();
        });

        ok(response);
    }).catch(function (error) {
        fail(error);
    });
}

//user edit view
function userEP(id) {
    $('#users-fragment').load('/administrator/user-manage?action=edit&id=' + id, function () {

    });
}

//user detail view
function userDP(id) {
    $('#users-fragment').load('/administrator/user-manage?action=detail&id=' + id, function () {

    });
}

//user list
function users() {
    $('#users-fragment').load('/administrator/user-manage?action=list', function () {
        userPaging();
    });
}

//user delete
function userD(id) {
    swal.fire({
        title: '确认删除?',
        text: '删除用户会删除所有与此用户相关的数据和资源，谨慎操作!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            axios.delete('/administrator/users/' + id)
                .then(response => {
                    $('#users-fragment').load('/administrator/user-manage?action=list', function () {
                        userPaging();
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

//user on
function userOn(user) {
    axios.put('/administrator/users/' + user + '/true')
        .then(response => {
            $('#users-fragment').load('/administrator/user-manage?action=list', function () {
                userPaging();
            });
            ok(response);
        })
        .catch(error => {
            fail(error);
        });
}

//user off
function userOff(user) {
    axios.put('/administrator/users/' + user + '/false')
        .then(response => {
            $('#users-fragment').load('/administrator/user-manage?action=list', function () {
                userPaging();
            });
            ok(response);
        })
        .catch(error => {
            fail(error);
        });
}