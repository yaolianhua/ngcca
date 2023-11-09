//
const WEB_ENDPOINT = $('#server-endpoint').data('server-endpoint');
const CURRENT_USERNAME = $('#current-username').data('current-username');

axios.defaults.baseURL = WEB_ENDPOINT;
axios.defaults.withCredentials = true;

// Request interceptors for API calls
axios.interceptors.request.use(
    config => {
        config.headers['Authorization'] = `Bearer ${getAuthorization()}`;
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);


//
let codemirror_text;
let codemirror_yaml;

// init codemirror
$(function () {

    // text CodeMirror
    codemirror_text = CodeMirror.fromTextArea(document.getElementById("codemirror-textarea"), {
        mode: "text",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });

    // yaml CodeMirror
    codemirror_yaml = CodeMirror.fromTextArea(document.getElementById("codemirror-yaml"), {
        mode: "yaml",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });

});

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

function ok(response) {
    console.log(response);
    toastr.success('操作成功!')
}

function fail(error) {
    console.log(error);
    toastr.error('操作失败[' + error.response.data.message + ']');
}

function isEmpty(str) {
    return str === "" ||
        str === "null" ||
        str === "undefined" ||
        str == null;
}

function alertWarn(msg) {
    animate_swal.fire({
        icon: 'warning',
        html: msg
    })
}

function alertInfo(msg) {
    animate_swal.fire({
        icon: 'info',
        html: msg
    })
}

const animate_swal = Swal.mixin({
    showClass: {
        popup: 'animate__animated animate__fadeInDown'
    },
    hideClass: {
        popup: 'animate__animated animate__fadeOutUp'
    }
})

//user avatar save
let avatar;
const FILE_UPLOAD_API = "/v1/files/upload";
const USER_API = "/v1/security/users";

function avatarSave(e) {
    if (avatar === '' || avatar === undefined || avatar === null || avatar === 'null') {
        alert('请上传头像');
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

function dropzone(username) {
    // DropzoneJS Demo Code Start
    Dropzone.autoDiscover = false;

    // Get the template HTML and remove it from the doumenthe template HTML and remove it from the doument
    let previewNode = document.querySelector("#template");
    previewNode.id = "";
    let previewTemplate = previewNode.parentNode.innerHTML;
    previewNode.parentNode.removeChild(previewNode);
    let myDropzone = new Dropzone(document.body, { // Make the whole body a dropzone
        url: WEB_ENDPOINT + FILE_UPLOAD_API + "?bucket=" + username, // Set the url
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
