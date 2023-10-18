const NODE_LIST_VIEWS = "/administrator/cluster/node?action=list";
function showlabels(e) {
    let labels = $(e).data("node-label");
    animate_swal.fire({
        title: "节点标签",
        html: labels
    })
}

let intervalId;
$(function () {
    intervalId = setInterval('listNode()', 10000);
})

function listNode() {
    $('#node-list-fragment').load(NODE_LIST_VIEWS, function () {

    });
}

