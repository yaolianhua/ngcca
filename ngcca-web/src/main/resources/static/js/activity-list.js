
$(function () {
    pagelist();
});
function pagelist() {
    //分页
    $('#activity-list').DataTable({
        "paging": true,
        "lengthChange": false,
        "searching": true,
        "ordering": true,
        "info": true,
        "autoWidth": true,
        "responsive": true,
    });
}

function activityempty() {
    swal.fire({
        title: '确认删除?',
        text: '所有活动日志都会被清除，谨慎操作!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            axios.delete(ACTIVITY_API)
                .then(response => {
                    window.location.href = ACTIVITY_VIEWS;
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
