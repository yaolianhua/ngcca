//初始化常量
const USER_LIST_VIEWS = "/administrator/system/user-manage?action=list";
const USER_EDIT_VIEWS = "/administrator/system/user-manage?action=edit&id=";

$(function () {
    userlistpage();
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
function userlistpage() {
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

//user save
function saveuser() {
    let data = {};
    let value = $('#user-form').serializeArray();
    $.each(value, function (index, item) {
        data[item.name] = item.value;
    });
    // Send a POST request
    axios({
        method: 'post',
        url: USER_API,
        data: data
    }).then(function (response) {
        $('#modal-new-user').modal('hide');
        $('#users-fragment').load(USER_LIST_VIEWS, function () {
            userPaging();
        });

        ok(response);
    }).catch(function (error) {
        fail(error);
    });
}

//user edit save
function submituseredit() {
    let data = {};
    let value = $('#user-edit-form').serializeArray();
    $.each(value, function (index, item) {
        data[item.name] = item.value;
    });
    // Send a POST request
    axios({
        method: 'put',
        url: USER_API,
        data: data
    }).then(function (response) {
        $('#users-fragment').load(USER_LIST_VIEWS, function () {
            userPaging();
        });

        ok(response);
    }).catch(function (error) {
        fail(error);
    });
}

//user edit view
function usereditpage(e) {
    let id = $(e).data("user-id");
    $('#users-fragment').load(USER_EDIT_VIEWS + id, function () {

    });
}

//user list
function userlist() {
    $('#users-fragment').load(USER_LIST_VIEWS, function () {
        userlistpage();
    });
}

//user delete
function deleteuser(e) {
    let id = $(e).data("user-id");
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
            axios.delete(USER_API + "/" + id)
                .then(response => {
                    $('#users-fragment').load(USER_LIST_VIEWS, function () {
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

//user enable
function enableuser(e) {
    let user = $(e).data("username");
    axios.put(USER_API + "/" + user + '/true')
        .then(response => {
            $('#users-fragment').load(USER_LIST_VIEWS, function () {
                userPaging();
            });
            ok(response);
        })
        .catch(error => {
            fail(error);
        });
}

//user disable
function disableuser(e) {
    let user = $(e).data("username");
    axios.put(USER_API + "/" + user + '/false')
        .then(response => {
            $('#users-fragment').load(USER_LIST_VIEWS, function () {
                userPaging();
            });
            ok(response);
        })
        .catch(error => {
            fail(error);
        });
}