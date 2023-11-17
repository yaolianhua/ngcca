const CURRENT_USERNAME = $('#current-username').data('current-username');
//avatar save
let avatar;

function avatarSave(e) {
    if (isEmpty(avatar)) {
        alertWarn('请上传头像');
        return;
    }
    let userid = $(e).data("user-id");
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

//avatar upload init
dropzone(CURRENT_USERNAME);

function dropzone(username) {
    // DropzoneJS Demo Code Start
    Dropzone.autoDiscover = false;

    // Get the template HTML and remove it from the doumenthe template HTML and remove it from the doument
    let previewNode = document.querySelector("#template");
    previewNode.id = "";
    let previewTemplate = previewNode.parentNode.innerHTML;
    previewNode.parentNode.removeChild(previewNode);
    let myDropzone = new Dropzone(document.body, { // Make the whole body a dropzone
        url: WEB_ENDPOINT + FILE_UPLOAD_API + "?bucket=" + username + "&public=true", // Set the url
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
