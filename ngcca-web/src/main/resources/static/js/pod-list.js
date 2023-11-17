

let intervalId;

function loadpods() {
    $('#pod-list-fragment').load(POD_LIST_VIEWS + "?action=list", function () {
        podlist();
    });
}

function podlist() {
    //分页
    $('#pod-list').DataTable({
        "paging": true,
        "lengthChange": false,
        "searching": true,
        "ordering": true,
        "info": true,
        "autoWidth": true,
        "responsive": true,
    });
}
$(function () {

    podlist();
    intervalId = setInterval('loadpods()', 10000);

});

function showsvc(e) {
    let svc = $(e).data("svc-show");
    animate_swal.fire({
        title: "服务端口信息",
        html: svc
    })
}

function podlog(agentUrl, namespace, pod) {
    $('#modal-codemirror-text').modal('show');
    axios.get(POD_API + "/" + namespace + '/' + pod + "/log?tail=500&agentUrl=" + agentUrl)
        .then(response => {
            // Populate data into table
            codemirror_text.setValue(response.data)
            codemirror_text.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function podyaml(agentUrl, namespace, pod) {
    $('#modal-codemirror-yaml').modal('show');
    axios.get(POD_API + "/" + namespace + '/' + pod + "/yaml?agentUrl=" + agentUrl)
        .then(response => {
            // Populate data into table
            codemirror_yaml.setValue(response.data)
            codemirror_yaml.refresh();
        })
        .catch(error => {
            fail(error);
        });
}