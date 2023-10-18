const POD_API = "/v1/kubernetes/pods";
const POD_LIST_VIEWS = "/administrator/cluster/pod?action=list";

let logcodemirror;
let yamlcodemirror;
let intervalId;

function loadPods() {
    $('#pod-list-fragment').load(POD_LIST_VIEWS, function () {
        pagePodList();
    });
}

function pagePodList() {
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

    pagePodList();
    // log CodeMirror
    logcodemirror = CodeMirror.fromTextArea(document.getElementById("codemirror-log"), {
        mode: "text",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });

    // yaml CodeMirror
    yamlcodemirror = CodeMirror.fromTextArea(document.getElementById("codemirror-yaml"), {
        mode: "yaml",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });

    intervalId = setInterval('loadPods()', 10000);

});

function showsvc(e) {
    let svc = $(e).data("svc-show");
    animate_swal.fire({
        title: "服务端口信息",
        html: svc
    })
}

function logs(agentUrl, namespace, pod) {
    $('#modal-pod-log').modal('show');
    axios.get(POD_API + "/" + namespace + '/' + pod + "/log?tail=500&agentUrl=" + agentUrl)
        .then(response => {
            // Populate data into table
            logcodemirror.setValue(response.data)
            logcodemirror.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function yaml(agentUrl, namespace, pod) {
    $('#modal-pod-yaml').modal('show');
    axios.get(POD_API + "/" + namespace + '/' + pod + "/yaml?agentUrl=" + agentUrl)
        .then(response => {
            // Populate data into table
            yamlcodemirror.setValue(response.data)
            yamlcodemirror.refresh();
        })
        .catch(error => {
            fail(error);
        });
}