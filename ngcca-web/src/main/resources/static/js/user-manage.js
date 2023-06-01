//初始化常量
const USER_API = "/v1/security/users";
const AVATAR_UPLOAD = "/v1/files/upload?bucket=avatar";
const USER_LIST_VIEWS = "/administrator/user-manage?action=list";
const USER_EDIT_VIEWS = "/administrator/user-manage?action=edit&id=";
const USER_DETAIL_VIEWS = "/administrator/user-manage?action=detail&id=";

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
function userEP(id) {
    $('#users-fragment').load(USER_EDIT_VIEWS + id, function () {

    });
}

//user detail view
function userDP(id, endpoint) {
    $('#users-fragment').load(USER_DETAIL_VIEWS + id, function () {
        dropzone(id, endpoint);
    });
}

//user list
function users() {
    $('#users-fragment').load(USER_LIST_VIEWS, function () {
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

//user on
function userOn(user) {
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

//user off
function userOff(user) {
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

//user avatar save
let avatar;

function useravatarS() {
    if (avatar === '' || avatar === undefined || avatar === null || avatar === 'null') {
        fail('请上传头像');
        return;
    }
    let userid = $('#bind-userid').data("userid");
    let data = {
        "id": userid,
        "avatar": avatar
    };

    // Send a PUT request
    axios({
        method: 'put',
        url: USER_API,
        data: data
    }).then(function (response) {
        $('#modal-upload-avatar').modal('hide');
        ok(response);
    }).catch(function (error) {
        fail(error);
    });

}

function dropzone(id, endpoint) {
    // DropzoneJS Demo Code Start
    Dropzone.autoDiscover = false;

    // Get the template HTML and remove it from the doumenthe template HTML and remove it from the doument
    let previewNode = document.querySelector("#template");
    previewNode.id = "";
    let previewTemplate = previewNode.parentNode.innerHTML;
    previewNode.parentNode.removeChild(previewNode);
    let myDropzone = new Dropzone(document.body, { // Make the whole body a dropzone
        url: endpoint + AVATAR_UPLOAD, // Set the url
        headers: {"Authorization": 'Bearer ' + getAuthorization()},
        acceptedFiles: "image/jpg, image/png, image/gif",
        success: function (file, response) {
            avatar = response.data;
        },
        thumbnailWidth: 80,
        thumbnailHeight: 80,
        parallelUploads: 20,
        previewTemplate: previewTemplate,
        autoQueue: false, // Make sure the files aren't queued until manually added
        previewsContainer: "#previews", // Define the container to display the previews
        clickable: ".fileinput-button" // Define the element that should be used as click trigger to select files.
    });

    myDropzone.on("addedfile", function (file) {
        // Hookup the start button
        file.previewElement.querySelector(".start").onclick = function () {
            myDropzone.enqueueFile(file);
        }
    });

    // Update the total progress bar
    myDropzone.on("totaluploadprogress", function (progress) {
        document.querySelector("#total-progress .progress-bar").style.width = progress + "%";
    });

    myDropzone.on("sending", function (file) {
        // Show the total progress bar when upload starts
        document.querySelector("#total-progress").style.opacity = "1";
        // And disable the start button
        file.previewElement.querySelector(".start").setAttribute("disabled", "disabled");
    });

    // Hide the total progress bar when nothing's uploading anymore
    myDropzone.on("queuecomplete", function (progress) {
        document.querySelector("#total-progress").style.opacity = "0";
    });

    // Setup the buttons for all transfers
    // The "add files" button doesn't need to be setup because the config
    // `clickable` has already been specified.
    /*document.querySelector("#actions .start").onclick = function () {
        myDropzone.enqueueFiles(myDropzone.getFilesWithStatus(Dropzone.ADDED));
    };*/
    /*document.querySelector("#actions .cancel").onclick = function () {
        myDropzone.removeAllFiles(true);
    };*/
    // DropzoneJS Demo Code End
}