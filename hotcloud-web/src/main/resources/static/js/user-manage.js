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
        dropzone(id);
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

//Get authorization from cookies
function getAuthorization() {
    let strcookie = document.cookie;//获取cookie字符串
    let arrcookie = strcookie.split("; ");//分割
    //遍历匹配
    for (let i = 0; i < arrcookie.length; i++) {
        let arr = arrcookie[i].split("=");
        if (arr[0] === "authorization") {
            return arr[1];
        }
    }
    return "";
}

function dropzone(id) {
    // DropzoneJS Demo Code Start
    Dropzone.autoDiscover = false

    // Get the template HTML and remove it from the doumenthe template HTML and remove it from the doument
    let previewNode = document.querySelector("#template")
    previewNode.id = ""
    let previewTemplate = previewNode.parentNode.innerHTML
    previewNode.parentNode.removeChild(previewNode)
    let myDropzone = new Dropzone(document.body, { // Make the whole body a dropzone
        url: "http://localhost:8080/v1/storage/upload?bucket=" + id, // Set the url
        headers: {"Authorization": 'Bearer ' + getAuthorization()},
        acceptedFiles: "image/jpg, image/png, image/gif",
        success: function (file, response) {
            alert(response.data);
        },
        thumbnailWidth: 80,
        thumbnailHeight: 80,
        parallelUploads: 20,
        previewTemplate: previewTemplate,
        autoQueue: false, // Make sure the files aren't queued until manually added
        previewsContainer: "#previews", // Define the container to display the previews
        clickable: ".fileinput-button" // Define the element that should be used as click trigger to select files.
    })

    myDropzone.on("addedfile", function (file) {
        // Hookup the start button
        file.previewElement.querySelector(".start").onclick = function () {
            myDropzone.enqueueFile(file)
        }
    })

    // Update the total progress bar
    myDropzone.on("totaluploadprogress", function (progress) {
        document.querySelector("#total-progress .progress-bar").style.width = progress + "%"
    })

    myDropzone.on("sending", function (file) {
        // Show the total progress bar when upload starts
        document.querySelector("#total-progress").style.opacity = "1"
        // And disable the start button
        file.previewElement.querySelector(".start").setAttribute("disabled", "disabled")
    })

    // Hide the total progress bar when nothing's uploading anymore
    myDropzone.on("queuecomplete", function (progress) {
        document.querySelector("#total-progress").style.opacity = "0"
    })

    // Setup the buttons for all transfers
    // The "add files" button doesn't need to be setup because the config
    // `clickable` has already been specified.
    document.querySelector("#actions .start").onclick = function () {
        myDropzone.enqueueFiles(myDropzone.getFilesWithStatus(Dropzone.ADDED))
    }
    document.querySelector("#actions .cancel").onclick = function () {
        myDropzone.removeAllFiles(true)
    }
    // DropzoneJS Demo Code End
}