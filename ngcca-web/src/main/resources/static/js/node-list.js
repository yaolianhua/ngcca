function showlabels(e) {
    let labels = $(e).data("node-label");
    animate_swal.fire({
        title: "节点标签",
        html: labels
    })
}

