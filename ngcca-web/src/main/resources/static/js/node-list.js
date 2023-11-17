
function showlabels(e) {
    let labels = $(e).data("node-label");
    animate_swal.fire({
        title: "节点标签",
        html: labels
    })
}

let intervalId;
$(function () {
    intervalId = setInterval('loadNodes()', 10000);
})

function loadNodes() {
    $('#node-list-fragment').load(NODE_LIST_VIEWS + "?action=list", function () {

    });
}

