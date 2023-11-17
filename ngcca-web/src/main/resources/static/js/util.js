//swal
const animate_swal = Swal.mixin({
    showClass: {
        popup: 'animate__animated animate__fadeInDown'
    },
    hideClass: {
        popup: 'animate__animated animate__fadeOutUp'
    }
})

function ok(response) {
    console.log(response);
    toastr.success('操作成功')
}

function fail(error) {
    console.log(error);
    toastr.error(error.response.data.message);
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