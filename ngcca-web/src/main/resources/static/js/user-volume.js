function volumes() {
    $('#volume-list-fragment').load(USER_VOLUMES_LIST_VIEWS + "?action=list", function () {

    });
}

function deletevolume(e) {
    let id = $(e).data("volume-id");
    swal.fire({
        title: '确认删除?',
        text: '删除数据卷会丢失持久化数据，谨慎操作!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            axios.delete(VOLUME_API + "/" + id)
                .then(response => {
                    volumes();
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

function createvolume() {
    let name = $('#volume-name').val();
    let gigabytes = $('#volume-capacity').val();

    //Send a POST request
    axios({
        method: 'post',
        url: VOLUME_API,
        data: {
            "name": name,
            "gigabytes": gigabytes
        }
    }).then(function (response) {
        ok(response);
        $('#modal-create-volume').modal('hide');
        volumes();
    }).catch(function (error) {
        fail(error);
    });
}
